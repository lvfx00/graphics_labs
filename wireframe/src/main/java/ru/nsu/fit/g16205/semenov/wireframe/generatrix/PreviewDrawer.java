package ru.nsu.fit.g16205.semenov.wireframe.generatrix;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.wireframe.model.IntPoint;
import ru.nsu.fit.g16205.semenov.wireframe.utils.transformer.CoordsTransformer;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PreviewDrawer {

    private static final Color CIRCLE_COLOR = Color.RED;
    private static final Color INACTIVE_CURVE_COLOR = Color.LIGHT_GRAY;
    private static final Color ACTIVE_CURVE_COLOR = Color.CYAN;
    private static final double PARAMETER_STEP = 1. / 1000;

    private PreviewDrawer() {
    }

    public static void drawBezierCurve(
            @NotNull BufferedImage previewImage,
            @NotNull BezierCurve bezierCurve,
            @NotNull CoordsTransformer coordsTransformer,
            double partBegin,
            double partEnd
    ) {
        final double beginParameter = partBegin * bezierCurve.getTotalLength();
        final double endParameter = partEnd * bezierCurve.getTotalLength();
        for (double parameter = 0; parameter <= bezierCurve.getTotalLength(); parameter += PARAMETER_STEP) {
            IntPoint pixel = coordsTransformer.toPixel(
                    BezierCurve.Adapter2D.matrixToPoint(bezierCurve.getPoint(parameter))
            );
            if (parameter < beginParameter || parameter > endParameter) {
                previewImage.setRGB(pixel.getX(), pixel.getY(), INACTIVE_CURVE_COLOR.getRGB());
            } else {
                previewImage.setRGB(pixel.getX(), pixel.getY(), ACTIVE_CURVE_COLOR.getRGB());
            }
        }
    }

    public static void drawAnchorPointsCircles(
            @NotNull Iterable<IntPoint> anchorPoints,
            @NotNull BufferedImage image,
            int radius
    ) {
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setColor(CIRCLE_COLOR);
        IntPoint previous = null;
        for (IntPoint point : anchorPoints) {
            graphics2D.drawOval(point.getX() - radius, point.getY() - radius, radius * 2, radius * 2);
            if (previous != null) {
                graphics2D.drawLine(previous.getX(), previous.getY(), point.getX(), point.getY());
            }
            previous = point;
        }
        graphics2D.dispose();
    }

}
