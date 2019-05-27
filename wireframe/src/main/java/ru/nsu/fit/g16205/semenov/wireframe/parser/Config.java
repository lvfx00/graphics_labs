package ru.nsu.fit.g16205.semenov.wireframe.parser;

import ru.nsu.fit.g16205.semenov.wireframe.model.camera.CameraParameters;
import ru.nsu.fit.g16205.semenov.wireframe.model.figure.FigureData;

import java.util.List;

public class Config {

    private final List<FigureData> figureDataList;
    private final CameraParameters cameraParameters;

    public Config(List<FigureData> figureDataList, CameraParameters cameraParameters) {
        this.figureDataList = figureDataList;
        this.cameraParameters = cameraParameters;
    }

    public List<FigureData> getFigureDataList() {
        return figureDataList;
    }

    public CameraParameters getCameraParameters() {
        return cameraParameters;
    }

}
