package ru.nsu.fit.g16205.semenov.wireframe.utils;

import org.ejml.simple.SimpleMatrix;
import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.DoublePoint3D;

public class VectorUtils {

    private VectorUtils() {
    }

    public static @NotNull DoublePoint3D crossProduct(@NotNull DoublePoint3D a, @NotNull DoublePoint3D b) {
        return new DoublePoint3D(
                a.getY() * b.getZ() - a.getZ() * b.getY(),
                a.getZ() * b.getX() - a.getX() * b.getZ(),
                a.getX() * b.getY() - a.getY() * b.getX()
        );
    }

    public static @NotNull SimpleMatrix crossProduct(@NotNull SimpleMatrix a, @NotNull SimpleMatrix b) {
        return toMatrix(crossProduct(toPoint3D(a), toPoint3D(b)));
    }

    public static @NotNull DoublePoint3D toPoint3D(@NotNull SimpleMatrix matrix) {
        if (matrix.numCols() == 3 && matrix.numRows() == 1) {
            return new DoublePoint3D(
                    matrix.get(0, 0),
                    matrix.get(0, 1),
                    matrix.get(0, 2)
            );
        }
        if (matrix.numCols() == 1 && matrix.numRows() == 3) {
            return new DoublePoint3D(
                    matrix.get(0, 0),
                    matrix.get(1, 0),
                    matrix.get(2, 0)
            );
        }
        throw new IllegalArgumentException("Invalid matrix size");
    }

    // vertical vector required!
    public static @NotNull DoublePoint3D homogenToPoint3D(@NotNull SimpleMatrix matrix) {
        if (matrix.numCols() != 1 || matrix.numRows() != 4) {
            throw new IllegalArgumentException("Invalid matrix size");
        }
        SimpleMatrix newMatrix = matrix.divide(matrix.get(3, 0));
        return new DoublePoint3D(
                newMatrix.get(0, 0),
                newMatrix.get(1, 0),
                newMatrix.get(2, 0)
        );
    }

    public static @NotNull SimpleMatrix toMatrix(@NotNull DoublePoint3D point3D) {
        return new SimpleMatrix(1, 3, true, new double[]{point3D.getX(), point3D.getY(), point3D.getZ()});
    }

    public static @NotNull SimpleMatrix toHomogenColumnVector(@NotNull DoublePoint3D point3D) {
        return new SimpleMatrix(4, 1, false, new double[]{point3D.getX(), point3D.getY(), point3D.getZ(), 1});
    }

}
