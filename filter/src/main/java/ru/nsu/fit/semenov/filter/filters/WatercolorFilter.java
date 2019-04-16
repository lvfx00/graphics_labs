package ru.nsu.fit.semenov.filter.filters;

import java.util.Arrays;

import static ru.nsu.fit.semenov.filter.util.ImageUtils.COLOR_COMPONENTS_NUM;

public class WatercolorFilter implements Filter {

    @Override
    public int[] apply(int x, int y, int width, int height, int[][][] colorComponents) {
        int[] colors = new int[COLOR_COMPONENTS_NUM];
        for (int i = 0; i < COLOR_COMPONENTS_NUM; ++i) {
            if (x < width - 2 && x > 1 && y < height - 2 && y > 1) {
                int[] colorValues = new int[25];
                for (int j = -2; j < 3; ++j) {
                    for (int k = -2; k < 3; ++k) {
                        colorValues[(j + 2) * 5 + (k + 2)] = colorComponents[x + j][y + k][i];
                    }
                }
                Arrays.sort(colorValues);
                colors[i] = colorValues[12];
            } else {
                colors[i] = colorComponents[x][y][i];
            }
        }
        return colors;
    }

}
