package ru.nsu.fit.g16205.semenov.wireframe.model.camera;

public class CameraParameters {

    private PyramidOfView pyramidOfView;
    private CameraPosition cameraPosition;

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

    public void setPyramidOfView(PyramidOfView pyramidOfView) {
        this.pyramidOfView = pyramidOfView;
    }

    public void setCameraPosition(CameraPosition cameraPosition) {
        this.cameraPosition = cameraPosition;
    }

}
