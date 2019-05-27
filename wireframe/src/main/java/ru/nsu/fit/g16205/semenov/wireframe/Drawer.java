package ru.nsu.fit.g16205.semenov.wireframe;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.wireframe.camera.CameraTransformer;
import ru.nsu.fit.g16205.semenov.wireframe.model.figure.BezierCurve;
import ru.nsu.fit.g16205.semenov.wireframe.model.figure.FigureData;
import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.*;
import ru.nsu.fit.g16205.semenov.wireframe.utils.CoordsTransformer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.nsu.fit.g16205.semenov.wireframe.SurfaceCreator.createSurface;
import static ru.nsu.fit.g16205.semenov.wireframe.SurfaceCreator.normalizeSurface;

public class Drawer {

    private static final Color CIRCLE_COLOR = Color.RED;
    private static final Color INACTIVE_CURVE_COLOR = Color.LIGHT_GRAY;
    private static final Color ACTIVE_CURVE_COLOR = Color.CYAN;
    private static final double PARAMETER_STEP = 1. / 1000;

    private Drawer() {
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

    public static void drawFigure(
            @NotNull BufferedImage image,
            @NotNull FigureData figureData,
            @NotNull CameraTransformer cameraTransformer
    ) {
        drawProjection(
                image,
                cameraTransformer.worldToViewPort(
                        normalizeSurface(createSurface(figureData)),
                        new Dimension(image.getWidth(), image.getHeight()),
                        figureData.getParameters().getRotationAndShiftMatrix()
                ),
                figureData.getParameters().getColor()
        );
    }

    public static void drawWorldOrts(@NotNull BufferedImage image, @NotNull CameraTransformer cameraTransformer, int ortLen) {
        checkArgument(ortLen > 0, "Invalid ortLen specified: %s", ortLen);
        drawProjection(
                image,
                cameraTransformer.worldToViewPort(
                        Collections.singletonList(Pair.of(
                                new DoublePoint3D(0, 0, 0),
                                new DoublePoint3D(ortLen, 0, 0)
                        )),
                        new Dimension(image.getWidth(), image.getHeight()),
                        null
                ),
                Color.BLUE
        );
        drawProjection(
                image,
                cameraTransformer.worldToViewPort(
                        Collections.singletonList(Pair.of(
                                new DoublePoint3D(0, 0, 0),
                                new DoublePoint3D(0, 5, 0)
                        )),
                        new Dimension(image.getWidth(), image.getHeight()),
                        null
                ),
                Color.RED
        );
        drawProjection(
                image,
                cameraTransformer.worldToViewPort(
                        Collections.singletonList(Pair.of(
                                new DoublePoint3D(0, 0, 0),
                                new DoublePoint3D(0, 0, 5)
                        )),
                        new Dimension(image.getWidth(), image.getHeight()),
                        null
                ),
                Color.GREEN
        );
    }

    public static void drawCube(@NotNull BufferedImage image, @NotNull CameraTransformer cameraTransformer) {
        drawProjection(
                image,
                cameraTransformer.worldToViewPort(
                        Arrays.asList(
                                Pair.of(new DoublePoint3D(0, 0, 0), new DoublePoint3D(1, 0, 0)),
                                Pair.of(new DoublePoint3D(0, 0, 0), new DoublePoint3D(0, 1, 0)),
                                Pair.of(new DoublePoint3D(0, 0, 0), new DoublePoint3D(0, 0, 1)),
                                Pair.of(new DoublePoint3D(1, 0, 0), new DoublePoint3D(1, 1, 0)),
                                Pair.of(new DoublePoint3D(1, 0, 0), new DoublePoint3D(1, 0, 1)),
                                Pair.of(new DoublePoint3D(0, 1, 0), new DoublePoint3D(1, 1, 0)),
                                Pair.of(new DoublePoint3D(0, 1, 0), new DoublePoint3D(0, 1, 1)),
                                Pair.of(new DoublePoint3D(0, 0, 1), new DoublePoint3D(1, 0, 1)),
                                Pair.of(new DoublePoint3D(0, 0, 1), new DoublePoint3D(0, 1, 1)),
                                Pair.of(new DoublePoint3D(0, 1, 1), new DoublePoint3D(1, 1, 1)),
                                Pair.of(new DoublePoint3D(1, 1, 0), new DoublePoint3D(1, 1, 1)),
                                Pair.of(new DoublePoint3D(1, 0, 1), new DoublePoint3D(1, 1, 1))
                        ),
                        new Dimension(image.getWidth(), image.getHeight()),
                        null
                ),
                Color.MAGENTA
        );
    }

    private static void drawProjection(
            @NotNull BufferedImage image,
            @NotNull List<Pair<IntPoint, IntPoint>> lines,
            @NotNull Color color
    ) {
        final Graphics2D graphics2D = image.createGraphics();
        graphics2D.setColor(color);
        for (Pair<IntPoint, IntPoint> line : lines) {
            graphics2D.drawLine(
                    line.getLeft().getX(),
                    line.getLeft().getY(),
                    line.getRight().getX(),
                    line.getRight().getY()
            );
        }
        graphics2D.dispose();
    }

}
