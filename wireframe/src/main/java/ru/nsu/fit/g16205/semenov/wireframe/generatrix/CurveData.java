package ru.nsu.fit.g16205.semenov.wireframe.generatrix;

public class CurveData {

    private final CurveParameters parameters;
    private final BezierCurve curve;

    public CurveData(CurveParameters parameters, BezierCurve curve) {
        this.parameters = parameters;
        this.curve = curve;
    }

    public CurveParameters getParameters() {
        return parameters;
    }

    public BezierCurve getCurve() {
        return curve;
    }

}
