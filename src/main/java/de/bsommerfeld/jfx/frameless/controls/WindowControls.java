package de.bsommerfeld.jfx.frameless.controls;

import javafx.scene.layout.HBox;

/**
 * Abstract base for platform-specific window control buttons.
 * Subclasses implement close, minimize, and maximize/zoom buttons
 * with platform-native styling and behavior.
 */
public abstract class WindowControls extends HBox {

    /**
     * Callback interface for window control actions.
     */
    public interface WindowActions {
        void close();

        void minimize();

        void maximize();
    }

    protected final WindowActions actions;

    protected WindowControls(WindowActions actions) {
        this.actions = actions;
        getStyleClass().add("window-controls");
    }
}
