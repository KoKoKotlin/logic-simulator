package me.kokokotlin.main.math;

import java.awt.*;

public class Point2f {
    public double x;
    public double y;

    public Point2f(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point2f(Point p) {
        this.x = p.x;
        this.y = p.y;
    }
}
