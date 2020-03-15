package me.kokokotlin.main.engine.connectios;

import me.kokokotlin.main.engine.SchematicElement;
import me.kokokotlin.main.engine.logicgates.Pin;
import me.kokokotlin.main.math.Point2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConnectionHandler {
    private final List<Connection> conns = new CopyOnWriteArrayList<>();

    public void addConnection(SchematicElement elemOut, SchematicElement elemIn,
                              int outputIndex, int inputIndex, boolean outPullUp, boolean inPullUp) {
        final Pin output = new Pin(outPullUp);
        final Pin input = new Pin(inPullUp);

        elemOut.setOutput(outputIndex, output);
        elemIn.setInput(inputIndex, input);

        final Point2f start = new Point2f(
                elemOut.getWorldPos().x + 1,
                elemOut.calcOutputPosY(outputIndex));
        final Point2f end = new Point2f(
                elemIn.getWorldPos().x,
                elemIn.calcInputPosY(inputIndex));

        final List<Pin> outs = Collections.singletonList(output);
        final List<Pin> ins = Collections.singletonList(input);

        final double dx = end.x - start.x;

        List<Point2f> path = new ArrayList<>(4);
        path.add(start);
        path.add(new Point2f(start.x + dx / 2.0, start.y));
        path.add(new Point2f(start.x + dx / 2.0, end.y));
        path.add(end);

        final Connection newConn = new Connection(outs, ins, Collections.singletonList(path), Collections.emptyList());
        conns.add(newConn);
    }

    public void addConnection(int[][] outIndices, int[][] inIndices, boolean[] outPullUp, boolean[] inPullUp,
                              List<List<Point2f>> paths, List<Point2f> pivots, List<SchematicElement> elements) {

        final List<Pin> outputs = new ArrayList<>(outIndices.length);
        final List<Pin> inputs = new ArrayList<>(inIndices.length);

        for (int i = 0; i < outIndices.length; i++) {
            final Pin out = new Pin(outPullUp[i]);
            int[] outPair = outIndices[i];

            elements.get(outPair[0]).setOutput(outPair[1], out);
            outputs.add(out);
        }

        for (int i = 0; i < inIndices.length; i++) {
            final Pin in = new Pin(inPullUp[i]);
            int[] inPair = inIndices[i];

            elements.get(inPair[0]).setInput(inPair[1], in);
            inputs.add(in);
        }

        conns.add(new Connection(outputs, inputs, paths, pivots));
    }

    public void updateStates() {
        for(Connection c: conns) {
            c.propagate();
        }
    }

    public List<Connection> getConns() {
        return conns;
    }
}
