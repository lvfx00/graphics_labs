package ru.nsu.fit.g16205.semenov.wireframe;

import org.apache.commons.lang3.tuple.Pair;
import org.ejml.simple.SimpleMatrix;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.nsu.fit.g16205.semenov.wireframe.model.figure.BezierCurve;
import ru.nsu.fit.g16205.semenov.wireframe.model.figure.FigureData;
import ru.nsu.fit.g16205.semenov.wireframe.model.figure.FigureParameters;
import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.DoublePoint;
import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.DoublePoint3D;

import java.util.ArrayList;
import java.util.List;

import static ru.nsu.fit.g16205.semenov.wireframe.model.figure.BezierCurve.Adapter2D.matrixToPoint;

public class SurfaceCreator {

    private SurfaceCreator() {
    }

    public static List<Pair<DoublePoint3D, DoublePoint3D>> createSurface(FigureData figureData) {
        final BezierCurve curve = figureData.getCurve();
        final FigureParameters parameters = figureData.getParameters();
        final double a = parameters.getA();
        final double b = parameters.getB();
        final double c = parameters.getC();
        final double d = parameters.getD();
        final int n = parameters.getN();
        final int m = parameters.getM();
        final int k = parameters.getK();
        final int numSegments = (n * (m + 1) + (n + 1) * m) * k * 2;
        final List<Pair<DoublePoint3D, DoublePoint3D>> surfaceSegments = new ArrayList<>(numSegments);

        for (int i = 0; i <= m; ++i) {
            @Nullable DoublePoint3D previousPoint = null;
            final double iterV = ((double) i) / m;
            final double v = c * (1 - iterV) + d * iterV;
            for (int j = 0; j <= n * k; ++j) {
                final double iterU = ((double) j) / (n * k);
                final double u = a * (1 - iterU) + b * iterU;
                final DoublePoint3D surfacePoint = getSurfacePoint(curve, u, v);
                if (previousPoint != null) {
                    surfaceSegments.add(Pair.of(previousPoint, surfacePoint));
                }
                previousPoint = surfacePoint;
            }
        }
        for (int j = 0; j <= n; ++j) {
            @Nullable DoublePoint3D previousPoint = null;
            final double iterU = ((double) j) / n;
            final double u = a * (1 - iterU) + b * iterU;
            for (int i = 0; i <= m * k; ++i) {
                final double iterV = ((double) i) / (m * k);
                final double v = c * (1 - iterV) + d * iterV;
                final DoublePoint3D surfacePoint = getSurfacePoint(curve, u, v);
                if (previousPoint != null) {
                    surfaceSegments.add(Pair.of(previousPoint, surfacePoint));
                }
                previousPoint = surfacePoint;
            }
        }
//        for (Pair<SimpleMatrix, SimpleMatrix> line : surfaceSegments) {
//            System.out.println(line.getLeft());
//            System.out.println(line.getRight());
//        }
        return surfaceSegments;
    }

    private static DoublePoint3D getSurfacePoint(@NotNull BezierCurve curve, double u, double v) {
        final DoublePoint curvePoint = matrixToPoint(curve.getPoint(u * curve.getTotalLength()));
        return new DoublePoint3D(
                curvePoint.getY() * Math.cos(v),
                curvePoint.getY() * Math.sin(v),
                curvePoint.getX()
        );
    }

}
