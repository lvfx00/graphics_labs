package ru.nsu.fit.g16205.semenov.raytracing.model.figures;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.DoublePoint3D;
import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.Ray;

public interface RaytracingFigure {

    @Nullable DoublePoint3D getIntersection(@NotNull Ray ray);

    @Nullable Ray getReflectedRay(@NotNull Ray ray);

}
