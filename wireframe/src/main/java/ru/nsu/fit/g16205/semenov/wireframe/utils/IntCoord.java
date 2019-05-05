package ru.nsu.fit.g16205.semenov.wireframe.utils;

import java.util.Objects;

public class IntCoord {

    private final int x;
    private final int y;

    public IntCoord(int x, int y) {
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
        IntCoord intCoord = (IntCoord) o;
        return x == intCoord.x &&
                y == intCoord.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "IntCoord{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

}
