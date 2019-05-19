package ru.nsu.fit.g16205.semenov.wireframe;

import org.apache.commons.lang3.tuple.Pair;
import org.ejml.simple.SimpleMatrix;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.nsu.fit.g16205.semenov.wireframe.figure.BezierCurve;
import ru.nsu.fit.g16205.semenov.wireframe.figure.FigureData;
import ru.nsu.fit.g16205.semenov.wireframe.figure.FigureParameters;

import java.util.ArrayList;
import java.util.List;

public class SurfaceCreator {

    private SurfaceCreator() {
    }

    private static SimpleMatrix getSurfacePoint(@NotNull BezierCurve curve, double u, double v) {
        final SimpleMatrix curvePoint = curve.getPoint(u * curve.getTotalLength());
        final double curvePointX = curvePoint.get(0, 0);
        final double curvePointY = curvePoint.get(0, 1);
        return new SimpleMatrix(
                4,
                1,
                false,
                new double[]{curvePointY * Math.cos(v), curvePointY * Math.sin(v), curvePointX, 1}
        );
    }

    public static List<Pair<SimpleMatrix, SimpleMatrix>> createSurface(FigureData figureData) {
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
        final List<Pair<SimpleMatrix, SimpleMatrix>> surfaceSegments = new ArrayList<>(numSegments);

        System.out.println("a: " + a);
        System.out.println("b: " + b);

        for (int i = 0; i <= m; ++i) {
            @Nullable SimpleMatrix previousPoint = null;
            final double iterV = ((double) i) / m;
            final double v = c * (1 - iterV) + d * iterV;
            for (int j = 0; j <= n * k; ++j) {
                final double iterU = ((double) j) / (n * k);
                final double u = a * (1 - iterU) + b * iterU;
                System.out.println("u " + u);
                final SimpleMatrix surfacePoint = getSurfacePoint(curve, u, v);
                if (previousPoint != null) {
                    surfaceSegments.add(Pair.of(previousPoint, surfacePoint));
                }
                previousPoint = surfacePoint;
            }
        }
        for (int j = 0; j <= n; ++j) {
            @Nullable SimpleMatrix previousPoint = null;
            final double iterU = ((double) j) / n;
            final double u = a * (1 - iterU) + b * iterU;
            System.out.println("U " + u);
            for (int i = 0; i <= m * k; ++i) {
                final double iterV = ((double) i) / (m * k);
                final double v = c * (1 - iterV) + d * iterV;
                final SimpleMatrix surfacePoint = getSurfacePoint(curve, u, v);
                if (previousPoint != null) {
                    surfaceSegments.add(Pair.of(previousPoint, surfacePoint));
                }
                previousPoint = surfacePoint;
            }
        }
        return surfaceSegments;
    }

}
