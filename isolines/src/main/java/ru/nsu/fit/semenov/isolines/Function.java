package ru.nsu.fit.semenov.isolines;

public interface Function {

    double apply(double x, double y);

    double getMin(double a, double b, double c, double d);

    double getMax(double a, double b, double c, double d);

}
