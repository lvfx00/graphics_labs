package ru.nsu.fit.semenov.isolines;

public class MyBoundedFunction implements BoundedFunction {
    @Override
    public double apply(double x, double y) {
        return Math.sin(x) * Math.cos(y);
    }

    @Override
    public double getMinX() {
        return -3;
    }

    @Override
    public double getMaxX() {
        return 3;
    }

    @Override
    public double getMinY() {
        return -3;
    }

    @Override
    public double getMaxY() {
        return 3;
    }

    @Override
    public double getMinValue() {
        return -1;
    }

    @Override
    public double getMaxValue() {
        return 1;
    }
}
