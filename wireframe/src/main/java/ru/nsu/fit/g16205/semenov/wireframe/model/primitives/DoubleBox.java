package ru.nsu.fit.g16205.semenov.wireframe.model.primitives;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

public class DoubleBox {

    private final double minX;
    private final double maxX;
    private final double xSize;
    private final double minY;
    private final double maxY;
    private final double ySize;
    private final double minZ;
    private final double maxZ;
    private final double zSize;

    public DoubleBox(double minX, double maxX, double minY, double maxY, double minZ, double maxZ) {
        Preconditions.checkArgument(minX < maxX, "minX < maxX required");
        Preconditions.checkArgument(minY < maxY, "minY < maxY required");
        Preconditions.checkArgument(minZ < maxZ, "minZ < maxZ required");
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
        xSize = maxX - minX;
        ySize = maxY - minY;
        zSize = maxZ - minZ;
    }

    public boolean includesPoint(double x, double y, double z) {
        return  x >= minX && x <= maxX &&
                y >= minY && y <= maxY &&
                z >= minZ && z <= maxZ;
    }

    public boolean includesPoint(@NotNull DoublePoint3D point3D) {
        return includesPoint(point3D.getX(), point3D.getY(), point3D.getZ());
    }

    public double getMinX() {
        return minX;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getxSize() {
        return xSize;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxY() {
        return maxY;
    }

    public double getySize() {
        return ySize;
    }

    public double getMinZ() {
        return minZ;
    }

    public double getMaxZ() {
        return maxZ;
    }

    public double getzSize() {
        return zSize;
    }

    @Override
    public String toString() {
        return "DoubleBox{" +
                "minX=" + minX +
                ", maxX=" + maxX +
                ", minY=" + minY +
                ", maxY=" + maxY +
                ", minZ=" + minZ +
                ", maxZ=" + maxZ +
                '}';
    }
}
