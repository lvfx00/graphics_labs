package ru.nsu.fit.semenov.filter.algorithms;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

public class ErrorDiffusionAlgorithm extends AbstractAlgorithm {
    private static final ErrorDistribution[] ERROR_DISTRIBUTIONS = {
            new ErrorDistribution(1, 0, 0.4375f),
            new ErrorDistribution(-1, 1, 0.1875f),
            new ErrorDistribution(0, 1, 0.3125f),
            new ErrorDistribution(1, 1, 0.0625f),
    };
    private final float[][] palettes = new float[3][];

    public ErrorDiffusionAlgorithm(int redPaletteSize, int greenPaletteSize, int bluePaletteSize) {
        final int[] sizes = {redPaletteSize, greenPaletteSize, bluePaletteSize};
        for (int i = 0; i < 3; ++i) {
            palettes[0] = new float[sizes[i]];
            float step = 1f / (sizes[i] - 1);
            for (int j = 0; j < sizes[i]; ++j) {
                palettes[0][j] = j * step;
            }
        }
    }

    @Override
    protected void apply(@NotNull BufferedImage sourceImage, @NotNull BufferedImage resultImage) {
        float[][][] errors = new float[3][sourceImage.getWidth()][sourceImage.getHeight()];

        for (int x = 0; x < sourceImage.getWidth(); ++x) {
            for (int y = 0; y < sourceImage.getHeight(); ++y) {
                float[] oldColor = new float[3];
                new Color(sourceImage.getRGB(x, y)).getColorComponents(oldColor);
                float[] newColor = new float[3];
                for (int i = 0; i < 3; ++i) {
                    newColor[i] = findClosestInPalette(palettes[i], oldColor[i] + errors[i][x][y]);
                    float errorValue = newColor[i] - oldColor[i];
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

    private static float findClosestInPalette(float[] palette, float color) {
        return (float) IntStream.range(0, palette.length)
                .mapToDouble(i -> Math.abs(palette[i] - color))
                .min()
                .orElseThrow(() -> new NoSuchElementException("No value present"));
    }

    private static boolean isIndexValid(int x, int y, int width, int height) {
        return x >= 0 && x < width && y >= 0 && y < height;
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
