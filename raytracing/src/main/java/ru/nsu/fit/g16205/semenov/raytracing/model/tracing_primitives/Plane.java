package ru.nsu.fit.g16205.semenov.raytracing.model.tracing_primitives;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.DoublePoint3D;
import ru.nsu.fit.g16205.semenov.raytracing.utils.GeometryUtils;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.abs;
import static ru.nsu.fit.g16205.semenov.raytracing.utils.VectorUtils.dotProduct;

public final class Plane {

    // plane equation: Ax + By + Cz + D = 0
    private final double a;
    private final double b;
    private final double c;
    private final double d;
    private final DoublePoint3D normalVector;

    private Plane(@NotNull DoublePoint3D abc, double d) {
        System.out.println(abc.getNorm());
        checkArgument(abs(abc.getNorm() - 1) < 1E-10, "You have to normalize a,b,c,d!!!");
        normalVector = abc;
        this.a = abc.getX();
        this.b = abc.getY();
        this.c = abc.getZ();
        this.d = d;
    }

    public static @NotNull Plane fromPoints(
            @NotNull DoublePoint3D p1,
            @NotNull DoublePoint3D p2,
            @NotNull DoublePoint3D p3
    ) {
        checkArgument(!p1.equals(p2), "p1 and p2 must be different points");
        checkArgument(!p1.equals(p3), "p1 and p3 must be different points");
        checkArgument(!p2.equals(p3), "p2 and p3 must be different points");

        final double x1 = p1.getX();
        final double y1 = p1.getY();
        final double z1 = p1.getZ();
        final double x2 = p2.getX();
        final double y2 = p2.getY();
        final double z2 = p2.getZ();
        final double x3 = p3.getX();
        final double y3 = p3.getY();
        final double z3 = p3.getZ();

        double a1 = x2 - x1;
        double b1 = y2 - y1;
        double c1 = z2 - z1;
        double a2 = x3 - x1;
        double b2 = y3 - y1;
        double c2 = z3 - z1;
        double a = b1 * c2 - b2 * c1;
        double b = a2 * c1 - a1 * c2;
        double c = a1 * b2 - b1 * a2;
        double d = (- a * x1 - b * y1 - c * z1);

        final DoublePoint3D abc = new DoublePoint3D(a, b, c);
        final double dNormalized = d / abc.getNorm();

        return new Plane(abc.getNormalized(), dNormalized);
    }

    public @NotNull DoublePoint3D getNormalVector() {
        return normalVector;
    }

    public static @NotNull Plane fromTriangle(@NotNull Triangle3D triangle) {
        return fromPoints(triangle.getA(), triangle.getB(), triangle.getC());
    }

    public boolean isOnPlane(@NotNull DoublePoint3D point) {
        return (a * point.getX() + b * point.getY() + c * point.getZ() + d) == 0;
    }

    public @Nullable DoublePoint3D getIntersection(@NotNull Ray ray) {
        final double pnOnRd = dotProduct(normalVector, ray.getDirection());
        if (pnOnRd >= 0) {
            return null;
        }
        final double pnOnR0 = dotProduct(normalVector, ray.getSource());
        final double t = -(pnOnR0 + d) / pnOnRd;
        if (t < 0) { // TODO add <= ???
            return null;
        }
        return ray.getSource().plus(ray.getDirection().scale(t));
    }

    public @Nullable Ray getReflectedRay(@NotNull Ray ray) {
        final DoublePoint3D intersectionWithPlane = getIntersection(ray);
        if (intersectionWithPlane == null) {
            return null;
        }
        return GeometryUtils.getReflectedRay(ray.getDirection(), normalVector, intersectionWithPlane);
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }

    public double getD() {
        return d;
    }
}
