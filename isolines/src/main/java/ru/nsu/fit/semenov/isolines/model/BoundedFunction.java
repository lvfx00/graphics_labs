package ru.nsu.fit.semenov.isolines.model;

import ru.nsu.fit.semenov.isolines.utils.Rectangle;

public interface BoundedFunction {

    double apply(double x, double y);

    Rectangle getDomain();

    double getMinValue();

    double getMaxValue();

}
