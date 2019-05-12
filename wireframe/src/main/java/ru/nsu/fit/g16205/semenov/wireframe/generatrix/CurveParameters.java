package ru.nsu.fit.g16205.semenov.wireframe.generatrix;

public class CurveParameters {

    private final double a;
    private final double b;
    private final double c;
    private final double d;
    private final int n;
    private final int m;
    private final int k;

    public CurveParameters(double a, double b, double c, double d, int n, int m, int k) {
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
