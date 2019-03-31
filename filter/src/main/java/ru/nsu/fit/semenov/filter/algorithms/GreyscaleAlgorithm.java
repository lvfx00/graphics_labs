package ru.nsu.fit.semenov.filter.algorithms;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;

public final class GreyscaleAlgorithm extends AbstractAlgorithm {
    private static final float RED_FACTOR = 0.299f;
    private static final float GREEN_FACTOR = 0.587f;
    private static final float BLUE_FACTOR = 0.114f;

    @Override
    public void apply(@NotNull BufferedImage sourceImage, @NotNull BufferedImage resultImage) {
        for (int x = 0; x < sourceImage.getWidth(); ++x) {
            for (int y = 0; y < sourceImage.getHeight(); ++y) {
                float[] colorComponents = new float[3];
                new Color(sourceImage.getRGB(x, y)).getColorComponents(colorComponents);
                float intense = RED_FACTOR * colorComponents[0] +
                        GREEN_FACTOR * colorComponents[1] +
                        BLUE_FACTOR * colorComponents[2];
                resultImage.setRGB(x, y, new Color(intense, intense, intense).getRGB());
            }
        }
    }

}
