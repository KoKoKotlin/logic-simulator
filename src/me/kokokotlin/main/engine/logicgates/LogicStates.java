package me.kokokotlin.main.engine.logicgates;

public enum LogicStates {
    HIGH,
    LOW,
    FLOATING;

    public static LogicStates fromBoolean(boolean b) {
        if(b) return LogicStates.HIGH;
        else return LogicStates.LOW;
    }

    public static boolean toBoolean(LogicStates state) {
        if(state == LogicStates.HIGH) return true;
        else return false;
    }
}
