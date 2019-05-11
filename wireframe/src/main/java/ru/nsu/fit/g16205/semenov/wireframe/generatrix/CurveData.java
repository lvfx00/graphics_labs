package ru.nsu.fit.g16205.semenov.wireframe.generatrix;

public class CurveData {

    private final BezierCurve bezierCurve;
    private final double a;
    private final double b;
    private final double c;
    private final double d;
    private final int n;
    private final int m;
    private final int k;

    public CurveData(BezierCurve bezierCurve, double a, double b, double c, double d, int n, int m, int k) {
        this.bezierCurve = bezierCurve;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.n = n;
        this.m = m;
        this.k = k;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }

    public double getD() {
        return d;
    }

    public BezierCurve getBezierCurve() {
        return bezierCurve;
    }

    public int getN() {
        return n;
    }

    public int getM() {
        return m;
    }

    public int getK() {
        return k;
    }

}
