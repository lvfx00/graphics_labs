package ru.nsu.fit.g16205.semenov.raytracing;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.raytracing.camera.CameraTransformer;
import ru.nsu.fit.g16205.semenov.raytracing.model.figure.FigureData;
import ru.nsu.fit.g16205.semenov.raytracing.model.tracing_primitives.Triangle3D;
import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.*;

import java.awt.*;
import java.awt.image.BufferedImage;
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

    public static void drawLines(
            @NotNull BufferedImage image,
            @NotNull CameraTransformer transformer,
            @NotNull List<DoubleLine> lines,
            @NotNull Color color
    ) {
        final Dimension imageSize = new Dimension(image.getWidth(), image.getHeight());
        drawProjection(image, transformer.worldToViewPort(lines, imageSize, null), color);
    }

    public static void drawTriangle(
            @NotNull BufferedImage image,
            @NotNull CameraTransformer cameraTransformer,
            @NotNull Triangle3D triangle
    ) {
        final Dimension imageSize = new Dimension(image.getWidth(), image.getHeight());
        final List<DoubleLine> lines = ImmutableList.of(
                new DoubleLine(triangle.getA(), triangle.getB()),
                new DoubleLine(triangle.getB(), triangle.getC()),
                new DoubleLine(triangle.getC(), triangle.getA())
        );
        drawProjection(image, cameraTransformer.worldToViewPort(lines, imageSize, null), Color.MAGENTA);
    }

    public static void drawWorldOrts(@NotNull BufferedImage image, @NotNull CameraTransformer transformer, int ortLen) {
        checkArgument(ortLen > 0, "Invalid ortLen specified: %s", ortLen);
        final Dimension imageSize = new Dimension(image.getWidth(), image.getHeight());
        final List<DoubleLine> xOrt = ImmutableList.of(new DoubleLine(new DoublePoint3D(0, 0, 0), new DoublePoint3D(ortLen, 0, 0)));
        final List<DoubleLine> yOrt = ImmutableList.of(new DoubleLine(new DoublePoint3D(0, 0, 0), new DoublePoint3D(0, ortLen, 0)));
        final List<DoubleLine> zOrt = ImmutableList.of(new DoubleLine(new DoublePoint3D(0, 0, 0), new DoublePoint3D(0, 0, ortLen)));
        drawProjection(image, transformer.worldToViewPort(yOrt, imageSize, null), Color.RED);
        drawProjection(image, transformer.worldToViewPort(xOrt, imageSize, null), Color.BLUE);
        drawProjection(image, transformer.worldToViewPort(zOrt, imageSize, null), Color.GREEN);
    }

    public static void drawCube(@NotNull BufferedImage image, @NotNull CameraTransformer cameraTransformer) {
        drawProjection(
                image,
                cameraTransformer.worldToViewPort(
                        ImmutableList.of(
                                new DoubleLine(new DoublePoint3D(0, 0, 0), new DoublePoint3D(1, 0, 0)),
                                new DoubleLine(new DoublePoint3D(0, 0, 0), new DoublePoint3D(0, 1, 0)),
                                new DoubleLine(new DoublePoint3D(0, 0, 0), new DoublePoint3D(0, 0, 1)),
                                new DoubleLine(new DoublePoint3D(1, 0, 0), new DoublePoint3D(1, 1, 0)),
                                new DoubleLine(new DoublePoint3D(1, 0, 0), new DoublePoint3D(1, 0, 1)),
                                new DoubleLine(new DoublePoint3D(0, 1, 0), new DoublePoint3D(1, 1, 0)),
                                new DoubleLine(new DoublePoint3D(0, 1, 0), new DoublePoint3D(0, 1, 1)),
                                new DoubleLine(new DoublePoint3D(0, 0, 1), new DoublePoint3D(1, 0, 1)),
                                new DoubleLine(new DoublePoint3D(0, 0, 1), new DoublePoint3D(0, 1, 1)),
                                new DoubleLine(new DoublePoint3D(0, 1, 1), new DoublePoint3D(1, 1, 1)),
                                new DoubleLine(new DoublePoint3D(1, 1, 0), new DoublePoint3D(1, 1, 1)),
                                new DoubleLine(new DoublePoint3D(1, 0, 1), new DoublePoint3D(1, 1, 1))
                        ),
                        new Dimension(image.getWidth(), image.getHeight()),
                        null
                ),
                Color.MAGENTA
        );
    }

    private static void drawProjection(
            @NotNull BufferedImage image,
            @NotNull List<IntLine> lines,
            @NotNull Color color
    ) {
        final Graphics2D graphics2D = image.createGraphics();
        graphics2D.setColor(color);
        for (IntLine line : lines) {
            graphics2D.drawLine(line.getP1().getX(), line.getP1().getY(), line.getP2().getX(), line.getP2().getY());
        }
        graphics2D.dispose();
    }

}
