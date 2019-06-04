package ru.nsu.fit.g16205.semenov.raytracing;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.raytracing.camera.CameraTransformer;
import ru.nsu.fit.g16205.semenov.raytracing.model.figure.BezierCurve;
import ru.nsu.fit.g16205.semenov.raytracing.model.figure.FigureData;
import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.*;
import ru.nsu.fit.g16205.semenov.raytracing.utils.CoordsTransformer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.nsu.fit.g16205.semenov.raytracing.SurfaceCreator.createSurface;
import static ru.nsu.fit.g16205.semenov.raytracing.SurfaceCreator.normalizeSurface;

public class Drawer {

    private Drawer() {
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

    public static void drawLine(
            @NotNull BufferedImage image,
            @NotNull CameraTransformer cameraTransformer,
            @NotNull Pair<DoublePoint3D, DoublePoint3D> line
    ) {
        drawProjection(
                image,
                cameraTransformer.worldToViewPort(
                        ImmutableList.of(line),
                        new Dimension(image.getWidth(), image.getHeight()),
                        null
                ),
                Color.MAGENTA
        );
    }

    public static void drawTriangle(
            @NotNull BufferedImage image,
            @NotNull CameraTransformer cameraTransformer,
            @NotNull Triangle3D triangle
    ) {
        drawProjection(
                image,
                cameraTransformer.worldToViewPort(
                        ImmutableList.of(
                                Pair.of(triangle.getA(), triangle.getB()),
                                Pair.of(triangle.getB(), triangle.getC()),
                                Pair.of(triangle.getC(), triangle.getA())
                        ),
                        new Dimension(image.getWidth(), image.getHeight()),
                        null
                ),
                Color.MAGENTA
        );
    }

    public static void drawWorldOrts(@NotNull BufferedImage image, @NotNull CameraTransformer transformer, int ortLen) {
        checkArgument(ortLen > 0, "Invalid ortLen specified: %s", ortLen);
        drawProjection(
                image,
                transformer.worldToViewPort(
                        ImmutableList.of(
                                Pair.of(new DoublePoint3D(0, 0, 0), new DoublePoint3D(ortLen, 0, 0)),
                                Pair.of(new DoublePoint3D(0, 0, 0), new DoublePoint3D(0, ortLen, 0)),
                                Pair.of(new DoublePoint3D(0, 0, 0), new DoublePoint3D(0, 0, ortLen))
                        ),
                        new Dimension(image.getWidth(), image.getHeight()),
                        null
                ),
                Color.BLUE
        );
    }

    public static void drawCube(@NotNull BufferedImage image, @NotNull CameraTransformer cameraTransformer) {
        drawProjection(
                image,
                cameraTransformer.worldToViewPort(
                        ImmutableList.of(
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
