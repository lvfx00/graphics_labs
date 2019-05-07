package ru.nsu.fit.g16205.semenov.wireframe.generatrix;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.wireframe.model.IntPoint;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;

public class PreviewCreator {

    private PreviewCreator() {
    }

    public static void drawCurveByPoints(
            @NotNull Collection<IntPoint> anchorPoints,
            @NotNull BufferedImage image,
            @NotNull Color curveColor
    ) {
        anchorPoints.forEach(p -> image.setRGB(p.getX(), p.getY(), curveColor.getRGB()));
    }

    public static void drawAnchorPoints(
            @NotNull Iterable<IntPoint> anchorPoints,
            @NotNull BufferedImage image,
            @NotNull Color pointColor,
            int radius
    ) {
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setColor(pointColor);
        anchorPoints.forEach(p -> graphics2D.drawOval(p.getX(), p.getY(), radius * 2, radius * 2));
        graphics2D.dispose();
    }

}
