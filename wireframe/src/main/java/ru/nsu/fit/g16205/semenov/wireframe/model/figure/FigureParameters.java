package ru.nsu.fit.g16205.semenov.wireframe.model.figure;

import org.ejml.simple.SimpleMatrix;
import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.DoublePoint3D;

import java.awt.*;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

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
    private final Color color;
    private final String name;
    private final SimpleMatrix rotationAndShiftMatrix;

    public FigureParameters(
            double a,
            double b,
            double c,
            double d,
            int n,
            int m,
            int k,
            @NotNull DoublePoint3D pointC,
            double thetaX,
            double thetaY,
            double thetaZ,
            @NotNull Color color,
            @NotNull String name
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
        this.color = color;
        this.name = name;
        rotationAndShiftMatrix = getRotationAndShiftMatrix(pointC, thetaX, thetaY, thetaZ);
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

    public @NotNull DoublePoint3D getPointC() {
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

    public Color getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public @NotNull SimpleMatrix getRotationAndShiftMatrix() {
        return rotationAndShiftMatrix;
    }

    // TODO transpose rotation matrices???
    private static @NotNull SimpleMatrix getRotationAndShiftMatrix(
            @NotNull DoublePoint3D c,
            double thetaX,
            double thetaY,
            double thetaZ
    ) {
        final SimpleMatrix rotationXmatrix = new SimpleMatrix(4, 4, true, new double[]{
                1, 0, 0, 0,
                0, cos(thetaX), -sin(thetaX), 0,
                0, sin(thetaX), cos(thetaX), 0,
                0, 0, 0, 1
        });
        final SimpleMatrix rotationYmatrix = new SimpleMatrix(4, 4, true, new double[]{
                cos(thetaY), 0, sin(thetaY), 0,
                0, 1, 0, 0,
                -sin(thetaY), 0, cos(thetaY), 0,
                0, 0, 0, 1
        });
        final SimpleMatrix rotationZmatrix = new SimpleMatrix(4, 4, true, new double[]{
                cos(thetaZ), -sin(thetaZ), 0, 0,
                sin(thetaZ), cos(thetaZ), 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        });
        final SimpleMatrix shiftmatrix = new SimpleMatrix(4, 4, true, new double[]{
                1, 0, 0, c.getX(),
                0, 1, 0, c.getY(),
                0, 0, 1, c.getZ(),
                0, 0, 0, 1
        });
        return rotationXmatrix.mult(rotationYmatrix).mult(rotationZmatrix).mult(shiftmatrix);
    }

}
