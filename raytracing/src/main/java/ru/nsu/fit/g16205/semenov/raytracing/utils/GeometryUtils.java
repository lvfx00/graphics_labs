package ru.nsu.fit.g16205.semenov.raytracing.utils;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.*;
import ru.nsu.fit.g16205.semenov.raytracing.model.tracing_primitives.Ray;

import static ru.nsu.fit.g16205.semenov.raytracing.utils.VectorUtils.dotProduct;

public class GeometryUtils {

    private GeometryUtils() {
    }

    public static @NotNull Ray getReflectedRay(
            @NotNull DoublePoint3D rayDirection,
            @NotNull DoublePoint3D normalVector,
            @NotNull DoublePoint3D intersectionPoint
    ) {
        rayDirection = rayDirection.scale(-1);
        final DoublePoint3D reflectedDirection = normalVector
                .scale(2 * dotProduct(normalVector, rayDirection))
                .minus(rayDirection);
        return new Ray(intersectionPoint, reflectedDirection);
    }

}
