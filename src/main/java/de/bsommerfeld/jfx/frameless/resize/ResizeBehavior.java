package de.bsommerfeld.jfx.frameless.resize;

import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Enables edge-based window resizing for undecorated stages.
 * Detects mouse position near window edges and handles drag-to-resize in all
 * directions.
 *
 * <p>
 * Attaches event filters to the scene; does not consume events unless actively
 * resizing.
 */
public class ResizeBehavior {

    private static final double DEFAULT_MARGIN = 6.0;

    private final Stage stage;
    private final Scene scene;
    private final double margin;

    private double startX;
    private double startY;
    private double startWidth;
    private double startHeight;
    private double startStageX;
    private double startStageY;

    private ResizeDirection direction = ResizeDirection.NONE;

    private enum ResizeDirection {
        NONE,
        NORTH, SOUTH, EAST, WEST,
        NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST
    }

    /**
     * Creates resize behavior with default 6px edge margin.
     */
    public ResizeBehavior(Stage stage) {
        this(stage, DEFAULT_MARGIN);
    }

    /**
     * Creates resize behavior with custom edge margin.
     */
    public ResizeBehavior(Stage stage, double margin) {
        this.stage = stage;
        this.scene = stage.getScene();
        this.margin = margin;

        if (scene != null) {
            attach();
        }
    }

    /**
     * Attaches resize handlers to the scene.
     */
    public void attach() {
        scene.addEventFilter(MouseEvent.MOUSE_MOVED, this::updateCursor);
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, this::startResize);
        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::performResize);
        scene.addEventFilter(MouseEvent.MOUSE_RELEASED, this::endResize);
    }

    private void updateCursor(MouseEvent event) {
        if (stage.isMaximized() || stage.isFullScreen()) {
            scene.setCursor(Cursor.DEFAULT);
            return;
        }

        direction = detectDirection(event.getSceneX(), event.getSceneY());
        scene.setCursor(getCursor(direction));
    }

    private ResizeDirection detectDirection(double x, double y) {
        double width = scene.getWidth();
        double height = scene.getHeight();

        boolean north = y < margin;
        boolean south = y > height - margin;
        boolean west = x < margin;
        boolean east = x > width - margin;

        if (north && west)
            return ResizeDirection.NORTH_WEST;
        if (north && east)
            return ResizeDirection.NORTH_EAST;
        if (south && west)
            return ResizeDirection.SOUTH_WEST;
        if (south && east)
            return ResizeDirection.SOUTH_EAST;
        if (north)
            return ResizeDirection.NORTH;
        if (south)
            return ResizeDirection.SOUTH;
        if (west)
            return ResizeDirection.WEST;
        if (east)
            return ResizeDirection.EAST;

        return ResizeDirection.NONE;
    }

    private Cursor getCursor(ResizeDirection dir) {
        return switch (dir) {
            case NORTH, SOUTH -> Cursor.N_RESIZE;
            case EAST, WEST -> Cursor.E_RESIZE;
            case NORTH_WEST, SOUTH_EAST -> Cursor.NW_RESIZE;
            case NORTH_EAST, SOUTH_WEST -> Cursor.NE_RESIZE;
            default -> Cursor.DEFAULT;
        };
    }

    private void startResize(MouseEvent event) {
        if (direction == ResizeDirection.NONE || stage.isMaximized()) {
            return;
        }

        startX = event.getScreenX();
        startY = event.getScreenY();
        startWidth = stage.getWidth();
        startHeight = stage.getHeight();
        startStageX = stage.getX();
        startStageY = stage.getY();

        event.consume();
    }

    private void performResize(MouseEvent event) {
        if (direction == ResizeDirection.NONE || stage.isMaximized()) {
            return;
        }

        double deltaX = event.getScreenX() - startX;
        double deltaY = event.getScreenY() - startY;

        double minWidth = stage.getMinWidth() > 0 ? stage.getMinWidth() : 200;
        double minHeight = stage.getMinHeight() > 0 ? stage.getMinHeight() : 150;

        switch (direction) {
            case EAST -> resizeEast(deltaX, minWidth);
            case WEST -> resizeWest(deltaX, minWidth);
            case SOUTH -> resizeSouth(deltaY, minHeight);
            case NORTH -> resizeNorth(deltaY, minHeight);
            case SOUTH_EAST -> {
                resizeEast(deltaX, minWidth);
                resizeSouth(deltaY, minHeight);
            }
            case SOUTH_WEST -> {
                resizeWest(deltaX, minWidth);
                resizeSouth(deltaY, minHeight);
            }
            case NORTH_EAST -> {
                resizeEast(deltaX, minWidth);
                resizeNorth(deltaY, minHeight);
            }
            case NORTH_WEST -> {
                resizeWest(deltaX, minWidth);
                resizeNorth(deltaY, minHeight);
            }
            default -> {
            }
        }

        event.consume();
    }

    private void resizeEast(double deltaX, double minWidth) {
        double newWidth = startWidth + deltaX;
        if (newWidth >= minWidth) {
            stage.setWidth(newWidth);
        }
    }

    private void resizeWest(double deltaX, double minWidth) {
        double newWidth = startWidth - deltaX;
        if (newWidth >= minWidth) {
            stage.setX(startStageX + deltaX);
            stage.setWidth(newWidth);
        }
    }

    private void resizeSouth(double deltaY, double minHeight) {
        double newHeight = startHeight + deltaY;
        if (newHeight >= minHeight) {
            stage.setHeight(newHeight);
        }
    }

    private void resizeNorth(double deltaY, double minHeight) {
        double newHeight = startHeight - deltaY;
        if (newHeight >= minHeight) {
            stage.setY(startStageY + deltaY);
            stage.setHeight(newHeight);
        }
    }

    private void endResize(MouseEvent event) {
        if (direction != ResizeDirection.NONE) {
            direction = ResizeDirection.NONE;
            event.consume();
        }
    }
}
