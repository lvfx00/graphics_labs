package ru.nsu.fit.g16205.semenov.wireframe.utils;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class ImageUtils {

    public static BufferedImage resizeImage(@NotNull BufferedImage image, double ratio) {
        // TODO
        return null;
    }

    public static BufferedImage resizeImage(@NotNull BufferedImage image, int newWidth, int newHeight) {
        if (newWidth <= 0 || newHeight <= 0) {
            throw new IllegalArgumentException("Invalid parameters specified");
        }
        BufferedImage resultImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(((double) newWidth) / image.getWidth(), ((double) newHeight) / image.getHeight());
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        return scaleOp.filter(image, resultImage);
    }

    public static BufferedImage createOpaqueImage(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Invalid parameters specified");
        }
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setComposite(AlphaComposite.Clear);
        graphics2D.fillRect(0, 0, image.getWidth(), image.getHeight());
        graphics2D.dispose();
        return image;
    }

}
