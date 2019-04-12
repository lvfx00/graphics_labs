package ru.nsu.fit.semenov.filter.algorithms;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.semenov.filter.filters.Filter;
import ru.nsu.fit.semenov.filter.util.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class FilterAlgorithm implements Algorithm {

    private final Filter filter;

    public FilterAlgorithm(Filter filter) {
        this.filter = filter;
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
                int[] newColor = filter.apply(x, y, sourceImage.getWidth(), sourceImage.getHeight(), colorComponents);
                resultImage.setRGB(x, y, new Color(newColor[0], newColor[1], newColor[2]).getRGB());
            }
        }
        return resultImage;
    }

}
