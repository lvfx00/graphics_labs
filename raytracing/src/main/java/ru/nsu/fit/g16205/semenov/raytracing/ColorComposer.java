package ru.nsu.fit.g16205.semenov.raytracing;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.raytracing.model.tracing_primitives.LightSource;
import ru.nsu.fit.g16205.semenov.raytracing.model.tracing_primitives.OpticalProperties;
import ru.nsu.fit.g16205.semenov.raytracing.model.tracing_primitives.Reflection;

import java.awt.*;
import java.util.List;


public class ColorComposer {

//    public static @NotNull float[] composeColor(@NotNull List<Reflection> reflectionList, @NotNull Color ambientColor) {
//        final List<Reflection> reflectionListReversed = Lists.reverse(reflectionList);
//        float[] ambientLight = ambientColor.getColorComponents(null);
//        float[] totalIntensity = new float[3];
//
//        float[] lastIntensity = null;
//        for (Reflection reflection : reflectionListReversed) {
//            float[] reflectionIntensity = new float[3];
//            final OpticalProperties opticalProperties = reflection.getFigure().getOpticalProperties();
//
//            for (int i = 0; i < 3; ++i) {
//                reflectionIntensity[i] += ambientLight[i] * opticalProperties.getkDiffuse()[i];
//
//                if (lastIntensity != null) {
//                    reflectionIntensity[i] += lastIntensity[i] * opticalProperties.getkSpecular()[i];
//                }
//
//                for (LightSource lightSource : reflection.getLightSources()) {
//
//
//                }
//            }
//            lastIntensity = reflectionIntensity;
//            totalIntensity = lastIntensity;
//        }
//        if (lastIntensity == null) {
//            return ambientLight;
//        } else {
//            return totalIntensity;
//        }
//    }

}
