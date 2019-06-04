package ru.nsu.fit.g16205.semenov.raytracing.utils;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.DoubleBox;
import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.DoublePoint3D;

import static com.google.common.base.Preconditions.checkArgument;

public class DoubleBoxTransformer {
    private final DoubleBox box1;
    private final DoubleBox box2;
    private final double b1toB2scaleXRatio;
    private final double b1toB2scaleYRatio;
    private final double b1toB2scaleZRatio;

    public DoubleBoxTransformer(@NotNull DoubleBox box1, @NotNull DoubleBox box2) {
        this.box1 = box1;
        this.box2 = box2;
        b1toB2scaleXRatio = box1.getxSize() / box2.getxSize();
        b1toB2scaleYRatio = box1.getySize() / box2.getySize();
        b1toB2scaleZRatio = box1.getzSize() / box2.getzSize();
    }

    public DoublePoint3D toBox2(double x, double y, double z) {
        checkArgument(box1.includesPoint(x, y, z), "Specified point is not in box 1");
        return new DoublePoint3D(
                ((x - box1.getMinX()) / b1toB2scaleXRatio) + box2.getMinX(),
                ((y - box1.getMinY()) / b1toB2scaleYRatio) + box2.getMinY(),
                ((z - box1.getMinZ()) / b1toB2scaleZRatio) + box2.getMinZ()
        );
    }

    public DoublePoint3D toBox2(DoublePoint3D point) {
        return toBox2(point.getX(), point.getY(), point.getZ());
    }

    public DoublePoint3D toBox1(double x, double y, double z) {
        checkArgument(box2.includesPoint(x, y, z), "Specified point is not in box 2");
        return new DoublePoint3D(
                ((x - box2.getMinX()) * b1toB2scaleXRatio) + box1.getMinX(),
                ((y - box2.getMinY()) * b1toB2scaleYRatio) + box1.getMinY(),
                ((z - box2.getMinZ()) * b1toB2scaleZRatio) + box1.getMinZ()
        );
    }

    public DoublePoint3D toBox1(DoublePoint3D point) {
        return toBox1(point.getX(), point.getY(), point.getZ());
    }

}
