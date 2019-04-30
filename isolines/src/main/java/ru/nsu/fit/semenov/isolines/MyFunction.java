package ru.nsu.fit.semenov.isolines;

public class MyFunction implements Function {
    @Override
    public double apply(double x, double y) {
        return Math.sin(x) * Math.cos(y);
    }

    @Override
    public double getMin(double a, double b, double c, double d) {
        return -1;
    }

    @Override
    public double getMax(double a, double b, double c, double d) {
        return 1;
    }
}
