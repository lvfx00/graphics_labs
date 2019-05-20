package ru.nsu.fit.g16205.semenov.wireframe.camera;

import org.ejml.simple.SimpleMatrix;
import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.wireframe.model.camera.CameraPosition;
import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.DoublePoint3D;
import ru.nsu.fit.g16205.semenov.wireframe.utils.VectorUtils;

public class CameraTransformer {

    private CameraTransformer() {
    }

    public static @NotNull SimpleMatrix getToCameraCoordsMatrix(@NotNull CameraPosition cameraPosition) {
        final DoublePoint3D cameraPoint = cameraPosition.getCameraPoint();
        final SimpleMatrix PeyeMinusPref = VectorUtils.toMatrix(cameraPosition.getViewPoint())
                .minus(VectorUtils.toMatrix(cameraPoint));
        final SimpleMatrix wOrt = PeyeMinusPref.divide(PeyeMinusPref.normF());
        final SimpleMatrix rr = VectorUtils.crossProduct(VectorUtils.toMatrix(cameraPosition.getUpVector()), wOrt);
        final SimpleMatrix uOrt = rr.divide(rr.normF());
        final SimpleMatrix vOrt = VectorUtils.crossProduct(wOrt, uOrt);
        return new SimpleMatrix(
                4,
                4,
                true,
                new double[]{
                        uOrt.get(0, 0), uOrt.get(0, 1),
                        uOrt.get(0, 2), -uOrt.get(0, 0) * cameraPoint.getX(),

                        vOrt.get(0, 0), vOrt.get(0, 1),
                        vOrt.get(0, 2), -vOrt.get(0, 0) * cameraPoint.getY(),

                        wOrt.get(0, 0), wOrt.get(0, 1),
                        wOrt.get(0, 2), -wOrt.get(0, 0) * cameraPoint.getZ(),

                        0, 0, 0, 1
                }
        );
    }

}
