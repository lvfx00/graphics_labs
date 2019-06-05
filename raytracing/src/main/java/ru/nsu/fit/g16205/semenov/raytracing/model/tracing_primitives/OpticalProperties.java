package ru.nsu.fit.g16205.semenov.raytracing.model.tracing_primitives;

public class OpticalProperties {
    private final double kDiffuseRed;
    private final double kDiffuseGreen;
    private final double kDiffuseBlue;
    private final double kSpecularRed;
    private final double kSpecularGreen;
    private final double kSpecularBlue;
    private final int power;

    public OpticalProperties(
            double kDiffuseRed,
            double kDiffuseGreen,
            double kDiffuseBlue,
            double kSpecularRed,
            double kSpecularGreen,
            double kSpecularBlue,
            int power
    ) {
        this.kDiffuseRed = kDiffuseRed;
        this.kDiffuseGreen = kDiffuseGreen;
        this.kDiffuseBlue = kDiffuseBlue;
        this.kSpecularRed = kSpecularRed;
        this.kSpecularGreen = kSpecularGreen;
        this.kSpecularBlue = kSpecularBlue;
        this.power = power;
    }

    public double getkDiffuseRed() {
        return kDiffuseRed;
    }

    public double getkDiffuseGreen() {
        return kDiffuseGreen;
    }

    public double getkDiffuseBlue() {
        return kDiffuseBlue;
    }

    public double getkSpecularRed() {
        return kSpecularRed;
    }

    public double getkSpecularGreen() {
        return kSpecularGreen;
    }

    public double getkSpecularBlue() {
        return kSpecularBlue;
    }

    public int getPower() {
        return power;
    }
}
