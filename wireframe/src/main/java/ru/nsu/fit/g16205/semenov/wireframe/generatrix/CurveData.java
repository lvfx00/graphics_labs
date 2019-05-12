package ru.nsu.fit.g16205.semenov.wireframe.generatrix;

public class CurveData {

    private final BezierCurve curve;
    private final CurveParameters parameters;

    public CurveData(BezierCurve curve, CurveParameters parameters) {
        this.curve = curve;
        this.parameters = parameters;
    }

    public BezierCurve getCurve() {
        return curve;
    }

    public CurveParameters getParameters() {
        return parameters;
    }

}
