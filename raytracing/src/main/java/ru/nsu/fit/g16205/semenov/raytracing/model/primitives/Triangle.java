package ru.nsu.fit.g16205.semenov.raytracing.model.primitives;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Triangle {

    private final DoublePoint a;
    private final DoublePoint b;
    private final DoublePoint c;
    private final List<DoublePoint> points;

    public Triangle(DoublePoint a, DoublePoint b, DoublePoint c) {
        this.a = a;
        this.b = b;
        this.c = c;
        List<DoublePoint> list = new ArrayList<>(3);
        list.add(a);
        list.add(b);
        list.add(c);
        points = Collections.unmodifiableList(list);
    }

    public DoublePoint getA() {
        return a;
    }

    public DoublePoint getB() {
        return b;
    }

    public DoublePoint getC() {
        return c;
    }

    public @NotNull List<DoublePoint> getPoints() {
        return points;
    }

    public double getSignedArea() {
        return ((b.getX() - a.getX()) * (c.getY() - a.getY()) - (c.getX() - a.getX()) * (b.getY() - a.getY())) / 2;
    }

}
