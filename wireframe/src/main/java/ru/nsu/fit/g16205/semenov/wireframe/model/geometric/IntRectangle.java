package ru.nsu.fit.g16205.semenov.wireframe.model.geometric;

import java.util.Objects;

public class IntRectangle {

    private final int minX;
    private final int minY;
    private final int width;
    private final int height;

    public IntRectangle(int minX, int minY, int width, int height) {
        this.minX = minX;
        this.minY = minY;
        this.width = width;
        this.height = height;
    }

    public int getMinX() {
        return minX;
    }

    public int getMinY() {
        return minY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntRectangle that = (IntRectangle) o;
        return minX == that.minX &&
                minY == that.minY &&
                width == that.width &&
                height == that.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minX, minY, width, height);
    }

    @Override
    public String toString() {
        return "IntRectangle{" +
                "minX=" + minX +
                ", minY=" + minY +
                ", width=" + width +
                ", height=" + height +
                '}';
    }

}
