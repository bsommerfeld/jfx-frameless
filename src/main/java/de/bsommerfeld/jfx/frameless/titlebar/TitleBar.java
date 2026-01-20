package de.bsommerfeld.jfx.frameless.titlebar;

import de.bsommerfeld.jfx.frameless.controls.MacOSControls;
import de.bsommerfeld.jfx.frameless.controls.WindowControls;
import de.bsommerfeld.jfx.frameless.controls.WindowsControls;
import de.bsommerfeld.jfx.frameless.platform.OperatingSystem;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/**
 * Draggable title bar with platform-aware window controls.
 * Supports custom center content, left/right slots, and double-click maximize.
 *
 * <p>
 * Platform behavior:
 * <ul>
 * <li>macOS: Traffic lights on left, content centered, optional right slot</li>
 * <li>Windows: Optional left slot, content centered, Fluent controls on
 * right</li>
 * </ul>
 */
public class TitleBar extends HBox {

    private static final double DEFAULT_HEIGHT = 38.0;

    private final Stage stage;
    private final HBox leftSlot;
    private final HBox centerSlot;
    private final HBox rightSlot;

    private double xOffset;
    private double yOffset;

    /**
     * Creates a title bar for the given stage with platform-appropriate controls.
     */
    public TitleBar(Stage stage) {
        this.stage = stage;

        getStyleClass().add("title-bar");
        setMinHeight(DEFAULT_HEIGHT);
        setPrefHeight(DEFAULT_HEIGHT);
        setMaxHeight(DEFAULT_HEIGHT);
        setAlignment(Pos.CENTER);
        setFillHeight(true);
        setPickOnBounds(true);

        leftSlot = new HBox();
        leftSlot.setAlignment(Pos.CENTER_LEFT);

        centerSlot = new HBox(4);
        centerSlot.setAlignment(Pos.CENTER);

        rightSlot = new HBox();
        rightSlot.setAlignment(Pos.CENTER_RIGHT);

        // True center alignment: both slots share the maximum width of either,
        // preventing asymmetric content (e.g., macOS vs Windows controls) from
        // displacing the center slot.
        NumberBinding maxSlotWidth = Bindings.max(
                leftSlot.widthProperty(), rightSlot.widthProperty());
        leftSlot.minWidthProperty().bind(maxSlotWidth);
        rightSlot.minWidthProperty().bind(maxSlotWidth);

        Region leftSpacer = createSpacer();
        Region rightSpacer = createSpacer();

        getChildren().addAll(leftSlot, leftSpacer, centerSlot, rightSpacer, rightSlot);

        setupDragBehavior();
        setupDoubleClickMaximize();
    }

    /**
     * Adds platform-native window controls to the appropriate slot.
     */
    public TitleBar withPlatformControls() {
        WindowControls.WindowActions actions = createWindowActions();

        if (OperatingSystem.current().isMacOS()) {
            MacOSControls controls = new MacOSControls(actions);
            controls.bindToStage(stage);
            leftSlot.getChildren().add(0, controls);
        } else {
            WindowsControls controls = new WindowsControls(actions);
            controls.maximizedProperty().bind(stage.maximizedProperty());
            rightSlot.getChildren().add(controls);
        }

        return this;
    }

    /**
     * Adds custom controls to the left slot.
     */
    public TitleBar withLeftContent(Node... nodes) {
        leftSlot.getChildren().addAll(nodes);
        return this;
    }

    /**
     * Adds custom controls to the right slot.
     */
    public TitleBar withRightContent(Node... nodes) {
        rightSlot.getChildren().addAll(nodes);
        return this;
    }

    /**
     * Sets the center content (replaces existing).
     */
    public TitleBar withCenterContent(Node... nodes) {
        centerSlot.getChildren().setAll(nodes);
        return this;
    }

    /**
     * Adds a title label bound to the stage's title property.
     * Title updates automatically when stage title changes.
     */
    public TitleBar bindTitle() {
        Label titleLabel = new Label();
        titleLabel.textProperty().bind(stage.titleProperty());
        titleLabel.getStyleClass().add("title-bar-title");
        centerSlot.getChildren().add(titleLabel);
        return this;
    }

    /**
     * Adds a static title label.
     */
    public TitleBar withTitle(String title) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("title-bar-title");
        centerSlot.getChildren().add(titleLabel);
        return this;
    }

    /**
     * Adds an icon from the stage's icon list.
     * Binds to the first icon in stage.getIcons() if present.
     * Updates automatically when stage icons change.
     */
    public TitleBar bindIcon() {
        javafx.scene.image.ImageView iconView = new javafx.scene.image.ImageView();
        iconView.setFitWidth(16);
        iconView.setFitHeight(16);
        iconView.setPreserveRatio(true);
        iconView.getStyleClass().add("title-bar-icon");

        // Bind to first icon if available
        updateBoundIcon(iconView);
        stage.getIcons().addListener((javafx.collections.ListChangeListener<javafx.scene.image.Image>) change -> {
            updateBoundIcon(iconView);
        });

        centerSlot.getChildren().add(0, iconView);
        return this;
    }

    private void updateBoundIcon(javafx.scene.image.ImageView iconView) {
        if (!stage.getIcons().isEmpty()) {
            iconView.setImage(stage.getIcons().getFirst());
            iconView.setVisible(true);
            iconView.setManaged(true);
        } else {
            iconView.setImage(null);
            iconView.setVisible(false);
            iconView.setManaged(false);
        }
    }

    /**
     * Adds a static icon node to the center slot.
     * No-op if icon is null.
     */
    public TitleBar withIcon(Node icon) {
        if (icon != null) {
            icon.getStyleClass().add("title-bar-icon");
            centerSlot.getChildren().add(0, icon);
        }
        return this;
    }

    /**
     * Gets the left slot for direct manipulation.
     */
    public HBox getLeftSlot() {
        return leftSlot;
    }

    /**
     * Gets the center slot for direct manipulation.
     */
    public HBox getCenterSlot() {
        return centerSlot;
    }

    /**
     * Gets the right slot for direct manipulation.
     */
    public HBox getRightSlot() {
        return rightSlot;
    }

    private WindowControls.WindowActions createWindowActions() {
        return new WindowControls.WindowActions() {
            @Override
            public void close() {
                stage.close();
            }

            @Override
            public void minimize() {
                stage.setIconified(true);
            }

            @Override
            public void maximize() {
                if (OperatingSystem.current().isMacOS()) {
                    stage.setFullScreen(!stage.isFullScreen());
                } else {
                    stage.setMaximized(!stage.isMaximized());
                }
            }
        };
    }

    private void setupDragBehavior() {
        setOnMousePressed(event -> {
            if (!stage.isMaximized() && !stage.isFullScreen()) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });

        setOnMouseDragged(event -> {
            if (!stage.isMaximized() && !stage.isFullScreen()) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });
    }

    private void setupDoubleClickMaximize() {
        setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                stage.setMaximized(!stage.isMaximized());
            }
        });
    }

    private Region createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
}
