package ru.nsu.fit.semenov.filter.filters;

import static ru.nsu.fit.semenov.filter.util.ImageUtils.COLOR_COMPONENTS_NUM;
import static ru.nsu.fit.semenov.filter.util.ImageUtils.MAX_COLOR_VALUE;

public class StampingFilter implements Filter {

    @Override
    public int[] apply(int x, int y, int width, int height, int[][][] colorComponents) {
        int[] colors = new int[COLOR_COMPONENTS_NUM];
        for (int i = 0; i < COLOR_COMPONENTS_NUM; ++i) {
            if (x < width - 1 && x > 0 && y < height - 1 && y > 0) {
                colors[i] = -colorComponents[x + 1][y][i] +
                        colorComponents[x - 1][y][i] +
                        colorComponents[x][y + 1][i] -
                        colorComponents[x][y - 1][i];
            } else {
                colors[i] = 0;
            }
            colors[i] = (colors[i] + (MAX_COLOR_VALUE + 1) >> 1) % (MAX_COLOR_VALUE + 1);
            if (colors[i] > MAX_COLOR_VALUE) {
                colors[i] = MAX_COLOR_VALUE;
            }
            if (colors[i] < 0) {
                colors[i] = 0;
            }
        }
        return colors;
    }

}
