package ru.nsu.fit.g16205.semenov.raytracing.model.tracing_primitives;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Reflection {
    private final RaytracingFigure figure;
    private final Ray reflectedRay;
    private final List<LightSource> lightSources = new ArrayList<>();

    public Reflection(@NotNull Ray reflectedRay, @NotNull RaytracingFigure figure) {
        this.figure = figure;
        this.reflectedRay = reflectedRay;
    }

    public @NotNull RaytracingFigure getFigure() {
        return figure;
    }

    public @NotNull Ray getReflectedRay() {
        return reflectedRay;
    }

    public void addLightSource(@NotNull LightSource lightSource) {
        lightSources.add(lightSource);
    }

    public @NotNull List<LightSource> getLightSources() {
        return Collections.unmodifiableList(lightSources);
    }
}
