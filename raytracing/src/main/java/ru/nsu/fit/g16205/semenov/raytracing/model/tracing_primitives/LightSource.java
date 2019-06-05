package ru.nsu.fit.g16205.semenov.raytracing.model.tracing_primitives;

import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.DoublePoint3D;

public class LightSource {

    private final DoublePoint3D position;
    private final double intensityRed;
    private final double intensityGreen;
    private final double intensityBlue;

    public LightSource(DoublePoint3D position, double intensityRed, double intensityGreen, double intensityBlue) {
        this.position = position;
        this.intensityRed = intensityRed;
        this.intensityGreen = intensityGreen;
        this.intensityBlue = intensityBlue;
    }

    public DoublePoint3D getPosition() {
        return position;
    }

    public double getIntensityRed() {
        return intensityRed;
    }

    public double getIntensityGreen() {
        return intensityGreen;
    }

    public double getIntensityBlue() {
        return intensityBlue;
    }
}
