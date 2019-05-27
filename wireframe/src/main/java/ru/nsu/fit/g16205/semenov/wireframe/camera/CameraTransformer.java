package ru.nsu.fit.g16205.semenov.wireframe.camera;

import org.apache.commons.lang3.tuple.Pair;
import org.ejml.simple.SimpleMatrix;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.nsu.fit.g16205.semenov.wireframe.model.camera.CameraParameters;
import ru.nsu.fit.g16205.semenov.wireframe.model.camera.CameraPosition;
import ru.nsu.fit.g16205.semenov.wireframe.model.camera.PyramidOfView;
import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.DoublePoint3D;
import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.DoubleRectangle;
import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.IntPoint;
import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.IntRectangle;
import ru.nsu.fit.g16205.semenov.wireframe.utils.CoordsTransformer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static ru.nsu.fit.g16205.semenov.wireframe.utils.VectorUtils.*;

public class CameraTransformer {

    private final @NotNull SimpleMatrix resultingMatrix;

    public CameraTransformer(@NotNull CameraParameters cameraParameters) {
        final SimpleMatrix toCameraCoordsMatrix = getToCameraCoordsMatrix(cameraParameters.getCameraPosition());
        final SimpleMatrix projectionMatrix = getProjectionMatrix(cameraParameters.getPyramidOfView());
        resultingMatrix = projectionMatrix.mult(toCameraCoordsMatrix);
    }

    // TODO asserts that figure points' matrices are homogeneous column vectors
    public List<Pair<IntPoint, IntPoint>> worldToViewPort(
            @NotNull List<Pair<DoublePoint3D, DoublePoint3D>> figure,
            @NotNull Dimension imageSize,
            @Nullable SimpleMatrix preMatrix
    ) {
        final List<Pair<IntPoint, IntPoint>> result = new ArrayList<>();
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

        for (Pair<DoublePoint3D, DoublePoint3D> line : figure) {
            DoublePoint3D leftPoint = homogenToPoint3D(
                    matrix.mult(toHomogenColumnVector(line.getLeft()))
            );
            DoublePoint3D rightPoint = homogenToPoint3D(
                    matrix.mult(toHomogenColumnVector(line.getRight()))
            );
            if (isVisible(leftPoint) && isVisible(rightPoint)) {
                result.add(Pair.of(
                        coordsTransformer.toPixel(leftPoint.getX(), leftPoint.getY()),
                        coordsTransformer.toPixel(rightPoint.getX(), rightPoint.getY())
                ));
            }
        }
        return result;
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

    // TODO check valid
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
