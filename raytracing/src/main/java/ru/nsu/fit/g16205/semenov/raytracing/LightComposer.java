package ru.nsu.fit.g16205.semenov.raytracing;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.DoublePoint3D;
import ru.nsu.fit.g16205.semenov.raytracing.model.tracing_primitives.OpticalProperties;
import ru.nsu.fit.g16205.semenov.raytracing.model.tracing_primitives.Reflection;

import java.awt.*;
import java.util.List;

import static ru.nsu.fit.g16205.semenov.raytracing.model.primitives.DoublePoint3D.getDistance;


public class LightComposer {

    public static @NotNull float[] composeLight(@NotNull List<Reflection> reflectionList, @NotNull Color ambientColor) {
        final List<Reflection> reflectionListReversed = Lists.reverse(reflectionList);
        float[] ambientLight = ambientColor.getColorComponents(null);
        float[] totalIntensity = new float[3];
        float[] lastIntensity = null;
        DoublePoint3D lastReflectionPoint = null;
        for (Reflection reflection : reflectionListReversed) {
            float[] reflectionIntensity = new float[3];
            final OpticalProperties opticalProperties = reflection.getFigure().getOpticalProperties();
            for (int i = 0; i < 3; ++i) {
                reflectionIntensity[i] += ambientLight[i] * opticalProperties.getkDiffuse()[i];
                reflectionIntensity[i] += reflection.getReflectionFromLightSources()[i];
                if (lastIntensity != null) {
                    reflectionIntensity[i] += lastIntensity[i] * opticalProperties.getkSpecular()[i] * 0.1 /
                            (getDistance(lastReflectionPoint, reflection.getReflectionPoint()) + 1);
                }
            }
            lastIntensity = reflectionIntensity;
            lastReflectionPoint = reflection.getReflectionPoint();
            totalIntensity = lastIntensity;
        }
        if (lastIntensity == null) {
            return ambientLight;
        } else {
            return totalIntensity;
        }
    }

    public static @NotNull Color[][] normalizeLight(@NotNull float[][][] light, @NotNull Dimension imageSize) {
        float maxLight = -Float.MAX_VALUE;
        for (int x = 0; x < imageSize.width; ++x) {
            for (int y = 0; y < imageSize.height; ++y) {
                for (int i = 0; i < 3; ++i) {
                    if (maxLight < light[y][x][i]) {
                        maxLight = light[y][x][i];
                    }
                }
            }
        }
        final Color[][] colors = new Color[imageSize.height][imageSize.width];
        for (int x = 0; x < imageSize.width; ++x) {
            for (int y = 0; y < imageSize.height; ++y) {
                float[] colorComponents = new float[3];
                for (int i = 0; i < 3; ++i) {
                    // TODO add gamma correction
                    colorComponents[i] = light[y][x][i] / maxLight;
                }
                colors[y][x] = new Color(colorComponents[0], colorComponents[1], colorComponents[2]);
            }
        }
        return colors;
    }

}
