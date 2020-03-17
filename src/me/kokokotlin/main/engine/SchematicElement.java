package me.kokokotlin.main.engine;

import me.kokokotlin.main.drawing.Drawable;
import me.kokokotlin.main.drawing.DrawingConstants;
import me.kokokotlin.main.engine.logicgates.LogicStates;
import me.kokokotlin.main.engine.logicgates.Pin;

import java.awt.*;
import java.util.List;

abstract public class SchematicElement implements Drawable, Updateable {

    protected Point worldPos;
    protected Dimension size;

    protected List<Pin> inputs;
    protected List<Pin> outputs;

    private final int LEAD_LENGTH;
    private final int LEAD_WIDTH;

    private boolean highlight = false;
    public final String REPR;

    public SchematicElement(int worldX, int worldY, int width, int height, String REPR) {
        this.worldPos = new Point(worldX, worldY);
        this.size = new Dimension(width, height);

        this.LEAD_LENGTH = (int) (DrawingConstants.CELL_SIZE * 0.25);
        this.LEAD_WIDTH = (int) (DrawingConstants.CELL_SIZE * (1 / 20.0));

        this.REPR = REPR;
    }

    protected double getMiddleY() {
        return worldPos.y + .5;
    }

    public abstract double calcInputPosY(int inputIndex);

    public abstract double calcOutputPosY(int outputIndex);

    public abstract String getArguments();

    public Point getWorldPos() {
        return worldPos;
    }

    public void setInput(int index, Pin input) {
        this.inputs.set(index, input);
    }

    public void setOutput(int index, Pin output) {
        this.outputs.set(index, output);
    }

    public void setHighlight(boolean highlight) { this.highlight = highlight; }

    protected boolean getHighlight() {
        return this.highlight;
    }

    public void drawInputs(Graphics g) {
        if (inputs.size() == 0) return;

        int screenX = getScreenPos().x;
        ((Graphics2D) g).setStroke(new BasicStroke(LEAD_WIDTH));

        for (int i = 0; i < this.inputs.size(); i++) {
            if (inputs.get(i).getState() == LogicStates.LOW) g.setColor(DrawingConstants.LOW_COLOR);
            else g.setColor(DrawingConstants.HIGH_COLOR);

            int y = DrawingConstants.worldToScreenCoord(calcInputPosY(i));
            g.drawLine(screenX, y, screenX + LEAD_LENGTH, y);
        }
    }

    public void drawOutputs(Graphics g) {
        if (outputs.size() == 0) return;

        int screenX = getScreenPos().x;
        ((Graphics2D) g).setStroke(new BasicStroke(LEAD_WIDTH));

        for (int i = 0; i < this.outputs.size(); i++) {
            if (outputs.get(i).getState() == LogicStates.LOW) g.setColor(DrawingConstants.LOW_COLOR);
            else g.setColor(DrawingConstants.HIGH_COLOR);

            int y = DrawingConstants.worldToScreenCoord(calcOutputPosY(i));
            g.drawLine(screenX + size.width - LEAD_LENGTH, y, screenX + size.width, y);
        }
    }

    public Point getScreenPos() {
        return DrawingConstants.worldToScreenPos(worldPos.x, worldPos.y);
    }

    public Dimension getSize() {
        return size;
    }

    public Rectangle getBounds() {
        return new Rectangle(getScreenPos().x, getScreenPos().y, size.width, size.height);
    }

    public void setWorldPos(Point p) {
        this.worldPos = p;
    }

    public List<Pin> getInputs() {
        return inputs;
    }

    public List<Pin> getOutputs() {
        return outputs;
    }
}
