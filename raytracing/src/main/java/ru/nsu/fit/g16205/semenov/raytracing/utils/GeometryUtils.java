package ru.nsu.fit.g16205.semenov.raytracing.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.*;

import static ru.nsu.fit.g16205.semenov.raytracing.utils.VectorUtils.dotProduct;

public class GeometryUtils {

    private GeometryUtils() {
    }

    public static double getDistance3D(@NotNull DoublePoint3D p1, @NotNull DoublePoint3D p2) {
        return Math.sqrt((p1.getX() - p2.getX()) * (p1.getX() - p2.getX()) +
                (p1.getY() - p2.getY()) * (p1.getY() - p2.getY()) +
                (p1.getZ() - p2.getZ()) * (p1.getZ() - p2.getZ()));
    }

    public static double getDistance(@NotNull DoublePoint p1, @NotNull DoublePoint p2) {
        return Math.sqrt((p1.getX() - p2.getX()) * (p1.getX() - p2.getX()) +
                (p1.getY() - p2.getY()) * (p1.getY() - p2.getY()));
    }

    public static @Nullable DoublePoint3D getIntersection(@NotNull Triangle3D triangle3D, @NotNull Ray ray) {
        final double ax = triangle3D.getA().getX();
        final double ay = triangle3D.getA().getY();
        final double az = triangle3D.getA().getZ();
        final double bx = triangle3D.getB().getX();
        final double by = triangle3D.getB().getY();
        final double bz = triangle3D.getB().getZ();
        final double cx = triangle3D.getC().getX();
        final double cy = triangle3D.getC().getY();
        final double cz = triangle3D.getC().getZ();

        final double A = ay * (bz - cz) + by * (cz - az) + cy * (az - bz);
        final double B = az * (bx - cx) + bz * (cx - ax) + cz * (ax - bx);
        final double C = ax * (by - cy) + bx * (cy - ay) + bx * (ay - by);
        final double D = -(ax * (by * cz - cy * bz) + bx * (cy * az - ay * cz) + cx * (ay * bz - by * az));

        final DoublePoint3D pn = new DoublePoint3D(A, B, C);
        final double pnOnRd = dotProduct(pn, ray.getDirection());
        if (pnOnRd >= 0) {
            return null;
        }

        final double pnOnR0 = dotProduct(pn, ray.getSource());
        final double t = -(pnOnR0 + D) / pnOnRd;

        if (t < 0) {
            return null;
        }

        // intersection with triangle3D plane
        final DoublePoint3D r = ray.getSource().plus(ray.getDirection().scale(t));
        if (isInTriangle(triangle3D.toTriangle2D(), r.toPoint2D())) {
            return r;
        }
        return null;
    }

    public static boolean isInTriangle(@NotNull Triangle triangle, @NotNull DoublePoint point) {
        final double areaABC = triangle.getSignedArea();
        final double areaPBC = new Triangle(point, triangle.getB(), triangle.getC()).getSignedArea();
        final double areaAPC = new Triangle(triangle.getA(), point, triangle.getC()).getSignedArea();
        final double areaABP = new Triangle(triangle.getA(), triangle.getB(), point).getSignedArea();
        final double alpha = areaPBC / areaABC;
        final double beta = areaAPC / areaABC;
        final double gamma = areaABP / areaABC;
        // < 00000000.1 to avoid rounding errors
        return (alpha + beta + gamma - 1) < 0.00000001  && alpha >= 0 && beta >= 0 && gamma >= 0;
    }

}
