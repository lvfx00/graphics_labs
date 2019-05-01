package ru.nsu.fit.semenov.isolines;

public class BoundedFunctionImpl implements BoundedFunction {

    @FunctionalInterface
    public interface DoubleBiFunction {

        double apply(double x, double y);

    }

    private final DoubleBiFunction function;
    private final double minX;
    private final double maxX;
    private final double minY;
    private final double maxY;
    private final double minValue;
    private final double maxValue;

    public BoundedFunctionImpl(
            DoubleBiFunction function,
            double minX,
            double maxX,
            double minY,
            double maxY,
            double minValue,
            double maxValue
    ) {
        this.function = function;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public double apply(double x, double y) {
        return function.apply(x, y);
    }

    @Override
    public double getMinX() {
        return minX;
    }

    @Override
    public double getMaxX() {
        return maxX;
    }

    @Override
    public double getMinY() {
        return minY;
    }

    @Override
    public double getMaxY() {
        return maxY;
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
