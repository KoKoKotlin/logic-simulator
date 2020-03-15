package me.kokokotlin.main.drawing;

import java.awt.*;

public class DrawingConstants {
    public static final int WINDOW_WIDTH = 1280, WINDOW_HEIGHT = 720;

    public static final int CELL_SIZE = 100;
    private static final double PADDING = 1.00;
    public static final Dimension STANDARD_DIM = new Dimension(CELL_SIZE, CELL_SIZE);

    public static final Color LOW_COLOR = new Color(100, 100, 100);
    public static final Color HIGH_COLOR = new Color(255, 255, 175);

    public static final int PIVOT_SIZE = 10;

    public static Point worldToScreenPos(double worldX, double worldY) {
        return new Point(
                worldToScreenCoord(worldX),
                worldToScreenCoord(worldY)
        );
    }

    public static int worldToScreenCoord(double d) {
       return (int) ((d * PADDING) * DrawingConstants.CELL_SIZE);
    }

}
