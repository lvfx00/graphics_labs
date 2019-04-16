package ru.nsu.fit.semenov.filter.algorithms;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.semenov.filter.util.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RotationAlgorithm implements Algorithm {

    private static final Color DEFAULT_COLOR = Color.WHITE;
    private final double sin;
    private final double cos;

    public RotationAlgorithm(double rotationAngle) {
        sin = Math.sin(rotationAngle);
        cos = Math.cos(rotationAngle);
    }

    @Override
    public @NotNull BufferedImage apply(@NotNull BufferedImage sourceImage) {
        int srcWidth = sourceImage.getWidth();
        int srcHeight = sourceImage.getHeight();
        BufferedImage resultImage = new BufferedImage(srcWidth, srcHeight, sourceImage.getType());

        double imageHalfWidth = srcWidth / 2.0;
        double imageHalfHeight = srcHeight / 2.0;

        for (int x = 0; x < srcWidth; ++x) {
            for (int y = 0; y < srcHeight; ++y) {
                double centeredX = x - imageHalfWidth;
                double centeredY = y - imageHalfHeight;
                resultImage.setRGB(
                        x,
                        y,
                        ImageUtils.getInterpolatedColor(
                                cos * centeredX + sin * centeredY + imageHalfWidth,
                                -sin * centeredX + cos * centeredY + imageHalfHeight,
                                sourceImage,
                                DEFAULT_COLOR
                        ).getRGB()
                );
            }
        }
        return resultImage;
    }

}
