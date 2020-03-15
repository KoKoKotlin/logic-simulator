package me.kokokotlin.main.drawing;

import java.awt.*;

public interface Drawable {
    public void draw(Graphics g);
    public DrawingPriorities getDrawPriority();
}
