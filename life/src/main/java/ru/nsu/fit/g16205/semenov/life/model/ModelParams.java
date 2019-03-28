package ru.nsu.fit.g16205.semenov.life.model;

public class ModelParams {
    private final double lifeBegin;
    private final double lifeEnd;
    private final double birthBegin;
    private final double birthEnd;
    private final double firstImpact;
    private final double secondImpact;

    public ModelParams(double lifeBegin, double lifeEnd, double birthBegin, double birthEnd, double firstImpact, double secondImpact) {
        this.lifeBegin = lifeBegin;
        this.lifeEnd = lifeEnd;
        this.birthBegin = birthBegin;
        this.birthEnd = birthEnd;
        this.firstImpact = firstImpact;
        this.secondImpact = secondImpact;
    }

    public double getLifeBegin() {
        return lifeBegin;
    }

    public double getLifeEnd() {
        return lifeEnd;
    }

    public double getBirthBegin() {
        return birthBegin;
    }

    public double getBirthEnd() {
        return birthEnd;
    }

    public double getFirstImpact() {
        return firstImpact;
    }

    public double getSecondImpact() {
        return secondImpact;
    }

    public static class Builder {
        private double lifeBegin = 2.0;
        private double lifeEnd = 3.3;
        private double birthBegin = 2.3;
        private double birthEnd = 2.9;
        private double firstImpact = 1.0;
        private double secondImpact = 0.3;

        public ModelParams build() {
            return new ModelParams(lifeBegin, lifeEnd, birthBegin, birthEnd, firstImpact, secondImpact);
        }

        public Builder setLifeBegin(double lifeBegin) {
            this.lifeBegin = lifeBegin;
            return this;
        }

        public Builder setLifeEnd(double lifeEnd) {
            this.lifeEnd = lifeEnd;
            return this;
        }

        public Builder setBirthBegin(double birthBegin) {
            this.birthBegin = birthBegin;
            return this;
        }

        public Builder setBirthEnd(double birthEnd) {
            this.birthEnd = birthEnd;
            return this;
        }

        public Builder setFirstImpact(double firstImpact) {
            this.firstImpact = firstImpact;
            return this;
        }

        public Builder setSecondImpact(double secondImpact) {
            this.secondImpact = secondImpact;
            return this;
        }
    }
}
