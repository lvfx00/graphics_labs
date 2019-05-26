package ru.nsu.fit.g16205.semenov.wireframe.model.primitives;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

public class SphericalPoint {

    private final double r;
    private final double eta;
    private final double fi;

    public SphericalPoint(double r, double eta, double fi) {
        checkArgument(r >= 0, "r was %s", r);
        checkArgument(eta >= 0 && eta <= Math.PI, "eta was %s", eta);
        checkArgument(fi >= 0 && fi < 2 * Math.PI, "fi was %s", fi);
        this.r = r;
        this.eta = eta;
        this.fi = fi;
    }

    public static @NotNull SphericalPoint fromDekartCoords(@NotNull DoublePoint3D point) {
        final double r = Math.sqrt(point.getX() * point.getX() + point.getY() * point.getY() + point.getZ() * point.getZ());
        final double fi = Math.atan2(point.getY(), point.getX());
        return new SphericalPoint(
                r,
                Math.acos(point.getZ() / r),
                (fi >= 0) ? fi : fi + Math.PI * 2
        );
    }

    public @NotNull DoublePoint3D toDekartCoords() {
        return new DoublePoint3D(
                r * Math.sin(eta) * Math.cos(fi),
                r * Math.sin(eta) * Math.sin(fi),
                r * Math.cos(eta)
        );
    }

    public double getR() {
        return r;
    }

    public double getEta() {
        return eta;
    }

    public double getFi() {
        return fi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SphericalPoint that = (SphericalPoint) o;
        return Double.compare(that.r, r) == 0 &&
                Double.compare(that.eta, eta) == 0 &&
                Double.compare(that.fi, fi) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, eta, fi);
    }

    @Override
    public String toString() {
        return "SphericalPoint{" +
                "r=" + r +
                ", eta=" + eta +
                ", fi=" + fi +
                '}';
    }

}
