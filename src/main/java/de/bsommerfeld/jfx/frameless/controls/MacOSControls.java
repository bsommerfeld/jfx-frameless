package de.bsommerfeld.jfx.frameless.controls;

import de.bsommerfeld.jfx.frameless.util.SvgLoader;
import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * macOS-style traffic light window controls.
 * Red (close), yellow (minimize), green (zoom/fullscreen) positioned left.
 * Icons appear on hover over the button group.
 */
public class MacOSControls extends WindowControls {

    private static final double BUTTON_SIZE = 12.0;
    private static final double BUTTON_SPACING = 8.0;

    private final Button closeButton;
    private final Button minimizeButton;
    private final Button zoomButton;

    public MacOSControls(WindowActions actions) {
        super(actions);

        getStyleClass().add("macos-controls");
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(BUTTON_SPACING);

        closeButton = createButton("close", "traffic-light-close", "/icons/macos-close.svg");
        closeButton.setOnAction(e -> actions.close());

        minimizeButton = createButton("minimize", "traffic-light-minimize", "/icons/macos-minimize.svg");
        minimizeButton.setOnAction(e -> actions.minimize());

        zoomButton = createButton("zoom", "traffic-light-zoom", "/icons/macos-zoom.svg");
        zoomButton.setOnAction(e -> actions.maximize());

        getChildren().addAll(closeButton, minimizeButton, zoomButton);

        setupHoverBehavior();
    }

    private Button createButton(String id, String styleClass, String iconPath) {
        Button button = new Button();
        button.setId("macos-" + id);
        button.getStyleClass().addAll("traffic-light-button", styleClass);

        button.setMinSize(BUTTON_SIZE, BUTTON_SIZE);
        button.setPrefSize(BUTTON_SIZE, BUTTON_SIZE);
        button.setMaxSize(BUTTON_SIZE, BUTTON_SIZE);
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        SVGPath icon = new SVGPath();
        icon.setContent(SvgLoader.load(iconPath));
        icon.getStyleClass().add("traffic-light-icon");
        icon.setOpacity(0);

        // Center the icon within the button using a StackPane wrapper
        StackPane iconWrapper = new StackPane(icon);
        iconWrapper.setAlignment(Pos.CENTER);
        iconWrapper.setMinSize(BUTTON_SIZE, BUTTON_SIZE);
        iconWrapper.setMaxSize(BUTTON_SIZE, BUTTON_SIZE);

        button.setGraphic(iconWrapper);
        button.setFocusTraversable(false);

        return button;
    }

    private void setupHoverBehavior() {
        setOnMouseEntered(e -> setIconsVisible(true));
        setOnMouseExited(e -> setIconsVisible(false));
    }

    private void setIconsVisible(boolean visible) {
        double targetOpacity = visible ? 1.0 : 0.0;
        animateIcon(closeButton, targetOpacity);
        animateIcon(minimizeButton, targetOpacity);
        animateIcon(zoomButton, targetOpacity);
    }

    private void animateIcon(Button button, double targetOpacity) {
        StackPane wrapper = (StackPane) button.getGraphic();
        SVGPath icon = (SVGPath) wrapper.getChildren().getFirst();
        FadeTransition fade = new FadeTransition(Duration.millis(100), icon);
        fade.setToValue(targetOpacity);
        fade.play();
    }

    /**
     * Binds visibility to stage state, hiding controls when fullscreen or
     * maximized.
     * On macOS, native fullscreen replaces traffic lights with system menu bar
     * controls,
     * so custom controls should disappear to match that behavior.
     */
    public MacOSControls bindToStage(Stage stage) {
        ChangeListener<Boolean> stateListener = (obs, oldVal, newVal) -> {
            boolean shouldHide = stage.isFullScreen() || stage.isMaximized();
            setVisible(!shouldHide);
            setManaged(!shouldHide);
        };

        stage.fullScreenProperty().addListener(stateListener);
        stage.maximizedProperty().addListener(stateListener);
        return this;
    }
}
