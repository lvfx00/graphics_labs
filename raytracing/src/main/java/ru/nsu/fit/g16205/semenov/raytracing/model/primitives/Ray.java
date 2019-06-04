package ru.nsu.fit.g16205.semenov.raytracing.model.primitives;

import com.google.common.base.Preconditions;

public class Ray {

    private final DoublePoint3D source;
    private final DoublePoint3D direction;

    public Ray(DoublePoint3D source, DoublePoint3D direction) {
        Preconditions.checkArgument(direction.getNorm() == 1., "Direction vector must be normalized");
        this.source = source;
        this.direction = direction;
    }

    public DoublePoint3D getSource() {
        return source;
    }

    public DoublePoint3D getDirection() {
        return direction;
    }
}
