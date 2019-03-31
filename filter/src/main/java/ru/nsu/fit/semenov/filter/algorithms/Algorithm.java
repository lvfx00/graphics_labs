package ru.nsu.fit.semenov.filter.algorithms;

import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;

public interface Algorithm {
    @NotNull BufferedImage apply(@NotNull BufferedImage sourceImage);
}
