package ru.nsu.fit.semenov.filter.algorithms;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;

import static ru.nsu.fit.semenov.filter.util.ImageUtils.MAX_COLOR_VALUE;

public final class NegativeAlgorithm implements Algorithm {

    @Override
    public @NotNull BufferedImage apply(@NotNull BufferedImage sourceImage) {
        BufferedImage resultImage = new BufferedImage(
                sourceImage.getWidth(),
                sourceImage.getHeight(),
                sourceImage.getType()
        );
        for (int x = 0; x < sourceImage.getWidth(); ++x) {
            for (int y = 0; y < sourceImage.getHeight(); ++y) {
                Color sourceColor = new Color(sourceImage.getRGB(x, y));
                Color resultColor = new Color(
                        MAX_COLOR_VALUE - sourceColor.getRed(),
                        MAX_COLOR_VALUE - sourceColor.getGreen(),
                        MAX_COLOR_VALUE - sourceColor.getBlue()
                );
                resultImage.setRGB(x, y, resultColor.getRGB());
            }
        }
        return resultImage;
    }

}
