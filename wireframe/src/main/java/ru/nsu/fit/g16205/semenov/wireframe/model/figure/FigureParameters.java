package ru.nsu.fit.g16205.semenov.wireframe.model.figure;

import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.DoublePoint3D;

public class FigureParameters {

    private final double a;
    private final double b;
    private final double c;
    private final double d;
    private final int n;
    private final int m;
    private final int k;
    private final DoublePoint3D pointC;
    private final double thetaX;
    private final double thetaY;
    private final double thetaZ;

    public FigureParameters(
            double a,
            double b,
            double c,
            double d,
            int n,
            int m,
            int k,
            DoublePoint3D pointC,
            double thetaX,
            double thetaY,
            double thetaZ
    ) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.n = n;
        this.m = m;
        this.k = k;
        this.pointC = pointC;
        this.thetaX = thetaX;
        this.thetaY = thetaY;
        this.thetaZ = thetaZ;
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

    public DoublePoint3D getPointC() {
        return pointC;
    }

    public double getThetaX() {
        return thetaX;
    }

    public double getThetaY() {
        return thetaY;
    }

    public double getThetaZ() {
        return thetaZ;
    }

}
