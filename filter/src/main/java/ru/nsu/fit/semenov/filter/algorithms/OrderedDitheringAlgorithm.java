package ru.nsu.fit.semenov.filter.algorithms;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.semenov.filter.util.ImageUtils;
import ru.nsu.fit.semenov.filter.util.IntMatrixUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class OrderedDitheringAlgorithm extends AbstractAlgorithm {

    private static final int DITHERING_MATRIX_SIZE_POW_OF_2 = 4;
    private static final int DITHERING_MATRIX_SIZE = 1 << DITHERING_MATRIX_SIZE_POW_OF_2;
    private static final int[][] ditheringMatrix = DitheringMatrix.getDitheringMatrix(DITHERING_MATRIX_SIZE_POW_OF_2);

    @Override
    protected void apply(@NotNull BufferedImage sourceImage, @NotNull BufferedImage resultImage) {
        int normFactor = (ImageUtils.MAX_COLOR_VALUE + 1) / (1 << (DITHERING_MATRIX_SIZE_POW_OF_2 * 2));
        for (int x = 0; x < sourceImage.getWidth(); ++x) {
            for (int y = 0; y < sourceImage.getHeight(); ++y) {
                Color sourceColor = new Color(sourceImage.getRGB(x, y));
                int ditheringValue = ditheringMatrix[y % DITHERING_MATRIX_SIZE][x % DITHERING_MATRIX_SIZE];
                float resultRed = (sourceColor.getRed() / normFactor > ditheringValue) ? 1.0f : 0.0f;
                float resultGreen = (sourceColor.getGreen() / normFactor > ditheringValue) ? 1.0f : 0.0f;
                float resultBlue = (sourceColor.getBlue() / normFactor > ditheringValue) ? 1.0f : 0.0f;
                Color resultColor = new Color(resultRed, resultGreen, resultBlue);
                resultImage.setRGB(x, y, resultColor.getRGB());
            }
        }
    }

    private static class DitheringMatrix {

        private static int[][] matrixN2 = {{0, 2}, {3, 1}};

        public static int[][] getDitheringMatrix(int matrixSizePowOf2) {
            if (matrixSizePowOf2 < 1) {
                throw new IllegalArgumentException("Invalid matrix size specified");
            }
            if (matrixSizePowOf2 == 1) {
                return matrixN2;
            }

            int[][] matrixDn = getDitheringMatrix(matrixSizePowOf2 - 1);
            int[][] matrix4Dn = IntMatrixUtils.performUnaryOp(matrixDn, matrixDn.length, matrixDn[0].length, i -> 4 * i);
            int[][] matrixUn = IntMatrixUtils.createMatrix(2 << (matrixSizePowOf2 - 1), 1);
            int[][] matrix2Un = IntMatrixUtils.createMatrix(2 << (matrixSizePowOf2 - 1), 2);
            int[][] matrix3Un = IntMatrixUtils.createMatrix(2 << (matrixSizePowOf2 - 1), 3);

            int[][] subMatrix10 = IntMatrixUtils.performBinaryOp(
                    matrix4Dn,
                    matrix3Un,
                    matrix4Dn.length,
                    matrix4Dn[0].length,
                    Integer::sum
            );
            int[][] subMatrix11 = IntMatrixUtils.performBinaryOp(
                    matrix4Dn,
                    matrixUn,
                    matrix4Dn.length,
                    matrix4Dn[0].length,
                    Integer::sum
            );
            int[][] subMatrix01 = IntMatrixUtils.performBinaryOp(
                    matrix4Dn,
                    matrix2Un,
                    matrix4Dn.length,
                    matrix4Dn[0].length,
                    Integer::sum
            );
            int[][] matrixRow1 = IntMatrixUtils.joinHorizontally(
                    matrix4Dn,
                    subMatrix01,
                    matrix4Dn.length,
                    matrix4Dn[0].length,
                    subMatrix01[0].length
            );
            int[][] matrixRow2 = IntMatrixUtils.joinHorizontally(
                    subMatrix10,
                    subMatrix11,
                    subMatrix10.length,
                    subMatrix10[0].length,
                    subMatrix11[0].length
            );
            return IntMatrixUtils.joinVertically(
                    matrixRow1,
                    matrixRow2,
                    matrixRow1.length,
                    matrixRow1[0].length,
                    matrixRow2.length
            );
        }

    }

}
