package ru.nsu.fit.g16205.semenov.raytracing.model.tracing_primitives;

import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.DoublePoint3D;

import java.awt.*;

public class LightSource {

    private final DoublePoint3D position;
    private final Color color;

    public LightSource(DoublePoint3D position, Color color) {
        this.position = position;
        this.color = color;
    }

    public DoublePoint3D getPosition() {
        return position;
    }

    public Color getColor() {
        return color;
    }

}
