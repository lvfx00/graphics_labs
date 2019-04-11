package ru.nsu.fit.semenov.filter.algorithms;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;

import static ru.nsu.fit.semenov.filter.util.ImageUtils.COLOR_COMPONENTS_NUM;
import static ru.nsu.fit.semenov.filter.util.ImageUtils.isIndexValid;

public class ErrorDiffusionAlgorithm extends AbstractAlgorithm {
    private static final ErrorDistribution[] ERROR_DISTRIBUTIONS = {
            new ErrorDistribution(1, 0, 0.4375f),
            new ErrorDistribution(-1, 1, 0.1875f),
            new ErrorDistribution(0, 1, 0.3125f),
            new ErrorDistribution(1, 1, 0.0625f),
    };
    private final float[] paletteSteps = new float[COLOR_COMPONENTS_NUM];

    public ErrorDiffusionAlgorithm(int redPaletteSize, int greenPaletteSize, int bluePaletteSize) {
        final int[] sizes = {redPaletteSize, greenPaletteSize, bluePaletteSize};
        for (int i = 0; i < COLOR_COMPONENTS_NUM; ++i) {
            paletteSteps[i] = 1f / (sizes[i] - 1);
        }
    }

    @Override
    protected void apply(@NotNull BufferedImage sourceImage, @NotNull BufferedImage resultImage) {
        float[][][] errors = new float[COLOR_COMPONENTS_NUM][sourceImage.getWidth()][sourceImage.getHeight()];

        for (int x = 0; x < sourceImage.getWidth(); ++x) {
            for (int y = 0; y < sourceImage.getHeight(); ++y) {
                float[] oldColor = new float[COLOR_COMPONENTS_NUM];
                new Color(sourceImage.getRGB(x, y)).getColorComponents(oldColor);
                float[] newColor = new float[COLOR_COMPONENTS_NUM];
                for (int i = 0; i < COLOR_COMPONENTS_NUM; ++i) {
                    newColor[i] = findClosestInPalette(paletteSteps[i], oldColor[i] + errors[i][x][y]);
                    float errorValue = oldColor[i] - newColor[i];
                    for (ErrorDistribution errorDistribution :
                            ERROR_DISTRIBUTIONS) {
                        int errorX = x + errorDistribution.getxOffset();
                        int errorY = y + errorDistribution.getyOffset();
                        if (isIndexValid(errorX, errorY, sourceImage.getWidth(), sourceImage.getHeight())) {
                            errors[i][errorX][errorY] += errorDistribution.getFactor() * errorValue;
                        }
                    }
                }
                resultImage.setRGB(x, y, new Color(newColor[0], newColor[1], newColor[2]).getRGB());
            }
        }

    }

    private static float findClosestInPalette(float paletteStep, float color) {
        float steps = color / paletteStep;
        float result;
        if (steps - (int) steps > 0.5f) {
            result = ((int) steps + 1) * paletteStep;
        } else {
            result = ((int) steps) * paletteStep;
        }
        return result;
    }

    private static class ErrorDistribution {
        private final int xOffset;
        private final int yOffset;
        private final float factor;

        public ErrorDistribution(int xOffset, int yOffset, float factor) {
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
