package me.kokokotlin.main.engine.connectios;

import me.kokokotlin.main.drawing.Drawable;
import me.kokokotlin.main.drawing.DrawingConstants;
import me.kokokotlin.main.drawing.DrawingPriorities;
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
