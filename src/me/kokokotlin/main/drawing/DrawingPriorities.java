package me.kokokotlin.main.drawing;

public enum DrawingPriorities {

    STANDARD(0),
    LOW(-1),
    HIGH(1),
    BACKGROUND(Integer.MIN_VALUE);

    private int value;

    DrawingPriorities(int value) {
        this.value = value;
    }

    public int compare(DrawingPriorities other) {
        return Integer.compare(value, other.value);
    }

}
