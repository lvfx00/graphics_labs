package ru.nsu.fit.g16205.semenov.wireframe.model.primitives;

import java.util.Objects;

public class DoublePoint {

    private final double x;
    private final double y;

    public DoublePoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoublePoint coord = (DoublePoint) o;
        return Double.compare(coord.x, x) == 0 &&
                Double.compare(coord.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "DoublePoint{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

}
