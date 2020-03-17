package me.kokokotlin.main.engine.logicgates;

import me.kokokotlin.main.engine.SchematicElement;

public class Pin {

    // represents one input OR output of a logic gate

    private LogicStates state;
    private final boolean isPullUp;

    private final SchematicElement se;

    private static long instanceCounter = 0;
    public final long ID;

    public Pin(boolean isPullUp, SchematicElement se) {
        this.isPullUp = isPullUp;
        handleFloating();

        this.se = se;
        ID = instanceCounter;
        instanceCounter++;
    }

    public void handleFloating() {
        state = (isPullUp) ? LogicStates.HIGH : LogicStates.LOW;
    }

    public void setState(LogicStates state) {
        if(state == LogicStates.FLOATING) handleFloating();
        else this.state = state;
    }

    public LogicStates getState() {
        return state;
    }

    public boolean isPullUp() { return isPullUp; }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Pin) {
            Pin other = (Pin)obj;

            return isPullUp == other.isPullUp && se == other.se && ID == other.ID;
        }

        return false;
    }
}
