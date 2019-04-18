package ru.nsu.fit.semenov.filter.algorithms;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.lang.Math.floor;
import static ru.nsu.fit.semenov.filter.util.ImageUtils.COLOR_COMPONENTS_NUM;
import static ru.nsu.fit.semenov.filter.util.ImageUtils.extractColorComponents;

public class BilinearInterpolationAlgorithm implements Algorithm {

    private final double scaleRatio;

    public BilinearInterpolationAlgorithm(double scaleRatio) {
        if (scaleRatio < 1.0) {
            throw new IllegalArgumentException("Scale ratio must be >= 1.0");
        }
        this.scaleRatio = scaleRatio;
    }

    @Override
    public @NotNull BufferedImage apply(@NotNull BufferedImage sourceImage) {
        final int[][][] colorComponents = extractColorComponents(sourceImage);

        final int newHeight = (int) floor(sourceImage.getHeight() * scaleRatio);
        final int newWidth = (int) floor(sourceImage.getWidth() * scaleRatio);
        final BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, sourceImage.getType());

        int floorVertical, floorHorizontal;
        double horizontalRatio, verticalRatio;
        double tmp;

        for (int j = 0; j < newHeight; j++) {
            tmp = (double) (j) / (newHeight - 1) * (sourceImage.getWidth() - 1);
            floorVertical = (int) floor(tmp);
            if (floorVertical < 0) {
                floorVertical = 0;
            } else if (floorVertical >= sourceImage.getHeight() - 1) {
                floorVertical = sourceImage.getHeight() - 2;
            }
            verticalRatio = tmp - floorVertical;

            for (int i = 0; i < newWidth; i++) {

                tmp = (float) (i) / (float) (newWidth - 1) * (sourceImage.getWidth() - 1);
                floorHorizontal = (int) floor(tmp);
                if (floorHorizontal < 0) {
                    floorHorizontal = 0;
                } else if (floorHorizontal >= sourceImage.getWidth() - 1) {
                    floorHorizontal = sourceImage.getWidth() - 2;
                }
                horizontalRatio = tmp - floorHorizontal;

                double ratio00 = (1 - horizontalRatio) * (1 - verticalRatio);
                double ratio10 = horizontalRatio * (1 - verticalRatio);
                double ratio11 = horizontalRatio * verticalRatio;
                double ratio01 = (1 - horizontalRatio) * verticalRatio;

                int[] p1 = colorComponents[floorVertical][floorHorizontal];
                int[] p2 = colorComponents[floorVertical][floorHorizontal + 1];
                int[] p3 = colorComponents[floorVertical + 1][floorHorizontal + 1];
                int[] p4 = colorComponents[floorVertical + 1][floorHorizontal];

                int[] newColors = new int[COLOR_COMPONENTS_NUM];
                for (int k = 0; k < COLOR_COMPONENTS_NUM; ++k) {
                    newColors[k] = (int) (p1[k] * ratio00 + p2[k] * ratio10 + p3[k] * ratio11 + p4[k] * ratio01);
                }

                scaledImage.setRGB(j, i, new Color(newColors[0], newColors[1], newColors[2]).getRGB());
            }
        }

        return scaledImage.getSubimage(
                (newWidth - sourceImage.getWidth()) / 2,
                (newHeight - sourceImage.getHeight()) / 2,
                sourceImage.getWidth(),
                sourceImage.getHeight()
        );
    }
}
