package me.kokokotlin.main.engine.logicgates;

import me.kokokotlin.main.drawing.DrawingConstants;

public class NorGate extends LogicGate {

    public NorGate(int worldX, int worldY, int numInputs) {
        super("NOR", numInputs, DrawingConstants.STANDARD_DIM, worldX, worldY);
    }

    @Override
    public void calcOutput() {
        boolean result = false;

        for(Pin input: inputs) {
            result |= LogicStates.toBoolean(input.getState());
        }

        outputs.get(0).setState(LogicStates.fromBoolean(!result));
    }
}
