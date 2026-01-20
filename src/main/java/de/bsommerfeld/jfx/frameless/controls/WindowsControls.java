package de.bsommerfeld.jfx.frameless.controls;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.shape.SVGPath;

/**
 * Windows 11 Fluent Design window controls.
 * Minimize, Maximize/Restore, Close positioned right with thin-line icons.
 */
public class WindowsControls extends WindowControls {

    private static final double BUTTON_WIDTH = 46.0;
    private static final double BUTTON_HEIGHT = 38.0;
    private static final double ICON_SCALE = 0.65;

    private static final String MINIMIZE_PATH = "M0,5 L10,5";
    private static final String MAXIMIZE_PATH = "M0,0 L10,0 L10,10 L0,10 Z";
    private static final String RESTORE_PATH = "M2,0 L10,0 L10,8 L8,8 L8,10 L0,10 L0,2 L2,2 Z M2,2 L2,8 L8,8 L8,2 Z";
    private static final String CLOSE_PATH = "M0,0 L10,10 M10,0 L0,10";

    private final Button maximizeButton;
    private final BooleanProperty maximizedProperty = new SimpleBooleanProperty(false);

    public WindowsControls(WindowActions actions) {
        super(actions);

        getStyleClass().add("windows-controls");
        setAlignment(Pos.CENTER_RIGHT);
        setSpacing(0);

        Button minimizeBtn = createButton("minimize", MINIMIZE_PATH, false);
        minimizeBtn.setOnAction(e -> actions.minimize());

        maximizeButton = createButton("maximize", MAXIMIZE_PATH, false);
        maximizeButton.setOnAction(e -> actions.maximize());

        maximizedProperty.addListener((obs, wasMax, isMax) -> updateMaximizeIcon(isMax));

        Button closeBtn = createButton("close", CLOSE_PATH, true);
        closeBtn.setOnAction(e -> actions.close());

        getChildren().addAll(minimizeBtn, maximizeButton, closeBtn);
    }

    /**
     * Binds the maximized state to update the maximize/restore icon.
     */
    public BooleanProperty maximizedProperty() {
        return maximizedProperty;
    }

    private void updateMaximizeIcon(boolean maximized) {
        SVGPath icon = (SVGPath) maximizeButton.getGraphic();
        icon.setContent(maximized ? RESTORE_PATH : MAXIMIZE_PATH);
    }

    private Button createButton(String id, String svgPath, boolean isClose) {
        Button button = new Button();
        button.setId("window-" + id);
        button.getStyleClass().add("window-control-button");
        button.getStyleClass().add("windows-" + id);

        if (isClose) {
            button.getStyleClass().add("close-button");
        }

        button.setMinWidth(BUTTON_WIDTH);
        button.setPrefWidth(BUTTON_WIDTH);
        button.setMaxWidth(BUTTON_WIDTH);
        button.setMinHeight(BUTTON_HEIGHT);
        button.setPrefHeight(BUTTON_HEIGHT);
        button.setMaxHeight(BUTTON_HEIGHT);

        SVGPath icon = new SVGPath();
        icon.setContent(svgPath);
        icon.getStyleClass().add("window-control-icon");
        icon.setScaleX(ICON_SCALE);
        icon.setScaleY(ICON_SCALE);

        button.setGraphic(icon);
        button.setFocusTraversable(false);

        return button;
    }
}
