package me.kokokotlin.main.engine.editor;

import me.kokokotlin.main.drawing.Drawable;
import me.kokokotlin.main.drawing.DrawingConstants;
import me.kokokotlin.main.drawing.DrawingPriorities;
import me.kokokotlin.main.drawing.MainWindow;
import me.kokokotlin.main.engine.SchematicElement;
import me.kokokotlin.main.engine.UpdateHandler;

import java.awt.*;

public class SchematicManipulator implements Drawable {

    private SchematicElement currentElement = null;
    private final UpdateHandler updateHandler;
    private MainWindow window;

    private Point position = new Point(0, 0);

    private boolean enabled = false;

    public SchematicManipulator(UpdateHandler updateHandler) {
        this.updateHandler = updateHandler;
    }

    public void setElement(SchematicElement element) {
        this.currentElement = element;
    }

    public void finishCurrent() {
        if(currentElement == null) return;

        updateHandler.addUpdateable(currentElement);
        window.addDrawable(currentElement);

        currentElement = null;
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

    public void reset() {
        this.currentElement = null;
        this.position = new Point(0, 0);
    }

    @Override
    public void draw(Graphics g) {
        if (currentElement == null || !enabled) return;

        final int x = Math.floorDiv(position.x, DrawingConstants.CELL_SIZE);
        final int y = Math.floorDiv(position.y, DrawingConstants.CELL_SIZE);

        currentElement.setWorldPos(new Point(x, y));
        currentElement.draw(g);

        g.setColor(new Color(0, 255, 0, 100));
        ((Graphics2D) g).setStroke(new BasicStroke(2f));

        g.drawOval(x * DrawingConstants.CELL_SIZE, y * DrawingConstants.CELL_SIZE,
                DrawingConstants.CELL_SIZE, DrawingConstants.CELL_SIZE);
    }

    @Override
    public DrawingPriorities getDrawPriority() {
        return DrawingPriorities.HIGH;
    }

    public void setPosition(Point position) { this.position = position; }

    public void setWindow(MainWindow window) { this.window = window; }
}
