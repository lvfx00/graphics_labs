package ru.nsu.fit.g16205.semenov.wireframe.utils;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.DoublePoint;
import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.DoublePoint3D;

public class GeometryUtils {

    private GeometryUtils() {
    }

    public static double getDistance3D(@NotNull DoublePoint3D p1, @NotNull DoublePoint3D p2) {
        return Math.sqrt((p1.getX() - p2.getX()) * (p1.getX() - p2.getX()) +
                (p1.getY() - p2.getY()) * (p1.getY() - p2.getY()) +
                (p1.getZ() - p2.getZ()) * (p1.getZ() - p2.getZ()));
    }

    public static double getDistance(@NotNull DoublePoint p1, @NotNull DoublePoint p2) {
        return Math.sqrt((p1.getX() - p2.getX()) * (p1.getX() - p2.getX()) +
                (p1.getY() - p2.getY()) * (p1.getY() - p2.getY()));
    }

}
