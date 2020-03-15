package me.kokokotlin.main.engine.logicgates;

import me.kokokotlin.main.drawing.DrawingConstants;

public class NotGate extends LogicGate {

    public NotGate(int worldX, int worldY) {
        super("NOT", 1, DrawingConstants.STANDARD_DIM, worldX, worldY);
    }

    @Override
    public void calcOutput() {
        boolean result = LogicStates.toBoolean(inputs.get(0).getState());

        outputs.get(0).setState(LogicStates.fromBoolean(!result));
    }
}
