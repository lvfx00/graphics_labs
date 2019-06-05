package ru.nsu.fit.g16205.semenov.raytracing.model.tracing_primitives;

import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.DoublePoint3D;

public class IncomingLight {

    private final LightSource lightSource;
    private final DoublePoint3D incomingDirection;
    private final double distance;

    public IncomingLight(LightSource lightSource, DoublePoint3D incomingDirection, double distance) {
        this.lightSource = lightSource;
        this.incomingDirection = incomingDirection;
        this.distance = distance;
    }

    public LightSource getLightSource() {
        return lightSource;
    }

    public DoublePoint3D getIncomingDirection() {
        return incomingDirection;
    }

    public double getDistance() {
        return distance;
    }
}
