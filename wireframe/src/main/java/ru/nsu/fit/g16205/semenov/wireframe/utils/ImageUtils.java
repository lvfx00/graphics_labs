package ru.nsu.fit.g16205.semenov.wireframe.utils;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class ImageUtils {

    public static @NotNull BufferedImage scaleImage(@NotNull BufferedImage image, double factor) {
        BufferedImage after = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(factor, factor);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        after = scaleOp.filter(image, after);
        return after;
    }

    public static BufferedImage createImage(Dimension d, Color color) {
        if (d.width <= 0 || d.height <= 0) {
            throw new IllegalArgumentException("Invalid parameters specified");
        }
        BufferedImage image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setColor(color);
        graphics2D.fillRect(0, 0, image.getWidth(), image.getHeight());
        graphics2D.dispose();
        return image;
    }

    public static double getScaleRatio(Dimension imgSize, Dimension boundary) {
        double originalWidth = imgSize.getWidth();
        double originalHeight = imgSize.getHeight();
        double boundWidth = boundary.getWidth();
        double boundHeight = boundary.getHeight();

        double newWidth = boundWidth;
        double newHeight = originalHeight * (newWidth / originalWidth);

        if (newHeight > boundHeight) {
            newHeight = boundHeight;
            newWidth = originalWidth * (newHeight / originalHeight);
        }

        return newWidth / originalWidth;
    }

}
