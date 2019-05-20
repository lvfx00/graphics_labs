package ru.nsu.fit.g16205.semenov.wireframe;

import org.apache.commons.lang3.tuple.Pair;
import org.ejml.simple.SimpleMatrix;
import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.wireframe.model.camera.CameraPosition;
import ru.nsu.fit.g16205.semenov.wireframe.camera.CameraTransformer;
import ru.nsu.fit.g16205.semenov.wireframe.model.camera.PyramidOfView;
import ru.nsu.fit.g16205.semenov.wireframe.camera.ViewPortProjector;
import ru.nsu.fit.g16205.semenov.wireframe.model.figure.FigureData;
import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.*;
import ru.nsu.fit.g16205.semenov.wireframe.utils.VectorUtils;
import ru.nsu.fit.g16205.semenov.wireframe.utils.transformer.CoordsTransformer;
import ru.nsu.fit.g16205.semenov.wireframe.utils.transformer.CoordsTransformerImpl;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ProjectionCreator {

    private ProjectionCreator() {
    }

    public static void drawProjection(
            @NotNull BufferedImage image,
            @NotNull FigureData figureData,
            @NotNull CameraPosition cameraPosition,
            @NotNull PyramidOfView pyramidOfView
    ) {
        final CoordsTransformer coordsTransformer = new CoordsTransformerImpl(
                new DoubleRectangle(-1, -1, 2, 2),
                new IntRectangle(0, 0, image.getWidth(), image.getHeight())
        );
        final Graphics2D graphics2D = image.createGraphics();
        graphics2D.setColor(Color.BLACK);

        final SimpleMatrix Mproj = ViewPortProjector.getProjectionMatrix(pyramidOfView);
        final SimpleMatrix Mcam = CameraTransformer.getToCameraCoordsMatrix(cameraPosition);
        final SimpleMatrix resultingMatrix = Mproj.mult(Mcam);

        for (Pair<SimpleMatrix, SimpleMatrix> line : SurfaceCreator.createSurface(figureData)) {
            DoublePoint3D leftPoint = VectorUtils.homogeneousToPoint3D(resultingMatrix.mult(line.getLeft()));
            DoublePoint3D rightPoint = VectorUtils.homogeneousToPoint3D(resultingMatrix.mult(line.getRight()));
            if (isVisible(leftPoint) && isVisible(rightPoint)) {
                continue;
            }
            IntPoint leftOnImage = coordsTransformer.toPixel(leftPoint.getX(), leftPoint.getY());
            IntPoint rightOnImage = coordsTransformer.toPixel(rightPoint.getX(), rightPoint.getY());
            graphics2D.drawLine(leftOnImage.getX(), leftOnImage.getY(), rightOnImage.getX(), rightOnImage.getY());
        }
        graphics2D.dispose();
    }

    private static boolean isVisible(DoublePoint3D point3D) {
        return (point3D.getX() >= 0 &&
                point3D.getX() <= 1 &&
                point3D.getY() >= 0 &&
                point3D.getY() <= 1 &&
                point3D.getZ() >= 0 &&
                point3D.getZ() <= 1
        );
    }

}
