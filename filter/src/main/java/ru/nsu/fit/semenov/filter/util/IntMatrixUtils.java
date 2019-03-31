package ru.nsu.fit.semenov.filter.util;

import java.util.function.IntUnaryOperator;

public class IntMatrixUtils {

    @FunctionalInterface
    public interface IntBiFunction {

        int apply(int op1, int op2);

    }

    public static int[][] performUnaryOp(int[][] matrix, int d1, int d2, IntUnaryOperator op) {
        int[][] newMatrix = new int[d1][d2];
        for (int i = 0; i < d1; ++i) {
            for (int j = 0; j < d2; ++j) {
                newMatrix[i][j] = op.applyAsInt(matrix[i][j]);
            }
        }
        return newMatrix;
    }

    public static int[][] performBinaryOp(int[][] matrix1, int[][] matrix2, int d1, int d2, IntBiFunction op) {
        int[][] newMatrix = new int[d1][d2];
        for (int i = 0; i < d1; ++i) {
            for (int j = 0; j < d2; ++j) {
                newMatrix[i][j] = op.apply(matrix1[i][j], matrix2[i][j]);
            }
        }
        return newMatrix;
    }

    // matrix1[d1][d2] and matrix2[d1][d3]
    public static int[][] joinHorizontally(int[][] matrix1, int[][] matrix2, int d1, int d2, int d3) {
        int[][] newMatrix = new int[d1][d2 + d3];
        for (int i = 0; i < d1; ++i) {
            System.arraycopy(matrix1[i], 0, newMatrix[i], 0, d2);
            System.arraycopy(matrix2[i], 0, newMatrix[i], d2, d3);
        }
        return newMatrix;
    }

    // matrix1[d1][d2] and matrix2[d3][d2]
    public static int[][] joinVertically(int[][] matrix1, int[][] matrix2, int d1, int d2, int d3) {
        int[][] newMatrix = new int[d1 + d3][d2];
        for (int i = 0; i < d1; ++i) {
            System.arraycopy(matrix1[i], 0, newMatrix[i], 0, d2);
        }
        for (int i = 0; i < d3; ++i) {
            System.arraycopy(matrix2[i], 0, newMatrix[i + d1], 0, d2);
        }
        return newMatrix;
    }

    public static int[][] createMatrix(int dimen, int defaultValue) {
        int[][] matrix = new int[dimen][dimen];
        for (int i = 0; i < dimen; ++i) {
            for (int j = 0; j < dimen; ++j) {
                matrix[i][j] = defaultValue;
            }
        }
        return matrix;
    }

}
