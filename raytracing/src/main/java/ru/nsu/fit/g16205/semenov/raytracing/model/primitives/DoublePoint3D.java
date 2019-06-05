package ru.nsu.fit.g16205.semenov.raytracing.model.primitives;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

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

    public double getNorm() {
        return sqrt(x * x + y * y + z * z);
    }

    public @NotNull DoublePoint3D getNormalized() {
        return scale(1. / getNorm());
    }

    public @NotNull DoublePoint3D plus(@NotNull DoublePoint3D point3D) {
        return new DoublePoint3D(x + point3D.getX(), y + point3D.getY(), z + point3D.getZ());
    }

    public @NotNull DoublePoint3D minus(@NotNull DoublePoint3D point3D) {
        return new DoublePoint3D(x - point3D.getX(), y - point3D.getY(), z - point3D.getZ());
    }

    public @NotNull DoublePoint3D scale(double value) {
        return new DoublePoint3D(x * value, y * value, z * value);
    }

    public @NotNull DoublePoint3D divide(@NotNull DoublePoint3D point) {
        return new DoublePoint3D(
                x / point.getX(),
                y / point.getY(),
                z / point.getZ()
        );
    }

    public static double getDistance(@NotNull DoublePoint3D p1, @NotNull DoublePoint3D p2) {
        return Math.sqrt((p1.getX() - p2.getX()) * (p1.getX() - p2.getX()) +
                (p1.getY() - p2.getY()) * (p1.getY() - p2.getY()) +
                (p1.getZ() - p2.getZ()) * (p1.getZ() - p2.getZ()));
    }

    public double[] asArray() {
        return new double[]{x, y, z};
    }

    public @NotNull DoublePoint toPoint2D() {
        return new DoublePoint(x, y);
    }

    public boolean equalsWithError(@NotNull DoublePoint3D point, double error) {
        return (abs(x - point.getX()) < error && abs(y - point.getY()) < error && abs(z - point.getZ()) < error);
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
