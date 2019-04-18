package ru.nsu.fit.semenov.filter.filters;

import static ru.nsu.fit.semenov.filter.util.ImageUtils.COLOR_COMPONENTS_NUM;

public class BlurFilter implements Filter {

    private static final int[][] BLUR_MATRIX = {
            {1, 2, 3, 2, 1},
            {2, 4, 5, 4, 2},
            {3, 5, 6, 5, 3},
            {2, 4, 5, 4, 2},
            {1, 2, 3, 2, 1}
    };
    private static final int MATRIX_DIVISOR = 74;

    @Override
    public int[] apply(int x, int y, int width, int height, int[][][] colorComponents) {
        int[] colors = new int[COLOR_COMPONENTS_NUM];
        for (int i = 0; i < COLOR_COMPONENTS_NUM; ++i) {
            if (x < width - 2 && x > 1 && y < height - 2 && y > 1) {
                colors[i] = 0;
                for (int j = -2; j < 3; ++j) {
                    for (int k = -2; k < 3; ++k) {
                        colors[i] += colorComponents[x + j][y + k][i] * BLUR_MATRIX[k + 2][j + 2];
                    }
                }
                colors[i] /= MATRIX_DIVISOR;
            } else {
                colors[i] = colorComponents[x][y][i];
            }
        }
        return colors;
    }

}
