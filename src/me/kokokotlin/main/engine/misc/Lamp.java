package me.kokokotlin.main.engine.misc;

import me.kokokotlin.main.drawing.DrawingConstants;
import me.kokokotlin.main.drawing.DrawingPriorities;
import me.kokokotlin.main.engine.SchematicElement;
import me.kokokotlin.main.engine.logicgates.LogicStates;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Lamp extends SchematicElement {

    public Lamp(int worldX, int worldY) {
        super(worldX, worldY, DrawingConstants.CELL_SIZE, DrawingConstants.CELL_SIZE);

        this.inputs = new ArrayList<>(1);
        inputs.add(null);
    }

    @Override
    public double calcInputPosY(int inputIndex) {
        return getMiddleY();
    }

    @Override
    public double calcOutputPosY(int outputIndex) {
        return 0;
    }

    @Override
    public void draw(Graphics g) {

        final int width = size.width / 2;
        final int height = size.height / 2;

        final int x = getScreenPos().x + width / 2;
        final int y = getScreenPos().y + height / 2;

        drawInputs(g);

        if(LogicStates.toBoolean(this.inputs.get(0).getState()))
            g.setColor(DrawingConstants.HIGH_COLOR);
        else
            g.setColor(DrawingConstants.LOW_COLOR);

        g.fillOval(x, y, width, height);

        g.setColor(Color.BLACK);
        ((Graphics2D) g).setStroke(new BasicStroke(width / 20f));
        g.drawOval(x, y, width, height);
    }

    @Override
    public DrawingPriorities getDrawPriority() {
        return DrawingPriorities.STANDARD;
    }

    @Override
    public void propagate() { }

    @Override
    public void onClick(MouseEvent e) { }
}
