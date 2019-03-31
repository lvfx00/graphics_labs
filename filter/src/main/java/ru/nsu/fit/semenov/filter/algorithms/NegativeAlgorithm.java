package ru.nsu.fit.semenov.filter.algorithms;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.semenov.filter.util.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

import static ru.nsu.fit.semenov.filter.util.ImageUtils.MAX_COLOR_VALUE;

public final class NegativeAlgorithm extends AbstractAlgorithm {

    @Override
    public void apply(@NotNull BufferedImage sourceImage, @NotNull BufferedImage resultImage) {
        ImageUtils.forEachPixel(sourceImage, resultImage, sourceRGB -> {
            Color sourceColor = new Color(sourceRGB);
            return new Color(
                    MAX_COLOR_VALUE - sourceColor.getRed(),
                    MAX_COLOR_VALUE - sourceColor.getGreen(),
                    MAX_COLOR_VALUE - sourceColor.getBlue()
            ).getRGB();
        });
    }

}
