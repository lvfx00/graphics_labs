package ru.nsu.fit.g16205.semenov.raytracing.camera;

import org.ejml.simple.SimpleMatrix;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.nsu.fit.g16205.semenov.raytracing.model.camera.CameraParameters;
import ru.nsu.fit.g16205.semenov.raytracing.model.camera.CameraPosition;
import ru.nsu.fit.g16205.semenov.raytracing.model.camera.PyramidOfView;
import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.*;
import ru.nsu.fit.g16205.semenov.raytracing.utils.CoordsTransformer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.nsu.fit.g16205.semenov.raytracing.utils.VectorUtils.*;

public class CameraTransformer {

    private final @NotNull SimpleMatrix worldToCameraMatrix;
    private final @NotNull SimpleMatrix cameraToWorldMatrix;
    private final @NotNull SimpleMatrix resultingMatrix;

    public CameraTransformer(@NotNull CameraParameters cameraParameters) {
        worldToCameraMatrix = getToCameraCoordsMatrix(cameraParameters.getCameraPosition());
        cameraToWorldMatrix = worldToCameraMatrix.invert();
        final SimpleMatrix projectionMatrix = getProjectionMatrix(cameraParameters.getPyramidOfView());
        resultingMatrix = projectionMatrix.mult(worldToCameraMatrix);
    }
    public List<IntLine> worldToViewPort(
            @NotNull DoubleLine line,
            @NotNull Dimension imageSize,
            @Nullable SimpleMatrix preMatrix
    ) {
        return worldToViewPort(Collections.singletonList(line), imageSize, preMatrix);
    }

    public List<IntLine> worldToViewPort(
            @NotNull List<DoubleLine> figure,
            @NotNull Dimension imageSize,
            @Nullable SimpleMatrix preMatrix
    ) {
        final List<IntLine> result = new ArrayList<>();
        final CoordsTransformer coordsTransformer = new CoordsTransformer(
                new DoubleRectangle(-1, -1, 2, 2),
                new IntRectangle(0, 0, imageSize.width, imageSize.height)
        );
        SimpleMatrix matrix;
        if (preMatrix != null) {
            matrix = resultingMatrix.mult(preMatrix);
        } else {
            matrix = resultingMatrix;
        }
        for (DoubleLine line : figure) {
            DoublePoint3D leftPoint = homogenToPoint3D(matrix.mult(toHomogenColumnVector(line.getP1())));
            DoublePoint3D rightPoint = homogenToPoint3D(matrix.mult(toHomogenColumnVector(line.getP2())));
            // TODO cut lines to viewPort if they are partially in it
            if (isVisible(leftPoint) && isVisible(rightPoint)) {
                result.add(new IntLine(
                        coordsTransformer.toPixel(leftPoint.getX(), leftPoint.getY()),
                        coordsTransformer.toPixel(rightPoint.getX(), rightPoint.getY())
                ));
            }
        }
        return result;
    }

    public @NotNull DoublePoint3D cameraToWorld(@NotNull DoublePoint3D pointInCamera) {
        return homogenToPoint3D(cameraToWorldMatrix.mult(toHomogenColumnVector(pointInCamera)));
    }

    public @NotNull DoublePoint3D worldToCamera(@NotNull DoublePoint3D pointInWorld) {
        return homogenToPoint3D(worldToCameraMatrix.mult(toHomogenColumnVector(pointInWorld)));
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

    private static @NotNull SimpleMatrix getProjectionMatrix(@NotNull PyramidOfView pyramid) {
        final double sw = pyramid.getSw();
        final double sh = pyramid.getSh();
        final double zf = pyramid.getZf();
        final double zb = pyramid.getZb();
        return new SimpleMatrix(4, 4, true, new double[]{
                2 / sw * zb, 0, 0, 0,
                0, 2 / sh * zb, 0, 0,
                0, 0, zf / (zf - zb), -zf * zb / (zf - zb),
                0, 0, 1, 0
        });
    }

    private static @NotNull SimpleMatrix getToCameraCoordsMatrix(@NotNull CameraPosition cameraPosition) {
        final DoublePoint3D cameraPoint = cameraPosition.getCameraPoint();
        SimpleMatrix temp = toMatrix(cameraPosition.getViewPoint()).minus(toMatrix(cameraPoint)); // Pref - Peye !!!
        final SimpleMatrix wOrt = temp.divide(temp.normF());
        temp = crossProduct(toMatrix(cameraPosition.getUpVector()), wOrt);
        final SimpleMatrix uOrt = temp.divide(temp.normF());
        final SimpleMatrix vOrt = crossProduct(wOrt, uOrt);
        final SimpleMatrix rotateMatrix = new SimpleMatrix(4, 4, true, new double[]{
                uOrt.get(0, 0), uOrt.get(0, 1), uOrt.get(0, 2), 0,
                vOrt.get(0, 0), vOrt.get(0, 1), vOrt.get(0, 2), 0,
                wOrt.get(0, 0), wOrt.get(0, 1), wOrt.get(0, 2), 0,
                0, 0, 0, 1
        });
        final SimpleMatrix shiftMatrix = new SimpleMatrix(4, 4, true, new double[]{
                1, 0, 0, -cameraPoint.getX(),
                0, 1, 0, -cameraPoint.getY(),
                0, 0, 1, -cameraPoint.getZ(),
                0, 0, 0, 1
        });
        return rotateMatrix.mult(shiftMatrix);
    }

}
