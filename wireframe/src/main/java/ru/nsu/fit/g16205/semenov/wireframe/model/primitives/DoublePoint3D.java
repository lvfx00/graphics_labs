package ru.nsu.fit.g16205.semenov.wireframe.model.primitives;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DoublePoint3D {

    private final double x;
    private final double y;
    private final double z;

    public DoublePoint3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public @NotNull DoublePoint3D plus(@NotNull DoublePoint3D point3D) {
        return new DoublePoint3D(x + point3D.getX(), y + point3D.getY(), z + point3D.getZ());
    }

    public @NotNull DoublePoint3D minus(@NotNull DoublePoint3D point3D) {
        return new DoublePoint3D(x - point3D.getX(), y - point3D.getY(), z - point3D.getZ());
    }

    public double[] asArray() {
        return new double[]{x, y, z};
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoublePoint3D that = (DoublePoint3D) o;
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0 &&
                Double.compare(that.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "DoublePoint3D{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

}
