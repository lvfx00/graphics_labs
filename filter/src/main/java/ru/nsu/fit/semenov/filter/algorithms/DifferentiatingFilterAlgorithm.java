package ru.nsu.fit.semenov.filter.algorithms;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;

import static ru.nsu.fit.semenov.filter.util.ImageUtils.COLOR_COMPONENTS_NUM;

public class DifferentiatingFilterAlgorithm extends AbstractAlgorithm {

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
    protected void apply(@NotNull BufferedImage sourceImage, @NotNull BufferedImage resultImage) {
        colorComponents = new int[COLOR_COMPONENTS_NUM][sourceImage.getWidth()][sourceImage.getHeight()];
        for (int x = 0; x < sourceImage.getWidth(); ++x) {
            for (int y = 0; y < sourceImage.getHeight(); ++y) {
                Color color = new Color(sourceImage.getRGB(x, y));
                colorComponents[0][x][y] = color.getRed();
                colorComponents[1][x][y] = color.getGreen();
                colorComponents[2][x][y] = color.getBlue();
            }
        }
        imageWidth = sourceImage.getWidth();
        imageHeight = sourceImage.getHeight();
        for (int x = 0; x < sourceImage.getWidth(); ++x) {
            for (int y = 0; y < sourceImage.getHeight(); ++y) {
                float[] newColor = new float[COLOR_COMPONENTS_NUM];
                for (int i = 0; i < COLOR_COMPONENTS_NUM; ++i) {
                    switch (filterType) {
                        case ROBERTS_OPERATOR:
                            newColor[i] = robertsOperator(x, y, i);
                            break;
                        case SOBEL_OPERATOR:
                            newColor[i] = sobelOperator(x, y, i);
                            break;
                        case BORDER_SELECTION:
                            newColor[i] = borderSelection(x, y, i);
                            break;
                        default:
                            throw new AssertionError("Invalid filter type specified");
                    }
                }
                resultImage.setRGB(x, y, new Color(newColor[0], newColor[1], newColor[2]).getRGB());
            }
        }
    }

    private float robertsOperator(int x, int y, int i) {
        if (x < imageWidth - 1 && y < imageHeight - 1) {
            int F = Math.abs(colorComponents[i][x][y] - colorComponents[i][x + 1][y + 1])
                    + Math.abs(colorComponents[i][x + 1][y] - colorComponents[i][x][y + 1]);
            return F > limit ? 1f : 0f;
        } else {
            return 0f;
        }
    }

    private float sobelOperator(int x, int y, int i) {
        if (x < imageWidth - 1 && x > 0 && y < imageHeight - 1 && y > 0) {
            int S = Math.abs(
                    (colorComponents[i][x + 1][y - 1] + 2 * colorComponents[i][x + 1][y] + colorComponents[i][x + 1][y + 1]) -
                            (colorComponents[i][x - 1][y - 1] + 2 * colorComponents[i][x - 1][y] + colorComponents[i][x - 1][y + 1])) +
                    Math.abs(
                            (colorComponents[i][x - 1][y + 1] + 2 * colorComponents[i][x][y + 1] + colorComponents[i][x + 1][y + 1]) -
                                    (colorComponents[i][x - 1][y - 1] + 2 * colorComponents[i][x][y - 1] + colorComponents[i][x + 1][y - 1])
                    );
            return S > limit ? 1f : 0f;
        } else {
            return 0f;
        }
    }

    private float borderSelection(int x, int y, int i) {
        return 0f;
    }

}
