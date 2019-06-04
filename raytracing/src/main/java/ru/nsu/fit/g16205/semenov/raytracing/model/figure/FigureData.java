package ru.nsu.fit.g16205.semenov.raytracing.model.figure;

public class FigureData {

    private final BezierCurve curve;
    private final FigureParameters parameters;

    public FigureData(BezierCurve curve, FigureParameters parameters) {
        this.curve = curve;
        this.parameters = parameters;
    }

    public BezierCurve getCurve() {
        return curve;
    }

    public FigureParameters getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return parameters.getName();
    }
}
