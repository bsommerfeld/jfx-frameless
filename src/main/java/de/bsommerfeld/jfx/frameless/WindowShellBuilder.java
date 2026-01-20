package de.bsommerfeld.jfx.frameless;

import de.bsommerfeld.jfx.frameless.titlebar.TitleBar;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * Builder for creating pre-configured {@link WindowShell} instances.
 *
 * <p>
 * Example:
 * 
 * <pre>{@code
 * WindowShell window = WindowShellBuilder.create(stage)
 *         .defaultTitleBar()
 *         .resizable()
 *         .minSize(400, 300)
 *         .content(myContentNode)
 *         .build();
 *
 * window.show();
 * }</pre>
 */
public class WindowShellBuilder {

    private final Stage stage;
    private boolean useDefaultTitleBar;
    private TitleBar customTitleBar;
    private boolean resizable;
    private double resizeMargin = 6.0;
    private double minWidth = 200;
    private double minHeight = 150;
    private double width = 800;
    private double height = 600;
    private double cornerRadius = 12.0;
    private Node content;
    private Node leftSidebar;
    private Node rightSidebar;
    private Node footer;
    private Node backgroundLayer;
    private final List<String> stylesheets = new ArrayList<>();

    private WindowShellBuilder(Stage stage) {
        this.stage = stage;
    }

    /**
     * Creates a new builder for the given stage.
     */
    public static WindowShellBuilder create(Stage stage) {
        return new WindowShellBuilder(stage);
    }

    /**
     * Adds a default title bar with platform controls and window title.
     */
    public WindowShellBuilder defaultTitleBar() {
        this.useDefaultTitleBar = true;
        this.customTitleBar = null;
        return this;
    }

    /**
     * Adds a custom title bar.
     */
    public WindowShellBuilder titleBar(TitleBar titleBar) {
        this.customTitleBar = titleBar;
        this.useDefaultTitleBar = false;
        return this;
    }

    /**
     * Enables edge-based resizing with default 6px margin.
     */
    public WindowShellBuilder resizable() {
        this.resizable = true;
        return this;
    }

    /**
     * Enables edge-based resizing with custom margin.
     */
    public WindowShellBuilder resizable(double margin) {
        this.resizable = true;
        this.resizeMargin = margin;
        return this;
    }

    /**
     * Sets minimum window dimensions.
     */
    public WindowShellBuilder minSize(double width, double height) {
        this.minWidth = width;
        this.minHeight = height;
        return this;
    }

    /**
     * Sets initial window dimensions.
     */
    public WindowShellBuilder size(double width, double height) {
        this.width = width;
        this.height = height;
        return this;
    }

    /**
     * Sets corner radius for rounded window edges.
     */
    public WindowShellBuilder cornerRadius(double radius) {
        this.cornerRadius = radius;
        return this;
    }

    /**
     * Disables rounded corners.
     */
    public WindowShellBuilder squareCorners() {
        this.cornerRadius = 0;
        return this;
    }

    /**
     * Sets the main content.
     */
    public WindowShellBuilder content(Node content) {
        this.content = content;
        return this;
    }

    /**
     * Sets a left sidebar.
     */
    public WindowShellBuilder leftSidebar(Node sidebar) {
        this.leftSidebar = sidebar;
        return this;
    }

    /**
     * Sets a right sidebar.
     */
    public WindowShellBuilder rightSidebar(Node sidebar) {
        this.rightSidebar = sidebar;
        return this;
    }

    /**
     * Sets a footer/bottom area.
     */
    public WindowShellBuilder footer(Node footer) {
        this.footer = footer;
        return this;
    }

    /**
     * Adds a background layer behind the layout.
     */
    public WindowShellBuilder backgroundLayer(Node layer) {
        this.backgroundLayer = layer;
        return this;
    }

    /**
     * Adds a stylesheet by classpath reference. Can be called multiple times.
     */
    public WindowShellBuilder stylesheet(String cssPath) {
        this.stylesheets.add(cssPath);
        return this;
    }

    /**
     * Builds the configured WindowShell.
     */
    public WindowShell build() {
        WindowShell shell = new WindowShell(stage);

        if (useDefaultTitleBar) {
            shell.withDefaultTitleBar();
        } else if (customTitleBar != null) {
            shell.withTitleBar(customTitleBar);
        }

        if (resizable) {
            shell.withResizable(resizeMargin);
        }

        shell.withMinSize(minWidth, minHeight);
        shell.withSize(width, height);
        shell.withCornerRadius(cornerRadius);

        if (content != null) {
            shell.withContent(content);
        }

        if (leftSidebar != null) {
            shell.withLeftSidebar(leftSidebar);
        }

        if (rightSidebar != null) {
            shell.withRightSidebar(rightSidebar);
        }

        if (footer != null) {
            shell.withFooter(footer);
        }

        if (backgroundLayer != null) {
            shell.withBackgroundLayer(backgroundLayer);
        }

        for (String stylesheet : stylesheets) {
            shell.withStylesheet(stylesheet);
        }

        return shell;
    }
}
