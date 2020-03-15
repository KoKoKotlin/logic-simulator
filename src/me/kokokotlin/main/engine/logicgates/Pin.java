package me.kokokotlin.main.engine.logicgates;

public class Pin {

    // represents one input OR output of a logic gate

    private LogicStates state;
    private final boolean isPullUp;

    public Pin(boolean isPullUp) {
        this.isPullUp = isPullUp;
        handleFloating();
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
}
