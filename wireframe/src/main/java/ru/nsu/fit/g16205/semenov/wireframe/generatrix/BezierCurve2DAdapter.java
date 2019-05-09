package ru.nsu.fit.g16205.semenov.wireframe.generatrix;

import org.ejml.simple.SimpleMatrix;
import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.wireframe.model.DoublePoint;

import java.util.Collection;

public class BezierCurve2DAdapter {

    private BezierCurve2DAdapter() {
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
