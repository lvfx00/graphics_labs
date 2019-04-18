package ru.nsu.fit.semenov.filter.algorithms;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.semenov.filter.util.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

import static ru.nsu.fit.semenov.filter.util.ImageUtils.*;

public class ErrorDiffusionAlgorithm implements Algorithm {
    private static final ErrorDistr[] ERROR_DISTRIBUTIONS = {
            new ErrorDistr(1, 0, 7f / 16),
            new ErrorDistr(-1, 1, 3f / 16),
            new ErrorDistr(0, 1, 5f / 16),
            new ErrorDistr(1, 1, 1f / 16),
    };

    private final float[] paletteSteps = new float[COLOR_COMPONENTS_NUM];

    public ErrorDiffusionAlgorithm(int redPaletteSize, int greenPaletteSize, int bluePaletteSize) {
        final int[] sizes = {redPaletteSize, greenPaletteSize, bluePaletteSize};
        for (int i = 0; i < COLOR_COMPONENTS_NUM; ++i) {
            paletteSteps[i] = (float) MAX_COLOR_VALUE / (sizes[i] - 1);
        }
    }

    @Override
    public @NotNull BufferedImage apply(@NotNull BufferedImage sourceImage) {
        final int[][][] colorComponents = ImageUtils.extractColorComponents(sourceImage);
        BufferedImage resultImage = new BufferedImage(
                sourceImage.getWidth(),
                sourceImage.getHeight(),
                sourceImage.getType()
        );
        for (int x = 0; x < sourceImage.getWidth(); ++x) {
            for (int y = 0; y < sourceImage.getHeight(); ++y) {
                int[] newColor = new int[COLOR_COMPONENTS_NUM];
                for (int i = 0; i < COLOR_COMPONENTS_NUM; ++i) {
                    newColor[i] = findClosestInPalette(colorComponents[x][y][i], paletteSteps[i]);
                    int error = colorComponents[x][y][i] - newColor[i];
                    for (ErrorDistr errorDistr : ERROR_DISTRIBUTIONS) {
                        int errorX = x + errorDistr.getxOffset();
                        int errorY = y + errorDistr.getyOffset();
                        if (isIndexValid(errorX, errorY, sourceImage.getWidth(), sourceImage.getHeight())) {
                            colorComponents[errorX][errorY][i] += Math.round(errorDistr.getFactor() * error);
                        }
                    }
                }
                resultImage.setRGB(x, y, new Color(newColor[0], newColor[1], newColor[2]).getRGB());
            }
        }
        return resultImage;
    }

    private static int findClosestInPalette(int color, float paletteStep) {
        int stepsNum = Math.round(color / paletteStep);
        int result = Math.round(stepsNum * paletteStep);
        if (result > MAX_COLOR_VALUE) return MAX_COLOR_VALUE;
        if (result < 0) return 0;
        return result;
    }

    private static class ErrorDistr {
        private final int xOffset;
        private final int yOffset;
        private final float factor;

        public ErrorDistr(int xOffset, int yOffset, float factor) {
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.factor = factor;
        }

        public int getxOffset() {
            return xOffset;
        }

        public int getyOffset() {
            return yOffset;
        }

        public float getFactor() {
            return factor;
        }
    }

}
