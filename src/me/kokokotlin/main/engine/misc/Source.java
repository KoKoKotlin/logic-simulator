package me.kokokotlin.main.engine.misc;

import me.kokokotlin.main.drawing.DrawingConstants;
import me.kokokotlin.main.drawing.DrawingPriorities;
import me.kokokotlin.main.engine.SchematicElement;
import me.kokokotlin.main.engine.logicgates.LogicStates;
import me.kokokotlin.main.engine.logicgates.Pin;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Source extends SchematicElement {

    private boolean isOn;

    public Source(int worldX, int worldY, boolean isOn) {
        super(worldX, worldY, DrawingConstants.CELL_SIZE, DrawingConstants.CELL_SIZE, "SRC");

        outputs = new ArrayList<>(1);
        outputs.add(new Pin(false, this));

        this.isOn = isOn;
    }

    @Override
    public double calcInputPosY(int inputIndex) {
        return 0;
    }

    @Override
    public double calcOutputPosY(int outputIndex) {
        return getMiddleY();
    }

    @Override
    public void propagate() {
        outputs.get(0).setState(LogicStates.fromBoolean(isOn));
    }

    @Override
    public String getArguments() {
        return String.format("[%d, %d, %b]", worldPos.x, worldPos.y, isOn);
    }

    @Override
    public void draw(Graphics g) {

        final int width = size.width / 2;
        final int height = size.height / 2;
        final int archSize = size.width / 15;
        final int strokeSize = size.width / 30;

        final int x = getScreenPos().x + width / 2;
        final int y = getScreenPos().y + height / 2;

        drawOutputs(g);

        if(isOn) g.setColor(DrawingConstants.HIGH_COLOR);
        else g.setColor(DrawingConstants.LOW_COLOR);

        g.fillRoundRect(x, y, width, height, archSize, archSize);

        g.setColor(new Color(150, 150, 150));
        ((Graphics2D) g).setStroke(new BasicStroke(strokeSize));
        g.drawRoundRect(x, y, width, height, archSize, archSize);
    }

    public void toggle() {
        this.isOn = !this.isOn;
    }

    @Override
    public void onClick(MouseEvent e) {
        Point p = e.getPoint();
        if(e.getButton() == MouseEvent.BUTTON1) {
            if (getBounds().contains(p))
                toggle();
            e.consume();
        }
    }

    @Override
    public DrawingPriorities getDrawPriority() {
        return DrawingPriorities.STANDARD;
    }
}
