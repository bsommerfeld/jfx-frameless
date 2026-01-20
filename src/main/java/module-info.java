/**
 * JFX Frameless - Modular window shell toolkit for JavaFX.
 *
 * <p>
 * Core components:
 * <ul>
 * <li>{@code WindowShell} - Transparent stage wrapper with rounded corners</li>
 * <li>{@code TitleBar} - Platform-aware draggable title bar</li>
 * <li>{@code WindowControls} - macOS traffic lights / Windows Fluent
 * buttons</li>
 * <li>{@code ResizeBehavior} - Edge-based window resizing</li>
 * </ul>
 */
module de.bsommerfeld.jfx.frameless {
    requires javafx.controls;
    requires javafx.graphics;

    exports de.bsommerfeld.jfx.frameless;
    exports de.bsommerfeld.jfx.frameless.titlebar;
    exports de.bsommerfeld.jfx.frameless.controls;
    exports de.bsommerfeld.jfx.frameless.resize;
    exports de.bsommerfeld.jfx.frameless.platform;
    exports de.bsommerfeld.jfx.frameless.util;
}
