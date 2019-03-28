package ru.nsu.fit.g16205.semenov.life.model;

public class Restrictions {
    private static final int MAX_FIELD_HEIGHT = 200;
    private static final int MAX_FIELD_WIDTH = 200;
    private static final int MIN_CELL_SIZE_TO_SHOW_IMPACT = 30;
    private static final double MAX_FIRST_IMPACT_VALUE = 20.0;
    private static final double MAX_SECOND_IMPACT_VALUE = 20.0;
    private static final double MAX_BIRTH_BEGIN_VALUE = 100.0;
    private static final double MAX_BIRTH_END_VALUE = 100.0;
    private static final double MAX_LIFE_BEGIN_VALUE = 100.0;
    private static final double MAX_LIFE_END_VALUE = 100.0;
    private static final int MIN_BORDER_SIZE = 1;
    private static final int MAX_BORDER_SIZE = 10;
    private static final int MIN_CELL_SIZE = 1;
    private static final int MAX_CELL_SIZE = 100;

    private final int maxFieldHeight;
    private final int maxFieldWidth;
    private final int minCellSizeToShowImpact;
    private final double maxFirstImpactValue;
    private final double maxSecondImpactValue;
    private final double maxBirthBeginValue;
    private final double maxBirthEndValue;
    private final double maxLifeBeginValue;
    private final double maxLifeEndValue;
    private final int minBorderSize;
    private final int maxBorderSize;
    private final int minCellSize;
    private final int maxCellSize;

    public Restrictions() {
        maxFieldHeight = MAX_FIELD_HEIGHT;
        maxFieldWidth = MAX_FIELD_WIDTH;
        minCellSizeToShowImpact = MIN_CELL_SIZE_TO_SHOW_IMPACT;
        maxFirstImpactValue = MAX_FIRST_IMPACT_VALUE;
        maxSecondImpactValue = MAX_SECOND_IMPACT_VALUE;
        maxBirthBeginValue = MAX_BIRTH_BEGIN_VALUE;
        maxBirthEndValue = MAX_BIRTH_END_VALUE;
        maxLifeBeginValue = MAX_LIFE_BEGIN_VALUE;
        maxLifeEndValue = MAX_LIFE_END_VALUE;
        minBorderSize = MIN_BORDER_SIZE;
        maxBorderSize = MAX_BORDER_SIZE;
        minCellSize = MIN_CELL_SIZE;
        maxCellSize = MAX_CELL_SIZE;
    }

    public Restrictions(int maxFieldHeight, int maxFieldWidth, int minCellSizeToShowImpact, double maxFirstImpactValue, double maxSecondImpactValue, double maxBirthBeginValue, double maxBirthEndValue, double maxLifeBeginValue, double maxLifeEndValue, int minBorderSize, int maxBorderSize, int minCellSize, int maxCellSize) {
        this.maxFieldHeight = maxFieldHeight;
        this.maxFieldWidth = maxFieldWidth;
        this.minCellSizeToShowImpact = minCellSizeToShowImpact;
        this.maxFirstImpactValue = maxFirstImpactValue;
        this.maxSecondImpactValue = maxSecondImpactValue;
        this.maxBirthBeginValue = maxBirthBeginValue;
        this.maxBirthEndValue = maxBirthEndValue;
        this.maxLifeBeginValue = maxLifeBeginValue;
        this.maxLifeEndValue = maxLifeEndValue;
        this.minBorderSize = minBorderSize;
        this.maxBorderSize = maxBorderSize;
        this.minCellSize = minCellSize;
        this.maxCellSize = maxCellSize;
    }

    public int getMaxFieldHeight() {
        return maxFieldHeight;
    }

    public int getMaxFieldWidth() {
        return maxFieldWidth;
    }

    public int getMinCellSizeToShowImpact() {
        return minCellSizeToShowImpact;
    }

    public double getMaxFirstImpactValue() {
        return maxFirstImpactValue;
    }

    public double getMaxSecondImpactValue() {
        return maxSecondImpactValue;
    }

    public double getMaxBirthBeginValue() {
        return maxBirthBeginValue;
    }

    public double getMaxBirthEndValue() {
        return maxBirthEndValue;
    }

    public double getMaxLifeBeginValue() {
        return maxLifeBeginValue;
    }

    public double getMaxLifeEndValue() {
        return maxLifeEndValue;
    }

    public int getMinBorderSize() {
        return minBorderSize;
    }

    public int getMaxBorderSize() {
        return maxBorderSize;
    }

    public int getMinCellSize() {
        return minCellSize;
    }

    public int getMaxCellSize() {
        return maxCellSize;
    }
}