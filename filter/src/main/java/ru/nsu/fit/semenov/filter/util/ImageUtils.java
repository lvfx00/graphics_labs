package ru.nsu.fit.semenov.filter.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

public class ImageUtils {
    public static int COLOR_COMPONENTS_NUM = 3;
    public static int BITS_PER_COLOR_COMPONENT = 8;
    public static int MAX_COLOR_VALUE = (1 << BITS_PER_COLOR_COMPONENT) - 1;

    public static final float RED_FACTOR = 0.299f;
    public static final float GREEN_FACTOR = 0.587f;
    public static final float BLUE_FACTOR = 0.114f;


    public static BufferedImage copyBufferedImage(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public static double getScaleRatio(Dimension imgSize, Dimension boundary) {
        int originalWidth = imgSize.width;
        int originalHeight = imgSize.height;
        int boundWidth = boundary.width;
        int boundHeight = boundary.height;
        int newWidth = originalWidth;
        int newHeight = originalHeight;

        if (originalWidth > boundWidth) {
            newWidth = boundWidth;
            newHeight = (newWidth * originalHeight) / originalWidth;
        }
        if (newHeight > boundHeight) {
            newHeight = boundHeight;
            newWidth = (newHeight * originalWidth) / originalHeight;
        }
        return ((double) newWidth) / originalWidth;
    }


    public static boolean isIndexValid(int x, int y, int width, int height) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public static int[][][] extractColorComponents(BufferedImage image) {
        final int[][][] colorComponents = new int[image.getWidth()][image.getHeight()][COLOR_COMPONENTS_NUM];
        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                Color color = new Color(image.getRGB(x, y));
                colorComponents[x][y][0] = color.getRed();
                colorComponents[x][y][1] = color.getGreen();
                colorComponents[x][y][2] = color.getBlue();
            }
        }
        return colorComponents;
    }

    public static Color getInterpolatedColor(double x, double y, BufferedImage image, Color defaultColor) {
        if (x < 0 || x > image.getWidth() - 1 || y < 0 || y > image.getHeight() - 1) {
            return defaultColor;
        }
        return new Color(image.getRGB((int) Math.round(x), (int) Math.round(y)));
    }

}
