package ru.nsu.fit.g16205.semenov.wireframe.generatrix;

import org.ejml.simple.SimpleMatrix;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLOutput;
import java.util.stream.DoubleStream;

public class BezierCurve {

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

        SimpleMatrix previousPoint = null;
        lengthsOfParts = new double[partsNum];

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

        System.out.println("parts num: " + partsNum);
        System.out.println("total length: " + totalLength);
        for (int i = 0; i < lengthsOfParts.length; i++) {
            System.out.println("length[" + i + "] = " + lengthsOfParts[i]);
        }

    }

    public @NotNull SimpleMatrix getPoint(double parameter) {
        if (parameter < 0 || parameter > totalLength) {
            throw new IllegalArgumentException("Invalid length specified");
        }
        double length = 0;
        for (int i = 0; i < partsNum; ++i) {
            double newLength = length + lengthsOfParts[i];
            if (parameter <= newLength) {
                return getPoint(i, (parameter - length) / lengthsOfParts[i]);
            }
            length = newLength;
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
