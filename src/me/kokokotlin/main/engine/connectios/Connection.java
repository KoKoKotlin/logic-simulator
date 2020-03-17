package me.kokokotlin.main.engine.connectios;

import me.kokokotlin.main.drawing.Drawable;
import me.kokokotlin.main.drawing.DrawingConstants;
import me.kokokotlin.main.drawing.DrawingPriorities;
import me.kokokotlin.main.engine.SchematicElement;
import me.kokokotlin.main.engine.logicgates.LogicStates;
import me.kokokotlin.main.engine.logicgates.Pin;
import me.kokokotlin.main.math.Point2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Connection implements Drawable {

    // input -> input of the connected ic (not input of the connection)
    // output -> output of the connected ic (not output of the connection)
    private final List<Pin> inputs = new ArrayList<>(), outputs = new ArrayList<>();

    private final List<List<Point2f>> paths;
    private final List<Point2f> pivots;

    public Connection(List<Pin> output, List<Pin> input, List<List<Point2f>> paths, List<Point2f> pivots) {
        this.inputs.addAll(input);
        this.outputs.addAll(output);

        this.paths = paths;
        this.pivots = pivots;
    }

    public void propagate() {
        for(Pin i: inputs) {
            i.setState(getCombinedState());
        }

        for(Pin o: outputs) {
            o.setState(getCombinedState());
        }
    }

    private LogicStates getCombinedState() {
        boolean result = false;

        for(Pin o: outputs) {
            result |= LogicStates.toBoolean(o.getState());
        }

        return LogicStates.fromBoolean(result);
    }

    private int[] findGateWithPin(boolean findInput, Pin pin, List<SchematicElement> elements) {
        int indecies[] = {-1, -1};

            for(int i = 0; i < elements.size(); i++) {
                SchematicElement se = elements.get(i);

                if (findInput && se.getInputs() != null) {
                    int inputIndex = se.getInputs().indexOf(pin);
                    if(inputIndex != -1) {
                        indecies[0] = i;
                        indecies[1] = inputIndex;
                        break;
                    }
                } else if(!findInput && se.getOutputs() != null) {
                    int outputIndex = se.getOutputs().indexOf(pin);
                    if(outputIndex != -1) {
                        indecies[0] = i;
                        indecies[1] = outputIndex;
                        break;
                    }
                }
            }

        return indecies;
    }

    public String getStringRepr(List<SchematicElement> elements) {
        StringBuilder sBuilder = new StringBuilder("CONN[");

        if(outputs.size() == 1 && inputs.size() == 1) {
            // quick method

            int outIndecies[] = findGateWithPin(false, outputs.get(0), elements);
            int inIndecies[] = findGateWithPin(true, inputs.get(0), elements);

            boolean outPullUp = elements.get(outIndecies[0]).getOutputs().get(outIndecies[1]).isPullUp();
            boolean inPullUp = elements.get(inIndecies[0]).getInputs().get(inIndecies[1]).isPullUp();

            sBuilder.append(String.format("%d->%d, %d->%d, %b, %b", outIndecies[0], outIndecies[1],
                    inIndecies[0], inIndecies[1], outPullUp, inPullUp));

        } else {
            // long method
        }

        sBuilder.append(']');
        return sBuilder.toString();
    }

    @Override
    public void draw(Graphics g) {
        final int leadWidth = (int)(DrawingConstants.CELL_SIZE * (1 / 20.0));

        ((Graphics2D) g).setStroke(new BasicStroke(leadWidth));

        if(getCombinedState() == LogicStates.LOW) g.setColor(DrawingConstants.LOW_COLOR);
        else g.setColor(DrawingConstants.HIGH_COLOR);

        for(List<Point2f> path: paths) {
            for (int i = 0; i < path.size() - 1; i++) {
                Point2f world1 = path.get(i);
                Point2f world2 = path.get(i + 1);

                Point screen1 = DrawingConstants.worldToScreenPos(world1.x, world1.y);
                Point screen2 = DrawingConstants.worldToScreenPos(world2.x, world2.y);

                g.drawLine(screen1.x, screen1.y, screen2.x, screen2.y);
            }
        }

        g.setColor(Color.BLACK);
        for(Point2f p: pivots) {
            Point screen = DrawingConstants.worldToScreenPos(p.x, p.y);

            g.fillOval(screen.x - DrawingConstants.PIVOT_SIZE / 2, screen.y - DrawingConstants.PIVOT_SIZE / 2,
                    DrawingConstants.PIVOT_SIZE, DrawingConstants.PIVOT_SIZE);
        }
    }

    @Override
    public DrawingPriorities getDrawPriority() {
        return DrawingPriorities.LOW;
    }
}
