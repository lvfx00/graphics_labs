package ru.nsu.fit.g16205.semenov.raytracing.model.tracing_primitives;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.DoubleLine;
import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.DoublePoint3D;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.abs;

public class Ray {

    private final DoublePoint3D source;
    private final DoublePoint3D direction;
    private final DoubleLine directionLine;

    public Ray(DoublePoint3D source, DoublePoint3D direction) {
        checkArgument(abs(direction.getNorm() - 1) < 1E-10, "Direction vector must be normalized");
        this.source = source;
        this.direction = direction;
        directionLine = new DoubleLine(source, source.plus(direction));
    }

    public double getParameterValue(@NotNull DoublePoint3D pointOnRay) {
        final DoublePoint3D parameter = pointOnRay.minus(source).divide(direction);
        // TODO delete
        // TODO check that they are the same
//        System.out.println("X param: " + parameter.getX());
//        System.out.println("Y param: " + parameter.getY());
//        System.out.println("Z param: " + parameter.getZ());

        return parameter.getX();
    }

    public @NotNull DoubleLine getDirectionLine() {
        return directionLine;
    }

    public DoublePoint3D getSource() {
        return source;
    }

    public DoublePoint3D getDirection() {
        return direction;
    }
}
