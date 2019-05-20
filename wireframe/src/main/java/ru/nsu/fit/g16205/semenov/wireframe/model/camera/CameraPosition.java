package ru.nsu.fit.g16205.semenov.wireframe.model.camera;

import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.DoublePoint3D;

public class CameraPosition {

    private DoublePoint3D cameraPoint;
    private DoublePoint3D viewPoint;
    private DoublePoint3D upVector;

    public CameraPosition(DoublePoint3D cameraPoint, DoublePoint3D viewPoint, DoublePoint3D upVector) {
        this.cameraPoint = cameraPoint;
        this.viewPoint = viewPoint;
        this.upVector = upVector;
    }

    public DoublePoint3D getCameraPoint() {
        return cameraPoint;
    }

    public DoublePoint3D getViewPoint() {
        return viewPoint;
    }

    public DoublePoint3D getUpVector() {
        return upVector;
    }

}
