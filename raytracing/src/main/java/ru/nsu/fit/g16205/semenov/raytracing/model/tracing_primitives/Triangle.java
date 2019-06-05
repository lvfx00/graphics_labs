package ru.nsu.fit.g16205.semenov.raytracing.model.tracing_primitives;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.DoublePoint;

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

    // TODO add support for вырожденные треугольники
    public boolean isInTriangle(@NotNull DoublePoint point) {
        final double areaABC = getSignedArea();
        final double areaPBC = new Triangle(point, b, c).getSignedArea();
        final double areaAPC = new Triangle(a, point, c).getSignedArea();
        final double areaABP = new Triangle(a, b, point).getSignedArea();
        final double alpha = areaPBC / areaABC;
        final double beta = areaAPC / areaABC;
        final double gamma = areaABP / areaABC;
        // < 00000000.1 to avoid rounding errors
        return (alpha + beta + gamma - 1) < 0.00000001  && alpha >= 0 && beta >= 0 && gamma >= 0;
    }

}
