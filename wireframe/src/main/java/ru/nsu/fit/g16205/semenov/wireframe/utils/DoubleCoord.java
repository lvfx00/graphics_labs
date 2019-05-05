package ru.nsu.fit.g16205.semenov.wireframe.utils;

import java.util.Objects;

public class DoubleCoord {

    private final double x;
    private final double y;

    public DoubleCoord(double x, double y) {
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
        DoubleCoord coord = (DoubleCoord) o;
        return Double.compare(coord.x, x) == 0 &&
                Double.compare(coord.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "DoubleCoord{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

}
