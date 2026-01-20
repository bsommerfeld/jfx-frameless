package de.bsommerfeld.jfx.frameless;

import de.bsommerfeld.jfx.frameless.resize.ResizeBehavior;
import de.bsommerfeld.jfx.frameless.titlebar.TitleBar;
import de.bsommerfeld.jfx.frameless.platform.OperatingSystem;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Undecorated window shell with composable features.
 * Wraps a Stage to provide transparent, frameless window functionality.
 *
 * <p>
 * Layout structure:
 * 
 * <pre>
 *   StackPane (root, rounded clip)
 *     └─ BorderPane (layout)
 *          ├─ top: TitleBar (optional)
 *          ├─ left: sidebar (optional)
 *          ├─ center: content area
 *          ├─ right: sidebar (optional)
 *          └─ bottom: footer (optional)
 * </pre>
 */
public class WindowShell {

    private static final double DEFAULT_CORNER_RADIUS = 10.0;

    private final Stage stage;
    private final StackPane root;
    private final BorderPane layout;
    private final StackPane contentPane;

    private TitleBar titleBar;
    private double cornerRadius = DEFAULT_CORNER_RADIUS;

    // State for restoring window bounds after fullscreen
    private double restoreX;
    private double restoreY;
    private double restoreW;
    private double restoreH;

    /**
     * Wraps an existing stage as frameless with transparent style.
     */
    public WindowShell(Stage stage) {
        this.stage = stage;
        stage.initStyle(StageStyle.TRANSPARENT);

        this.root = new StackPane();
        root.getStyleClass().add("window-shell-root");

        this.layout = new BorderPane();
        layout.getStyleClass().add("window-shell-layout");

        this.contentPane = new StackPane();
        contentPane.getStyleClass().add("window-shell-content");
        layout.setCenter(contentPane);

        root.getChildren().add(layout);

        Scene scene = new Scene(root, 800, 600, Color.TRANSPARENT);
        stage.setScene(scene);

        // Load base styles from library resources
        var baseStylesheet = getClass().getResource("/css/frameless.css");
        if (baseStylesheet != null) {
            scene.getStylesheets().add(baseStylesheet.toExternalForm());
        }

        applyRoundedClip();
        setupWindowStates();
    }

    /**
     * Adds a title bar to the top of the window.
     */
    public WindowShell withTitleBar(TitleBar titleBar) {
        this.titleBar = titleBar;
        layout.setTop(titleBar);
        return this;
    }

    /**
     * Creates and adds a default title bar with platform controls and bound stage
     * title.
     */
    public WindowShell withDefaultTitleBar() {
        this.titleBar = new TitleBar(stage)
                .withPlatformControls()
                .bindTitle();
        layout.setTop(titleBar);
        return this;
    }

    /**
     * Enables edge-based window resizing with default 6px margin.
     */
    public WindowShell withResizable() {
        return withResizable(6.0);
    }

    /**
     * Enables edge-based window resizing with custom margin.
     */
    public WindowShell withResizable(double margin) {
        new ResizeBehavior(stage, margin);
        return this;
    }

    /**
     * Sets minimum window dimensions.
     */
    public WindowShell withMinSize(double width, double height) {
        stage.setMinWidth(width);
        stage.setMinHeight(height);
        return this;
    }

    /**
     * Sets initial window dimensions.
     */
    public WindowShell withSize(double width, double height) {
        stage.setWidth(width);
        stage.setHeight(height);
        return this;
    }

    /**
     * Sets corner radius for the rounded clip.
     */
    public WindowShell withCornerRadius(double radius) {
        this.cornerRadius = radius;
        applyRoundedClip();
        return this;
    }

    /**
     * Disables rounded corners (square window).
     */
    public WindowShell withSquareCorners() {
        return withCornerRadius(0);
    }

    /**
     * Sets the main content of the window.
     */
    public WindowShell withContent(Node content) {
        contentPane.getChildren().setAll(content);
        return this;
    }

    /**
     * Sets the left sidebar.
     */
    public WindowShell withLeftSidebar(Node sidebar) {
        layout.setLeft(sidebar);
        return this;
    }

    /**
     * Sets the right sidebar.
     */
    public WindowShell withRightSidebar(Node sidebar) {
        layout.setRight(sidebar);
        return this;
    }

    /**
     * Sets the footer/bottom area.
     */
    public WindowShell withFooter(Node footer) {
        layout.setBottom(footer);
        return this;
    }

    /**
     * Adds a stylesheet to the scene.
     */
    public WindowShell withStylesheet(String cssPath) {
        stage.getScene().getStylesheets().add(cssPath);
        return this;
    }

    /**
     * Adds a background layer behind the layout (for glow effects, etc.).
     */
    public WindowShell withBackgroundLayer(Node layer) {
        root.getChildren().add(0, layer);
        return this;
    }

    /**
     * Shows the window.
     */
    public void show() {
        stage.show();
    }

    /**
     * Gets the underlying JavaFX stage.
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Gets the root stack pane.
     */
    public StackPane getRoot() {
        return root;
    }

    /**
     * Gets the layout border pane.
     */
    public BorderPane getLayout() {
        return layout;
    }

    /**
     * Gets the content pane.
     */
    public StackPane getContentPane() {
        return contentPane;
    }

    /**
     * Gets the title bar (null if not configured).
     */
    public TitleBar getTitleBar() {
        return titleBar;
    }

    /**
     * Gets the scene.
     */
    public Scene getScene() {
        return stage.getScene();
    }

    private void applyRoundedClip() {
        // Disable clip in full screen or maximized state to allow full edge-to-edge
        // content
        if ((stage != null && (stage.isFullScreen() || stage.isMaximized())) || cornerRadius <= 0) {
            root.setClip(null);
            return;
        }

        Rectangle clip = new Rectangle();
        clip.setArcWidth(cornerRadius * 2);
        clip.setArcHeight(cornerRadius * 2);
        clip.widthProperty().bind(root.widthProperty());
        clip.heightProperty().bind(root.heightProperty());
        root.setClip(clip);
    }

    private void setupWindowStates() {
        ChangeListener<Boolean> stateListener = (obs, oldVal, newVal) -> {
            applyRoundedClip();

            // Fix for macOS Transparent Stage FullScreen black void issue
            if (OperatingSystem.current().isMacOS()) {
                if (stage.isFullScreen()) {
                    // Save current bounds before we force them to valid values
                    // Only save if we weren't already maximized or fullscreen (prevent overwriting
                    // with wrong values)
                    if (!oldVal) {
                        restoreX = stage.getX();
                        restoreY = stage.getY();
                        restoreW = stage.getWidth();
                        restoreH = stage.getHeight();
                    }

                    // Defer to allow property change to settle and transition to start
                    javafx.application.Platform.runLater(() -> {
                        // Force stage to match screen bounds
                        Screen screen = getScreenForStage();
                        if (screen != null) {
                            Rectangle2D bounds = screen.getBounds();
                            stage.setX(bounds.getMinX());
                            stage.setY(bounds.getMinY());
                            stage.setWidth(bounds.getWidth());
                            stage.setHeight(bounds.getHeight());

                            // Also ensure root matches
                            root.setMinWidth(bounds.getWidth());
                            root.setMinHeight(bounds.getHeight());
                            root.setPrefSize(bounds.getWidth(), bounds.getHeight());
                        }
                    });
                } else {
                    // Reset to computed size
                    root.setMinWidth(Region.USE_COMPUTED_SIZE);
                    root.setMinHeight(Region.USE_COMPUTED_SIZE);
                    root.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

                    // Restore previous bounds if they exist
                    if (restoreW > 0 && restoreH > 0) {
                        stage.setX(restoreX);
                        stage.setY(restoreY);
                        stage.setWidth(restoreW);
                        stage.setHeight(restoreH);
                    }
                }
            }
        };

        stage.fullScreenProperty().addListener(stateListener);
        stage.maximizedProperty().addListener(stateListener);
    }

    private Screen getScreenForStage() {
        // Find screen that contains the center of the stage
        double centerX = stage.getX() + stage.getWidth() / 2;
        double centerY = stage.getY() + stage.getHeight() / 2;
        return Screen.getScreensForRectangle(centerX, centerY, 1, 1)
                .stream()
                .findFirst()
                .orElse(Screen.getPrimary());
    }
}
