package ru.nsu.fit.g16205.semenov.raytracing.model.tracing_primitives;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.DoubleLine;
import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.DoublePoint3D;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.nsu.fit.g16205.semenov.raytracing.utils.VectorUtils.dotProduct;

public final class Plane {

    // plane equation: Ax + By + Cz + D = 0
    private final double a;
    private final double b;
    private final double c;
    private final double d;
    private final DoublePoint3D normalVector;

    public Plane(double a, double b, double c, double d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        normalVector = new DoublePoint3D(a, b, c);
    }


    public static @NotNull Plane fromPoints(
            @NotNull DoublePoint3D p1,
            @NotNull DoublePoint3D p2,
            @NotNull DoublePoint3D p3
    ) {
        checkArgument(!p1.equals(p2), "p1 and p2 must be different points");
        checkArgument(!p1.equals(p3), "p1 and p3 must be different points");
        checkArgument(!p2.equals(p3), "p2 and p3 must be different points");

        final double ax = p1.getX();
        final double ay = p1.getY();
        final double az = p1.getZ();
        final double bx = p2.getX();
        final double by = p2.getY();
        final double bz = p2.getZ();
        final double cx = p3.getX();
        final double cy = p3.getY();
        final double cz = p3.getZ();

        final double a = ay * (bz - cz) + by * (cz - az) + cy * (az - bz);
        final double b = az * (bx - cx) + bz * (cx - ax) + cz * (ax - bx);
        final double c = ax * (by - cy) + bx * (cy - ay) + bx * (ay - by);
        final double d = -(ax * (by * cz - cy * bz) + bx * (cy * az - ay * cz) + cx * (ay * bz - by * az));
        return new Plane(a, b, c, d);
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
        if (t < 0) {
            return null;
        }
        return ray.getSource().plus(ray.getDirection().scale(t));
    }

    public @Nullable Ray getReflectedRay(@NotNull Ray ray) {
        final DoublePoint3D intersectionWithPlane = getIntersection(ray);
        if (intersectionWithPlane == null) {
            return null;
        }
        final DoublePoint3D reflectedDirection = normalVector
                .scale(2 * dotProduct(ray.getDirection(), normalVector))
                .minus(ray.getDirection());
        return new Ray(intersectionWithPlane, reflectedDirection);
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

    // todo override equals() method
}
