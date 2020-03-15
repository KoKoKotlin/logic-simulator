package me.kokokotlin.main.engine.logicgates;

import me.kokokotlin.main.drawing.DrawingConstants;

public class AndGate extends LogicGate {

    public AndGate(int worldX, int worldY, int numInputs) {
        super("AND", numInputs, DrawingConstants.STANDARD_DIM, worldX, worldY);
    }

    @Override
    public void calcOutput() {
        boolean result = true;

        for(Pin input: inputs) {
            result &= LogicStates.toBoolean(input.getState());
        }

        outputs.get(0).setState(LogicStates.fromBoolean(result));
    }
}
