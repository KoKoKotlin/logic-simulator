package me.kokokotlin.main.io;

import me.kokokotlin.main.drawing.MainWindow;
import me.kokokotlin.main.engine.SchematicElement;
import me.kokokotlin.main.engine.UpdateHandler;
import me.kokokotlin.main.engine.logicgates.*;
import me.kokokotlin.main.engine.misc.Lamp;
import me.kokokotlin.main.engine.misc.Source;
import me.kokokotlin.main.math.Point2f;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// TODO: add timing to files

public class CircuitLoader {
    /*    data format
          Elements:
              > AND[posX, posY, numInputs] (similar for other gate types with variable inputs)
                creates gate with numInputs inputs as (posX, posY)
              > NOT[posX, posY]
                creates NOT gate at (posX, posY)
              > SRC[posX, posY, isOn]
                creates constant source that is by default in the state isOn
              > LMP[posX, posY]
                creates a Lamp that can show the output of a circuit that is connected to it
          MetaInfo:
              > CONN[outGate1->outGateIndex1 ... n, inGate1->inGateIndex1 ... m, outPullUp[1-n], inPullUp[1-m] [, (x|y)]]
                creates a connection from outGate to the inGateInput from inGate
                x and y of extra Points are screen coordinates

    */
    public static String loadCircuit(Path path, MainWindow mainWindow, UpdateHandler updateHandler) {
        List<SchematicElement> elements = new ArrayList<>();
        String error = "";

        try (BufferedReader bReader = Files.newBufferedReader(path)) {
            String lines[] = bReader.lines()
                    .filter(line -> !line.isEmpty()).toArray(String[]::new);

            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                error = String.format("Error in line %d: %s", i, line);

                String command = line.split("\\[")[0];

                String rawData[] = line.split("\\[")[1]
                        .replaceAll(" ", "")
                        .replace("]", "")
                        .split(",");

                switch (command) {
                    case "AND":
                    case "OR":
                    case "NOT":
                    case "NOR":
                    case "NAND":
                    case "XOR": {
                        elements.add(loadLogicGate(command, rawData));
                        break;
                    }
                    case "CONN": {
                        loadConnection(rawData, updateHandler, elements);
                        break;
                    }
                    case "SRC": {
                        elements.add(loadSource(rawData));
                        break;
                    }
                    case "LMP": {
                        elements.add(loadLamp(rawData));
                        break;
                    }
                }
            }
        } catch (Exception e) {
            return error;
        }

        error = "";
        elements.forEach(element -> {
            if (element instanceof LogicGate) ((LogicGate) element).finish();

            mainWindow.addDrawable(element);
            updateHandler.addUpdateable(element);
        });

        updateHandler.getConnHandler().getConns().forEach(mainWindow::addDrawable);

        return error;
    }

    private static LogicGate loadLogicGate(String gateName, String rawData[]) {
        LogicGate gate;
        int[] data = Arrays.stream(rawData).mapToInt(Integer::parseInt).toArray();

        switch (gateName) {
            case "AND":
                gate = new AndGate(data[0], data[1], data[2]);
                break;
            case "OR":
                data = Arrays.stream(rawData).mapToInt(Integer::parseInt).toArray();
                gate = new OrGate(data[0], data[1], data[2]);
                break;
            case "NOT":
                data = Arrays.stream(rawData).mapToInt(Integer::parseInt).toArray();
                gate = new NotGate(data[0], data[1]);
                break;
            case "NAND":
                data = Arrays.stream(rawData).mapToInt(Integer::parseInt).toArray();
                gate = new NandGate(data[0], data[1], data[2]);
                break;
            case "NOR":
                data = Arrays.stream(rawData).mapToInt(Integer::parseInt).toArray();
                gate = new NorGate(data[0], data[1], data[2]);
                break;
            case "XOR":
                data = Arrays.stream(rawData).mapToInt(Integer::parseInt).toArray();
                gate = new XorGate(data[0], data[1], data[2]);
                break;
            default:
                gate = null;
        }

        return gate;
    }

    private static Source loadSource(String rawData[]) {
        int x = Integer.parseInt(rawData[0]);
        int y = Integer.parseInt(rawData[1]);
        boolean isOn = Boolean.parseBoolean(rawData[2]);

        return new Source(x, y, isOn);
    }

    private static Lamp loadLamp(String rawData[]) {
        int[] data = Arrays.stream(rawData).mapToInt(Integer::parseInt).toArray();
        return new Lamp(data[0], data[1]);
    }

    private static void loadConnection(String rawData[], UpdateHandler updateHandler,
                                       List<SchematicElement> elements) {

        // quick load
        if (rawData.length == 4) {
            quickLoadConnection(rawData, updateHandler, elements);
            return;
        }

        // load outputs and inputs
        int[][] outIndicies = Arrays.stream(rawData[0]
                .replace("{", "")
                .replace("}", "")
                .split(";"))
                .map(str -> Arrays.stream(str.split("->")).mapToInt(Integer::parseInt).toArray())
                .toArray(int[][]::new);

        int[][] inIndicies = Arrays.stream(rawData[1]
                .replace("{", "")
                .replace("}", "")
                .split(";"))
                .map(str -> Arrays.stream(str.split("->")).mapToInt(Integer::parseInt).toArray())
                .toArray(int[][]::new);

        // load pull up and pull down booleans
        int outputCount = outIndicies.length;
        int inputCount = inIndicies.length;

        boolean[] isOutputPullup = new boolean[outputCount];
        boolean[] isInputPullup = new boolean[inputCount];

        for (int i = 0; i < outputCount; i++) {
            isOutputPullup[i] = Boolean.parseBoolean(rawData[i + 2]);
        }

        for (int i = 0; i < inputCount; i++) {
            isInputPullup[i] = Boolean.parseBoolean(rawData[i + 2 + outputCount]);
        }

        String pathsData = rawData[2 + outputCount + inputCount];
        String pivotData = rawData[2 + outputCount + inputCount + 1];

        pathsData = pathsData.replace("{", "").replace("}", "");
        pivotData = pivotData.replace("{", "").replace("}", "");

        final List<List<Point2f>> paths = new ArrayList<>();

        for (String pathData : pathsData.split("_")) {
            final List<Point2f> path = Arrays.stream(pathData.split(";"))
                    .map(str -> Arrays.stream(str.split("\\|")).mapToDouble(Double::parseDouble).toArray())
                    .map(doubles -> new Point2f(doubles[0], doubles[1]))
                    .collect(Collectors.toList());

            paths.add(path);
        }

        final List<Point2f> pivots = Arrays.stream(pivotData
                .split(";"))
                .map(str -> Arrays.stream(str.split("\\|")).mapToDouble(Double::parseDouble).toArray())
                .map(doubles -> new Point2f(doubles[0], doubles[1]))
                .collect(Collectors.toList());

        updateHandler.getConnHandler().addConnection(outIndicies, inIndicies, isOutputPullup, isInputPullup,
                paths, pivots, elements);
    }

    private static void quickLoadConnection(String rawData[], UpdateHandler updateHandler,
                                            List<SchematicElement> elements) {
        final int outGateIndex = Integer.parseInt(rawData[0].split("->")[0]);
        final int outIndex = Integer.parseInt(rawData[0].split("->")[1]);

        final int inGateIndex = Integer.parseInt(rawData[1].split("->")[0]);
        final int inIndex = Integer.parseInt(rawData[1].split("->")[1]);

        final boolean outPullUp = Boolean.parseBoolean(rawData[2]);
        final boolean inPullUp = Boolean.parseBoolean(rawData[3]);

        updateHandler.getConnHandler().addConnection(elements.get(outGateIndex), elements.get(inGateIndex),
                outIndex, inIndex, outPullUp, inPullUp);
    }
}
