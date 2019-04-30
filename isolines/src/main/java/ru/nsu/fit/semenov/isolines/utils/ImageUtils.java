package ru.nsu.fit.semenov.isolines.utils;

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

}
