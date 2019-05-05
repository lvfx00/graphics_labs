package ru.nsu.fit.g16205.semenov.wireframe.model;

import java.util.Objects;

public class DoubleRectangle {

    private final double x;
    private final double y;
    private final double width;
    private final double height;

    public DoubleRectangle(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoubleRectangle doubleRectangle = (DoubleRectangle) o;
        return Double.compare(doubleRectangle.x, x) == 0 &&
                Double.compare(doubleRectangle.y, y) == 0 &&
                Double.compare(doubleRectangle.width, width) == 0 &&
                Double.compare(doubleRectangle.height, height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, width, height);
    }

    @Override
    public String toString() {
        return "DoubleRectangle{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }

}
