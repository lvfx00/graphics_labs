package ru.nsu.fit.g16205.semenov.wireframe.generatrix;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.wireframe.model.IntPoint;
import ru.nsu.fit.g16205.semenov.wireframe.utils.transformer.CoordsTransformer;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PreviewCreator {

    private PreviewCreator() {
    }

    public static void drawBezierCurve(
            @NotNull BezierCurve curve,
            @NotNull BufferedImage image,
            @NotNull CoordsTransformer transformer,
            @NotNull Color activeColor,
            @NotNull Color inactiveColor,
            double startActive,
            double endActive,
            int stepsNum
    ) {
        final double curveLenght = curve.getTotalLength();
        final int beforeActiveStepsNum = (int) Math.floor(stepsNum * startActive / curveLenght);
        final int afterActiveStepsNum = (int) Math.floor(stepsNum * (curveLenght - endActive) / curveLenght);
        final int activeStepsNum = stepsNum - beforeActiveStepsNum - afterActiveStepsNum;

        drawBezierCurve(curve, image, transformer, inactiveColor, 0, startActive, beforeActiveStepsNum);
        drawBezierCurve(curve, image, transformer, activeColor, startActive, endActive, activeStepsNum);
        drawBezierCurve(curve, image, transformer, inactiveColor, endActive, curveLenght, afterActiveStepsNum);
    }

    private static void drawBezierCurve(
            @NotNull BezierCurve curve,
            @NotNull BufferedImage image,
            @NotNull CoordsTransformer transformer,
            @NotNull Color color,
            double start,
            double end,
            int stepsNum
    ) {
//        if (start < 0 || end > curve.getTotalLength() || end < start) {
//            System.out.println("start = " + start);
//            System.out.println("end = " + end);
//            throw new IllegalArgumentException("Invalid parameter range specified");
//        }
        double parameterStep = (end - start) / stepsNum;
        for (double parameter = start; parameter <= end; parameter += parameterStep) {
            IntPoint pixel = transformer.toPixel(
                    BezierCurve2DAdapter.matrixToPoint(
                            curve.getPoint(parameter)
                    )
            );
            image.setRGB(pixel.getX(), pixel.getY(), color.getRGB());
        }
    }

    public static void drawAnchorPointsCircles(
            @NotNull Iterable<IntPoint> anchorPoints,
            @NotNull BufferedImage image,
            @NotNull Color circleColor,
            int radius
    ) {
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setColor(circleColor);
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
