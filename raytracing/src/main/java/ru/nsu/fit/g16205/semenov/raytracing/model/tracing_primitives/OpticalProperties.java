package ru.nsu.fit.g16205.semenov.raytracing.model.tracing_primitives;

public class OpticalProperties {
    private final double[] kDiffuse = new double[3];
    private final double[] kSpecular = new double[3];
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
        this.kDiffuse[0] = kDiffuseRed;
        this.kDiffuse[1] = kDiffuseGreen;
        this.kDiffuse[2] = kDiffuseBlue;
        this.kSpecular[0] = kSpecularRed;
        this.kSpecular[1] = kSpecularGreen;
        this.kSpecular[2] = kSpecularBlue;
        this.power = power;
    }

    public double[] getkDiffuse() {
        return kDiffuse;
    }

    public double[] getkSpecular() {
        return kSpecular;
    }

    public int getPower() {
        return power;
    }
}
