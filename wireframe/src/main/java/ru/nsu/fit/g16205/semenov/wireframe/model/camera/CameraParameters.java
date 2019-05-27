package ru.nsu.fit.g16205.semenov.wireframe.model.camera;

public class CameraParameters {

    private final PyramidOfView pyramidOfView;
    private final CameraPosition cameraPosition;

    public CameraParameters(PyramidOfView pyramidOfView, CameraPosition cameraPosition) {
        this.pyramidOfView = pyramidOfView;
        this.cameraPosition = cameraPosition;
    }

    public PyramidOfView getPyramidOfView() {
        return pyramidOfView;
    }

    public CameraPosition getCameraPosition() {
        return cameraPosition;
    }

}
