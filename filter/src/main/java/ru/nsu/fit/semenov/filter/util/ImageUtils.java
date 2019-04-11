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

}
