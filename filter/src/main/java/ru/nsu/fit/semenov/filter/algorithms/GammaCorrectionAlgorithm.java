package ru.nsu.fit.semenov.filter.algorithms;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GammaCorrectionAlgorithm implements Algorithm {

    private final float gamma;

    public GammaCorrectionAlgorithm(float gamma) {
        this.gamma = gamma;
    }

    @Override
    public @NotNull BufferedImage apply(@NotNull BufferedImage sourceImage) {
        BufferedImage resultImage = new BufferedImage(
                sourceImage.getWidth(),
                sourceImage.getHeight(),
                sourceImage.getType()
        );
        for (int x = 0; x < sourceImage.getWidth(); ++x) {
            for (int y = 0; y < sourceImage.getHeight(); ++y) {
                float[] colorComponents = new float[3];
                new Color(sourceImage.getRGB(x, y)).getColorComponents(colorComponents);
                resultImage.setRGB(
                        x,
                        y,
                        new Color(
                                (float) Math.pow(colorComponents[0], gamma),
                                (float) Math.pow(colorComponents[1], gamma),
                                (float) Math.pow(colorComponents[2], gamma)
                        ).getRGB()
                );
            }
        }
        return resultImage;
    }

}
