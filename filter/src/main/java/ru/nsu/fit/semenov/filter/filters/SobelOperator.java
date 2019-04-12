package ru.nsu.fit.semenov.filter.filters;

import static ru.nsu.fit.semenov.filter.util.ImageUtils.COLOR_COMPONENTS_NUM;
import static ru.nsu.fit.semenov.filter.util.ImageUtils.MAX_COLOR_VALUE;

public class SobelOperator implements Filter {

    private final int limit;

    public SobelOperator(int limit) {
        this.limit = limit;
    }

    @Override
    public int[] apply(int x, int y, int width, int height, int[][][] colorComponents) {
        int[] colors = new int[COLOR_COMPONENTS_NUM];
        for (int i = 0; i < COLOR_COMPONENTS_NUM; ++i) {
            if (x < width - 1 && x > 0 && y < height - 1 && y > 0) {
                int Sx = (colorComponents[x + 1][y - 1][i] + 2 * colorComponents[x + 1][y][i] + colorComponents[x + 1][y + 1][i]) -
                        (colorComponents[x - 1][y - 1][i] + 2 * colorComponents[x - 1][y][i] + colorComponents[x - 1][y + 1][i]);
                int Sy = (colorComponents[x - 1][y + 1][i] + 2 * colorComponents[x][y + 1][i] + colorComponents[x + 1][y + 1][i]) -
                        (colorComponents[x - 1][y - 1][i] + 2 * colorComponents[x][y - 1][i] + colorComponents[x + 1][y - 1][i]);
                int val = Math.abs(Sx) + Math.abs(Sy);
                colors[i] = (val > limit) ? MAX_COLOR_VALUE : 0;
            } else {
                colors[i] = 0;
            }
        }
        return colors;
    }

}
