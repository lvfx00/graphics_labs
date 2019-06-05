package ru.nsu.fit.g16205.semenov.raytracing;

import org.ejml.simple.SimpleMatrix;
import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.raytracing.camera.CameraTransformer;
import ru.nsu.fit.g16205.semenov.raytracing.model.camera.CameraParameters;
import ru.nsu.fit.g16205.semenov.raytracing.model.camera.CameraPosition;
import ru.nsu.fit.g16205.semenov.raytracing.model.camera.PyramidOfView;
import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.*;
import ru.nsu.fit.g16205.semenov.raytracing.model.tracing_primitives.Ray;
import ru.nsu.fit.g16205.semenov.raytracing.utils.CoordsTransformer;
import ru.nsu.fit.g16205.semenov.raytracing.utils.VectorUtils;

import java.awt.*;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.nsu.fit.g16205.semenov.raytracing.utils.ImageUtils.isOnImage;
import static ru.nsu.fit.g16205.semenov.raytracing.utils.VectorUtils.toMatrix;

public class InitialRaysCreator {
    private final CameraTransformer cameraTransformer;
    private final PyramidOfView pyramidOfView;
    private final CameraPosition cameraPosition;
    private final Dimension imageSize;
    private final CoordsTransformer coordsTransformer;

    public InitialRaysCreator(CameraTransformer cameraTransformer, CameraParameters cameraParameters, Dimension imageSize) {
        this.cameraTransformer = cameraTransformer;
        this.imageSize = imageSize;
        this.cameraPosition = cameraParameters.getCameraPosition();
        this.pyramidOfView = cameraParameters.getPyramidOfView();
        coordsTransformer = new CoordsTransformer(
                new DoubleRectangle(
                        -pyramidOfView.getSw() / 2,
                        -pyramidOfView.getSh() / 2,
                        pyramidOfView.getSw(),
                        pyramidOfView.getSh()
                ),
                new IntRectangle(0, 0, imageSize.width, imageSize.height)
        );
    }

    public @NotNull Ray createRayFromViewPortPixel(int x, int y) {
        checkArgument(isOnImage(x, y, imageSize), "Specified pixel isn't on image");
        final DoublePoint pointOnViewPort = coordsTransformer.toCoords(x, y);
        final DoublePoint3D pointInCameraCoords = new DoublePoint3D(
                pointOnViewPort.getX(),
                pointOnViewPort.getY(),
                pyramidOfView.getZb() + 1E-10
        );
        final DoublePoint3D pointInWorldCoords = cameraTransformer.cameraToWorld(pointInCameraCoords);
        final SimpleMatrix temp = toMatrix(pointInWorldCoords.minus(cameraPosition.getCameraPoint()));
        return new Ray(pointInWorldCoords, VectorUtils.toPoint3D(temp.divide(temp.normF())));
    }

}
