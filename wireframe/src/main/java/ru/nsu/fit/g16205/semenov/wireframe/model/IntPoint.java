package ru.nsu.fit.g16205.semenov.wireframe.model;

import java.util.Objects;

public class IntPoint {

    private final int x;
    private final int y;

    public IntPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntPoint intPoint = (IntPoint) o;
        return x == intPoint.x &&
                y == intPoint.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "IntPoint{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

}
