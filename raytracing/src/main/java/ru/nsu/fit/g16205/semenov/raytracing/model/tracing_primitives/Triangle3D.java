package ru.nsu.fit.g16205.semenov.raytracing.model.tracing_primitives;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.DoubleLine;
import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.DoublePoint3D;
import ru.nsu.fit.g16205.semenov.raytracing.utils.GeometryUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Triangle3D implements RaytracingPrimitive {

    private final DoublePoint3D a;
    private final DoublePoint3D b;
    private final DoublePoint3D c;
    private final List<DoublePoint3D> points;
    private final Plane trianglePlane;
    private final List<DoubleLine> figureLines;

    public Triangle3D(DoublePoint3D a, DoublePoint3D b, DoublePoint3D c) {
        this.a = a;
        this.b = b;
        this.c = c;
        List<DoublePoint3D> list = new ArrayList<>(3);
        list.add(a);
        list.add(b);
        list.add(c);
        points = Collections.unmodifiableList(list);
        trianglePlane = Plane.fromPoints(a, b, c);
        figureLines = ImmutableList.of(new DoubleLine(a, b), new DoubleLine(b, c), new DoubleLine(c, a));
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

    @Override
    public @NotNull List<DoubleLine> getFigureLines() {
        return figureLines;
    }

    @Override
    public @Nullable DoublePoint3D getIntersection(@NotNull Ray ray) {
        final DoublePoint3D intersectionWithPlane = trianglePlane.getIntersection(ray);
        if (intersectionWithPlane == null) {
            return null;
        }
        if (!toTriangle2D().isInTriangle(intersectionWithPlane.toPoint2D())) {
            return null;
        }
        return intersectionWithPlane;
    }

    @Override
    public @Nullable Ray getReflectedRay(@NotNull Ray ray) {
        final DoublePoint3D intersection = getIntersection(ray);
        if (intersection == null) {
            return null;
        }
        return GeometryUtils.getReflectedRay(ray.getDirection(), trianglePlane.getNormalVector(), intersection);
    }


}
