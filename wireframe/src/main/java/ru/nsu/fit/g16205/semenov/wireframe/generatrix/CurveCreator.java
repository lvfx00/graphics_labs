package ru.nsu.fit.g16205.semenov.wireframe.generatrix;

import org.ejml.simple.SimpleMatrix;
import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.wireframe.model.DoublePoint;

import java.util.Collection;
import java.util.stream.Stream;

public class CurveCreator {

    private static final int N = 1000;

    private static final SimpleMatrix Ms = new SimpleMatrix(
            new double[][]{
                    {-1, 3, -3, 1},
                    {3, -6, 3, 0},
                    {-3, 0, 3, 0},
                    {1, 4, 1, 0}
            }
    ).scale(1. / 6);

    private static SimpleMatrix getTMatrix(double t) {
        return new SimpleMatrix(1, 4, true, new double[]{t * t * t, t * t, t, 1});
    }

    private CurveCreator() {
    }

    public static Stream<DoublePoint> getCurvePoints(@NotNull Collection<DoublePoint> points) {
        return getCurvePoints(toMatrix(points)).map(matrix ->
                new DoublePoint(matrix.get(0, 0), matrix.get(0, 1))
        );
    }

    // обобщенный n-мерный случай
    public static Stream<SimpleMatrix> getCurvePoints(@NotNull SimpleMatrix anchorPoints) {
        Stream.Builder<SimpleMatrix> streamBuilder = Stream.builder();
        for (int i = 1; i < anchorPoints.numRows() - 2; ++i) {
            SimpleMatrix Gsi = anchorPoints.rows(i - 1, i + 3);
            for (double t = 0.; t < 1.; t += 1. / N) {
                SimpleMatrix T = getTMatrix(t);
                SimpleMatrix point = T.mult(Ms).mult(Gsi);
                streamBuilder.add(point);
            }
        }
        return streamBuilder.build();
    }

    private static SimpleMatrix toMatrix(@NotNull Collection<DoublePoint> points) {
        SimpleMatrix matrix = new SimpleMatrix(points.size(), 2);
        int i = 0;
        for (DoublePoint c : points) {
            matrix.set(i, 0, c.getX());
            matrix.set(i, 1, c.getY());
            ++i;
        }
        return matrix;
    }

}
