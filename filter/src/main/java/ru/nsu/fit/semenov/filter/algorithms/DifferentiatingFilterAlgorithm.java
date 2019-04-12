package ru.nsu.fit.semenov.filter.algorithms;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.semenov.filter.util.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

import static ru.nsu.fit.semenov.filter.util.ImageUtils.COLOR_COMPONENTS_NUM;

public class DifferentiatingFilterAlgorithm implements Algorithm {

    public enum DifferentiatingFilterType {

        ROBERTS_OPERATOR,
        SOBEL_OPERATOR,
        BORDER_SELECTION

    }

    private final int limit;
    private final DifferentiatingFilterType filterType;
    private int[][][] colorComponents;
    private int imageWidth;
    private int imageHeight;

    public DifferentiatingFilterAlgorithm(int limit, DifferentiatingFilterType filterType) {
        this.limit = limit;
        this.filterType = filterType;
    }

    @Override
    public @NotNull BufferedImage apply(@NotNull BufferedImage sourceImage) {
        colorComponents = ImageUtils.extractColorComponents(sourceImage);
        BufferedImage resultImage = new BufferedImage(
                sourceImage.getWidth(),
                sourceImage.getHeight(),
                sourceImage.getType()
        );
        // TODO add greyscale
        imageWidth = sourceImage.getWidth();
        imageHeight = sourceImage.getHeight();
        for (int x = 0; x < sourceImage.getWidth(); ++x) {
            for (int y = 0; y < sourceImage.getHeight(); ++y) {
                float[] newColor = new float[COLOR_COMPONENTS_NUM];
                for (int i = 0; i < COLOR_COMPONENTS_NUM; ++i) {
                    int diff;
                    switch (filterType) {
                        case ROBERTS_OPERATOR:
                            diff = robertsOperator(x, y, i);
                            break;
                        case SOBEL_OPERATOR:
                            diff = sobelOperator(x, y, i);
                            break;
                        case BORDER_SELECTION:
                            diff = borderSelection(x, y, i);
                            break;
                        default:
                            throw new AssertionError("Invalid filter type specified");
                    }
                    newColor[i] = diff > limit ? 1f : 0f;
                }
                resultImage.setRGB(x, y, new Color(newColor[0], newColor[1], newColor[2]).getRGB());
            }
        }
        return resultImage;
    }

    private int robertsOperator(int x, int y, int i) {
        if (x < imageWidth - 1 && y < imageHeight - 1) {
            return Math.abs(colorComponents[x][y][i] - colorComponents[x + 1][y + 1][i])
                    + Math.abs(colorComponents[x + 1][y][i] - colorComponents[x][y + 1][i]);
        } else {
            return 0;
        }
    }

    private int sobelOperator(int x, int y, int i) {
        if (x < imageWidth - 1 && x > 0 && y < imageHeight - 1 && y > 0) {
            int Sx = (colorComponents[x + 1][y - 1][i] + 2 * colorComponents[x + 1][y][i] + colorComponents[x + 1][y + 1][i]) -
                    (colorComponents[x - 1][y - 1][i] + 2 * colorComponents[x - 1][y][i] + colorComponents[x - 1][y + 1][i]);
            int Sy = (colorComponents[x - 1][y + 1][i] + 2 * colorComponents[x][y + 1][i] + colorComponents[x + 1][y + 1][i]) -
                    (colorComponents[x - 1][y - 1][i] + 2 * colorComponents[x][y - 1][i] + colorComponents[x + 1][y - 1][i]);
            return Math.abs(Sx) + Math.abs(Sy);
        } else {
            return 0;
        }
    }

    private int borderSelection(int x, int y, int i) {
        if (x < imageWidth - 1 && x > 0 && y < imageHeight - 1 && y > 0) {
            return (4 * colorComponents[x][y][i] -
                    colorComponents[x + 1][y][i] -
                    colorComponents[x - 1][y][i] -
                    colorComponents[x][y + 1][i] -
                    colorComponents[x][y - 1][i]) / 4;
        } else {
            return 0;
        }
    }

}
