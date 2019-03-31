package ru.nsu.fit.semenov.filter.algorithms;

import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;

public abstract class AbstractAlgorithm implements Algorithm {
    @Override
    public final @NotNull BufferedImage apply(@NotNull BufferedImage sourceImage) {
        // TODO remove this restriction and transform source image by myself
        if (sourceImage.getMinX() != 0 || sourceImage.getMinY() != 0) {
            throw new IllegalArgumentException("Source image must have minX = 0 and minY = 0");
        }
        BufferedImage resultImage = new BufferedImage(
                sourceImage.getWidth(),
                sourceImage.getHeight(),
                sourceImage.getType()
        );
        apply(sourceImage, resultImage);
        return resultImage;
    }

    abstract protected void apply(@NotNull BufferedImage sourceImage, @NotNull BufferedImage resultImage);
}
