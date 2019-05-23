package ru.nsu.fit.g16205.semenov.wireframe;

import org.apache.commons.lang3.tuple.Pair;
import org.ejml.simple.SimpleMatrix;
import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.wireframe.model.camera.CameraPosition;
import ru.nsu.fit.g16205.semenov.wireframe.model.camera.PyramidOfView;
import ru.nsu.fit.g16205.semenov.wireframe.model.figure.FigureData;
import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.*;
import ru.nsu.fit.g16205.semenov.wireframe.utils.transformer.CoordsTransformer;
import ru.nsu.fit.g16205.semenov.wireframe.utils.transformer.CoordsTransformerImpl;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static ru.nsu.fit.g16205.semenov.wireframe.camera.CameraTransformer.getToCameraCoordsMatrix;
import static ru.nsu.fit.g16205.semenov.wireframe.camera.ViewPortProjector.getProjectionMatrix;
import static ru.nsu.fit.g16205.semenov.wireframe.utils.VectorUtils.homogenToPoint3D;

public class ProjectionCreator {

    private ProjectionCreator() {
    }

    public static List<Pair<IntPoint, IntPoint>> getProjectedLines(
            @NotNull List<Pair<SimpleMatrix, SimpleMatrix>> figureInWorldHomogen,
            @NotNull CameraPosition cameraPosition,
            @NotNull PyramidOfView pyramidOfView,
            @NotNull Dimension imageSize
    ) {
        final List<Pair<IntPoint, IntPoint>> result = new ArrayList<>();
        final CoordsTransformer coordsTransformer = new CoordsTransformerImpl(
                new DoubleRectangle(-1, -1, 2, 2),
                new IntRectangle(0, 0, imageSize.width, imageSize.height)
        );
        final SimpleMatrix mProj = getProjectionMatrix(pyramidOfView);
        final SimpleMatrix mCam = getToCameraCoordsMatrix(cameraPosition);
        final SimpleMatrix resultingMatrix = mProj.mult(mCam);

        for (Pair<SimpleMatrix, SimpleMatrix> line : figureInWorldHomogen) {
            System.out.println("LEFT B4" + line.getLeft());
            System.out.println("RIGHT B4" + line.getRight());

            DoublePoint3D leftPoint = homogenToPoint3D(resultingMatrix.mult(line.getLeft()));
            DoublePoint3D rightPoint = homogenToPoint3D(resultingMatrix.mult(line.getRight()));

            System.out.println("LEFT AFTER" + leftPoint);
            System.out.println("RIGHT AFTER" + rightPoint);

            if (isVisible(leftPoint) && isVisible(rightPoint)) {
                continue;
            }
            result.add(Pair.of(
                    coordsTransformer.toPixel(leftPoint.getX(), leftPoint.getY()),
                    coordsTransformer.toPixel(rightPoint.getX(), rightPoint.getY())
            ));
        }
        return result;
    }

    public static void drawProjection(
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

    public static void drawProjection(
            @NotNull BufferedImage image,
            @NotNull FigureData figureData,
            @NotNull CameraPosition cameraPosition,
            @NotNull PyramidOfView pyramidOfView,
            @NotNull Color color
    ) {
        final List<Pair<IntPoint, IntPoint>> linesOnViewPort = getProjectedLines(
                SurfaceCreator.createSurface(figureData),
                cameraPosition,
                pyramidOfView,
                new Dimension(image.getWidth(), image.getHeight())
        );
        drawProjection(image, linesOnViewPort, color);
    }

    private static boolean isVisible(DoublePoint3D point3D) {
        return (point3D.getX() >= -1 &&
                point3D.getX() <= 1 &&
                point3D.getY() >= -1 &&
                point3D.getY() <= 1 &&
                point3D.getZ() >= 0 &&
                point3D.getZ() <= 1
        );
    }

}
