package ru.nsu.fit.g16205.semenov.wireframe.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtils {

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

    public static BufferedImage concatImagesVertically(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() != img2.getWidth() || img1.getMinX() != img2.getMinX()) {
            throw new IllegalArgumentException("Invalid arguments specified");
        }

        BufferedImage newImage = new BufferedImage(
                img1.getWidth(),
                img1.getHeight() + img2.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        for (int x = 0; x < img1.getWidth(); ++x) {
            for (int y = 0; y < img1.getHeight(); ++y) {
                newImage.setRGB(x, y, img1.getRGB(x, y));
            }
            for (int y = 0; y < img2.getHeight(); ++y) {
                newImage.setRGB(x, y + img1.getHeight(), img2.getRGB(x, y));
            }
        }

        return newImage;
    }

}
