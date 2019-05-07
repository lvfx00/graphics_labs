package ru.nsu.fit.g16205.semenov.wireframe.model;

import java.util.Objects;

public class DoubleRectangle {

    private final double minX;
    private final double minY;
    private final double width;
    private final double height;

    public DoubleRectangle(double minX, double minY, double width, double height) {
        this.minX = minX;
        this.minY = minY;
        this.width = width;
        this.height = height;
    }

    public double getMinX() {
        return minX;
    }

    public double getMinY() {
        return minY;
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
        return Double.compare(doubleRectangle.minX, minX) == 0 &&
                Double.compare(doubleRectangle.minY, minY) == 0 &&
                Double.compare(doubleRectangle.width, width) == 0 &&
                Double.compare(doubleRectangle.height, height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minX, minY, width, height);
    }

    @Override
    public String toString() {
        return "DoubleRectangle{" +
                "minX=" + minX +
                ", minY=" + minY +
                ", width=" + width +
                ", height=" + height +
                '}';
    }

}
