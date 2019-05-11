package ru.nsu.fit.g16205.semenov.wireframe.generatrix;

import org.ejml.simple.SimpleMatrix;
import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.wireframe.model.DoublePoint;

import java.util.Collection;
import java.util.stream.DoubleStream;

public class BezierCurve {

    public static class Adapter2D {

        private  Adapter2D() {
        }

        public static @NotNull DoublePoint matrixToPoint(@NotNull SimpleMatrix matrix) {
            if (matrix.numCols() != 2 || matrix.numRows() != 1) {
                throw new IllegalArgumentException("Invalid matrix specified");
            }
            return new DoublePoint(matrix.get(0, 0), matrix.get(0, 1));
        }

        public static @NotNull SimpleMatrix pointsToMatrix(@NotNull Collection<DoublePoint> points) {
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

    public static final int MIN_POINTS_NUM = 4;
    private static final int N_FOR_LENGTH = 100;
    private static final SimpleMatrix Ms = new SimpleMatrix(
            new double[][]{
                    {-1, 3, -3, 1},
                    {3, -6, 3, 0},
                    {-3, 0, 3, 0},
                    {1, 4, 1, 0}
            }
    ).scale(1. / 6);

    private final SimpleMatrix anchorPoints;
    private final int partsNum;
    private final double[] lengthsOfParts;
    private final double totalLength;

    public BezierCurve(@NotNull SimpleMatrix anchorPoints) {
        partsNum = anchorPoints.numRows() - (MIN_POINTS_NUM - 1);
        if (partsNum < 1) {
            throw new IllegalArgumentException("You have to define at least 4 anchor points to create curve");
        }
        this.anchorPoints = anchorPoints;
        lengthsOfParts = new double[partsNum];
        SimpleMatrix previousPoint = null;
        for (int i = 0; i < partsNum; ++i) {
            for (double t = 0.; t < 1.; t += 1. / N_FOR_LENGTH) {
                SimpleMatrix point = getPoint(i, t);
                if (previousPoint != null) {
                    lengthsOfParts[i] += getDistance(previousPoint, point);
                }
                previousPoint = point;
            }
        }
        totalLength = DoubleStream.of(lengthsOfParts).sum();
    }

    public @NotNull SimpleMatrix getPoint(double parameter) {
        if (parameter < 0 || parameter > totalLength) {
            throw new IllegalArgumentException("Invalid length specified");
        }
        double previousPartsLength = 0;
        for (int i = 0; i < partsNum; ++i) {
            final double newLength = previousPartsLength + lengthsOfParts[i];
            if (parameter <= newLength) {
                return getPoint(i, (parameter - previousPartsLength) / lengthsOfParts[i]);
            }
            previousPartsLength = newLength;
        }
        throw new AssertionError("Never reached");
    }

    public double getTotalLength() {
        return totalLength;
    }

    private @NotNull SimpleMatrix getPoint(int partNum, double positionInPart) {
        SimpleMatrix Gsi = anchorPoints.rows(partNum, partNum + 4);
        SimpleMatrix T = getTMatrix(positionInPart);
        return T.mult(Ms).mult(Gsi);
    }

    private static double getDistance(SimpleMatrix point1, SimpleMatrix point2) {
        if (point1.numCols() != point2.numCols()) {
            throw new IllegalArgumentException("Specified points have different dimensions");
        }
        double distance = 0;
        for (int i = 0; i < point1.numCols(); ++i) {
            distance += (point1.get(0, i) - point2.get(0, i)) * (point1.get(0, i) - point2.get(0, i));
        }
        return Math.sqrt(distance);
    }

    private static @NotNull SimpleMatrix getTMatrix(double t) {
        return new SimpleMatrix(1, 4, true, new double[]{t * t * t, t * t, t, 1});
    }

}
