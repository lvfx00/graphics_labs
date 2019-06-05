package ru.nsu.fit.g16205.semenov.raytracing;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.DoublePoint3D;
import ru.nsu.fit.g16205.semenov.raytracing.model.tracing_primitives.*;

import java.util.ArrayList;
import java.util.List;

import static ru.nsu.fit.g16205.semenov.raytracing.model.primitives.DoublePoint3D.getDistance;

public class Reflector {

    public static List<Reflection> getRaytracing(
            @NotNull Ray initialRay,
            @NotNull List<RaytracingFigure> figures,
            @NotNull List<LightSource> lightSources,
            int tracingDepth
    ) {
        final List<Reflection> reflectionList = new ArrayList<>(tracingDepth);
        Ray currentRay = initialRay;
        for (int i = 0; i < tracingDepth; ++i) {
            final Pair<Reflection, Double> closest = findClosestReflection(currentRay, figures);
            if (closest == null) {
                // TODO reflectionList.add(VOID);
                return reflectionList;
            }
            final Reflection reflection = closest.getLeft();
            addIncomingLights(reflection, figures, lightSources);
            reflectionList.add(reflection);
            currentRay = reflection.getReflectedRay();
        }
        return reflectionList;
    }

    private static @Nullable Pair<Reflection, Double> findClosestReflection(
            @NotNull Ray ray,
            @NotNull List<RaytracingFigure> figures
    ) {
        final List<Reflection> reflectionList = new ArrayList<>();
        for (RaytracingFigure figure : figures) {
            final Ray reflectedRay = figure.getPrimitive().getReflectedRay(ray);
            if (reflectedRay != null) {
                reflectionList.add(new Reflection(
                        reflectedRay.getSource(),
                        ray.getDirection(),
                        reflectedRay.getDirection(),
                        figure)
                );
            }
        }
        double minDistance = Double.MAX_VALUE;
        Reflection closestReflection = null;
        for (Reflection reflection : reflectionList) {
            double distance = getDistance(ray.getSource(), reflection.getReflectionPoint());
            if (distance < minDistance) {
                minDistance = distance;
                closestReflection = reflection;
            }
        }
        if (closestReflection == null) {
            return null;
        }
        return Pair.of(closestReflection, minDistance);
    }

    private static void addIncomingLights(
            @NotNull Reflection reflection,
            @NotNull List<RaytracingFigure> figures,
            @NotNull List<LightSource> lightSources
    ) {
        final DoublePoint3D reflectionPoint = reflection.getReflectionPoint();
        for (LightSource lightSource : lightSources) {
            final DoublePoint3D direction = reflectionPoint.minus(lightSource.getPosition()).getNormalized();
            final Ray lightRay = new Ray(lightSource.getPosition(), direction);

            final Pair<Reflection, Double> closestReflection = findClosestReflection(lightRay, figures);
            if (closestReflection == null) {
                // TODO check that
                System.out.println("ASSERT: Ne mojet bit' null !!!!!!!!!!!!!");
                continue;
            }
            final DoublePoint3D intersection = closestReflection.getLeft().getReflectionPoint();
            // TODO стены пропускают свет с обратной стороны через себя так надо???
            if (intersection.equalsWithError(reflectionPoint, 1E-10)) {
                reflection.addIncomingLight(
                        new IncomingLight(lightSource, direction, closestReflection.getRight())
                );
            }
        }
    }
}
