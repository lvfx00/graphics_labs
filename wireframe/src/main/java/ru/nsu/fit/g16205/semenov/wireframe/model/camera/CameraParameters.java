package ru.nsu.fit.g16205.semenov.wireframe.model.camera;

import java.awt.*;

public class CameraParameters {

    private final PyramidOfView pyramidOfView;
    private final CameraPosition cameraPosition;
    private final Color backgroundColor;

    public CameraParameters(PyramidOfView pyramidOfView, CameraPosition cameraPosition, Color backgroundColor) {
        this.pyramidOfView = pyramidOfView;
        this.cameraPosition = cameraPosition;
        this.backgroundColor = backgroundColor;
    }

    public PyramidOfView getPyramidOfView() {
        return pyramidOfView;
    }

    public CameraPosition getCameraPosition() {
        return cameraPosition;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }
}
