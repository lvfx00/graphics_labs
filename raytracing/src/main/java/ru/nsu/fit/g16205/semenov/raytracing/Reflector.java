package ru.nsu.fit.g16205.semenov.raytracing;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.nsu.fit.g16205.semenov.raytracing.model.tracing_primitives.RaytracingFigure;
import ru.nsu.fit.g16205.semenov.raytracing.model.tracing_primitives.Ray;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Reflector {

    public static List<Ray> getRaytracing(@NotNull Ray initialRay, @NotNull List<RaytracingFigure> figures, int tracingDepth) {
        final List<Ray> reflectionList = new ArrayList<>(tracingDepth);
        Ray currentRay = initialRay;
        for(int i = 0; i < tracingDepth; ++i) {
            final Ray reflection = findClosestReflection(currentRay, figures);
            if (reflection == null) {
                return reflectionList;
            }
            reflectionList.add(reflection);
            currentRay = reflection;
        }
        return reflectionList;
    }

    private static @Nullable Ray findClosestReflection(@NotNull Ray ray, @NotNull List<RaytracingFigure> figures) {
        final List<Pair<Ray, Double>> reflectionList = new ArrayList<>();
        for (RaytracingFigure figure : figures) {
            final Ray reflection = figure.getReflectedRay(ray);
            if (reflection != null) {
                reflectionList.add(Pair.of(reflection, ray.getParameterValue(reflection.getSource())));
            }
        }
        return reflectionList.stream()
                .min(Comparator.comparingDouble(Pair::getRight))
                .orElseGet(() -> Pair.of(null, null))
                .getLeft();
    }

}
