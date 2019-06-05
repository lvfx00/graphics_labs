package ru.nsu.fit.g16205.semenov.raytracing.model.tracing_primitives;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.DoublePoint3D;

public class Ray {

    private final DoublePoint3D source;
    private final DoublePoint3D direction;

    public Ray(DoublePoint3D source, DoublePoint3D direction) {
        Preconditions.checkArgument(direction.getNorm() == 1., "Direction vector must be normalized");
        this.source = source;
        this.direction = direction;
    }

    public double getParameterValue(@NotNull DoublePoint3D pointOnRay) {
        final DoublePoint3D parameter = pointOnRay.minus(source).divide(direction);
        // TODO delete
        // TODO check that they are the same
        System.out.println("X param: " + parameter.getX());
        System.out.println("Y param: " + parameter.getY());
        System.out.println("Z param: " + parameter.getZ());

        return parameter.getX();
    }

    public DoublePoint3D getSource() {
        return source;
    }

    public DoublePoint3D getDirection() {
        return direction;
    }
}
