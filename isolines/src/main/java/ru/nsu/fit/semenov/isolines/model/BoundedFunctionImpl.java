package ru.nsu.fit.semenov.isolines.model;

import ru.nsu.fit.semenov.isolines.utils.Rectangle;

public final class BoundedFunctionImpl implements BoundedFunction {

    private static final double X_STEPS = 1000;
    private static final double Y_STEPS = 1000;

    @FunctionalInterface
    public interface DoubleBiFunction {

        double apply(double x, double y);

    }

    private final DoubleBiFunction function;
    private final Rectangle domain;
    private final double minValue;
    private final double maxValue;

    public BoundedFunctionImpl(DoubleBiFunction function, Rectangle domain) {
        this.function = function;
        this.domain = domain;
        double minValue = Double.MAX_VALUE;
        double maxValue = -Double.MAX_VALUE;
        for (double x = domain.getX(); x <= domain.getX() + domain.getWidth(); x += domain.getWidth() / X_STEPS) {
            for (double y = domain.getY(); y <= domain.getY() + domain.getHeight(); y += domain.getHeight() / Y_STEPS) {
                double val = function.apply(x, y);
                if (val > maxValue) {
                    maxValue = val;
                }
                if (val < minValue) {
                    minValue = val;
                }
            }
        }
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public double apply(double x, double y) {
        return function.apply(x, y);
    }

    @Override
    public Rectangle getDomain() {
        return domain;
    }

    @Override
    public double getMinValue() {
        return minValue;
    }

    @Override
    public double getMaxValue() {
        return maxValue;
    }

}
