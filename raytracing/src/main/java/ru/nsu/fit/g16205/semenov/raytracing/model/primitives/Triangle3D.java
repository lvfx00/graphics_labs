package ru.nsu.fit.g16205.semenov.raytracing.model.primitives;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Triangle3D {

    private final DoublePoint3D a;
    private final DoublePoint3D b;
    private final DoublePoint3D c;
    private final List<DoublePoint3D> points;

    public Triangle3D(DoublePoint3D a, DoublePoint3D b, DoublePoint3D c) {
        this.a = a;
        this.b = b;
        this.c = c;
        List<DoublePoint3D> list = new ArrayList<>(3);
        list.add(a);
        list.add(b);
        list.add(c);
        points = Collections.unmodifiableList(list);
    }

    public DoublePoint3D getA() {
        return a;
    }

    public DoublePoint3D getB() {
        return b;
    }

    public DoublePoint3D getC() {
        return c;
    }

    public @NotNull List<DoublePoint3D> getPoints() {
        return points;
    }

    public @NotNull Triangle toTriangle2D() {
        return new Triangle(points.get(0).toPoint2D(), points.get(1).toPoint2D(), points.get(2).toPoint2D());
    }

}
