package me.kokokotlin.main.engine.logicgates;

import me.kokokotlin.main.drawing.DrawingConstants;
import me.kokokotlin.main.drawing.DrawingPriorities;
import me.kokokotlin.main.engine.SchematicElement;
import me.kokokotlin.main.io.ImageLoader;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

abstract public class LogicGate extends SchematicElement {

    // represents a standard logic gate like (AND, OR, NOT, ...)
    // multiplexer and encoder and other more complex ics have their own class

    private String name;
    private int numInputs;

    private BufferedImage sprite;

    public LogicGate(String name, int numInputs, Dimension size, int worldX, int worldY) {
        super(worldX, worldY, DrawingConstants.CELL_SIZE, DrawingConstants.CELL_SIZE);

        this.name = name;
        this.inputs = new ArrayList<>(numInputs);
        for (int i = 0; i < numInputs; i++) {
            this.inputs.add(null);
        }

        this.outputs = new ArrayList<>(1);
        outputs.add(null);

        this.size = size;

        this.worldPos = new Point(worldX, worldY);

        this.numInputs = numInputs;

        if (numInputs != inputs.size())
            throw new IllegalArgumentException("Number of inputs does not match supplied argument!");

        sprite = ImageLoader.loadImage(name);
    }

    protected abstract void calcOutput();

    public void finish() {
        for (int i = 0; i < numInputs; i++) {
            if(inputs.get(i) == null) {
                System.out.println("Gate input at pos " + i + " is null at the end of loading -> defaulting to PullUp");

                inputs.set(i, new Pin(true));
            }
        }

        if(outputs.get(0) == null) {
            System.out.println("Gate output is null at the end of loading -> defaulting to PullDown");

            outputs.set(0, new Pin(false));
        }
    }

    @Override
    public void propagate() {
        calcOutput();
    }

    public void setOutput(Pin output) {
        this.outputs.set(0, output);
    }

    public void setInput(int index, Pin input) {
        this.inputs.set(index, input);
    }

    public double calcInputPosY(int inputIndex) {
        final int screenY = getScreenPos().y;
        final int inputOffset = (numInputs >= 2) ? (int) (size.height * 0.2) : (size.height / 2);
        final int spacing = (numInputs >= 2) ? (size.height - 2 * inputOffset) / (numInputs - 1) : (0);

        return (screenY + spacing * inputIndex + inputOffset) / (double)DrawingConstants.CELL_SIZE;
    }

    public double calcOutputPosY(int outputIndex) {
        return getMiddleY();
    }

    @Override
    public void draw(Graphics g) {
        int screenX = getScreenPos().x;
        int screenY = getScreenPos().y;

        drawInputs(g);

        drawOutputs(g);

        g.drawImage(sprite, screenX, screenY, DrawingConstants.CELL_SIZE, DrawingConstants.CELL_SIZE, null);

        // draw highlight
        if(getHighlight()) {
            g.setColor(new Color(255, 255, 0, 100));
            ((Graphics2D) g).setStroke(new BasicStroke(5f));
            g.drawRect(screenX, screenY, DrawingConstants.CELL_SIZE, DrawingConstants.CELL_SIZE);
        }
    }

    @Override
    public DrawingPriorities getDrawPriority() {
        return DrawingPriorities.STANDARD;
    }

    @Override
    public void onClick(MouseEvent e) { }
}
