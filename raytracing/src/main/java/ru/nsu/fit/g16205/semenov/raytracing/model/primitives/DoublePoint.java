package ru.nsu.fit.g16205.semenov.raytracing.model.primitives;

import org.jetbrains.annotations.NotNull;

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

    public static double getDistance(@NotNull DoublePoint p1, @NotNull DoublePoint p2) {
        return Math.sqrt((p1.getX() - p2.getX()) * (p1.getX() - p2.getX()) +
                (p1.getY() - p2.getY()) * (p1.getY() - p2.getY()));
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
