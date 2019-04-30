package ru.nsu.fit.semenov.isolines;

public interface BoundedFunction {

    double apply(double x, double y);

    double getMinX();

    double getMaxX();

    double getMinY();

    double getMaxY();

    double getMinValue();

    double getMaxValue();

}
