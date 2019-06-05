package ru.nsu.fit.g16205.semenov.raytracing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.DoublePoint3D;
import ru.nsu.fit.g16205.semenov.raytracing.model.tracing_primitives.LightSource;
import ru.nsu.fit.g16205.semenov.raytracing.model.tracing_primitives.RaytracingFigure;
import ru.nsu.fit.g16205.semenov.raytracing.model.tracing_primitives.Ray;
import ru.nsu.fit.g16205.semenov.raytracing.model.tracing_primitives.Reflection;

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
            final Reflection reflection = findClosestReflection(currentRay, figures);
            if (reflection == null) {
                // TODO reflectionList.add(VOID);
                return reflectionList;
            }
            addLightSources(reflection, figures, lightSources);
            reflectionList.add(reflection);
            currentRay = reflection.getReflectedRay();
        }
        return reflectionList;
    }

    private static @Nullable Reflection findClosestReflection(
            @NotNull Ray ray,
            @NotNull List<RaytracingFigure> figures
    ) {
        final List<Reflection> reflectionList = new ArrayList<>();
        for (RaytracingFigure figure : figures) {
            final Ray reflectedRay = figure.getPrimitive().getReflectedRay(ray);
            if (reflectedRay != null) {
                reflectionList.add(new Reflection(reflectedRay, figure));
            }
        }
        double minDistance = Double.MAX_VALUE;
        Reflection closestReflection = null;
        for (Reflection reflection : reflectionList) {
            double distance = getDistance(ray.getSource(), reflection.getReflectedRay().getSource());
            if (distance < minDistance) {
                minDistance = distance;
                closestReflection = reflection;
            }
        }
        return closestReflection;
    }

    private static void addLightSources(
            @NotNull Reflection reflection,
            @NotNull List<RaytracingFigure> figures,
            @NotNull List<LightSource> lightSources
    ) {
        final DoublePoint3D target = reflection.getReflectedRay().getSource();
        for (LightSource lightSource : lightSources) {
            final DoublePoint3D direction = target.minus(lightSource.getPosition()).getNormalized();
            final Ray lightRay = new Ray(lightSource.getPosition(), direction);

            final Reflection closestReflection = findClosestReflection(lightRay, figures);
            if (closestReflection == null) {
                System.out.println("Ne mojet bit' null");
                continue;
            }
            final DoublePoint3D intersection = closestReflection.getReflectedRay().getSource();
            // TODO стены пропускают свет с обратной стороны через себя так надо???
            if (intersection.equalsWithError(target, 1E-10)) {
                reflection.addLightSource(lightSource);
            }
        }
    }
}
