package ru.nsu.fit.g16205.semenov.life.my_model;

import ru.nsu.fit.g16205.semenov.life.model.*;
import ru.nsu.fit.g16205.semenov.life.util.Coord;

public class GameModelImpl implements GameModel {
    private static final Coord[] wideRowFirstNeighboursTopBottom = {
            new Coord(0, -1),
            new Coord(0, 1),

            new Coord(-1, -1),
            new Coord(-1, 1),
    };

    private static final Coord[] horizontalFirstNeighbours = {
            new Coord(1, 0),
            new Coord(-1, 0),
    };

    private static final Coord[] wideRowSecondNeighboursTopBottom = {
            new Coord(1, -1),
            new Coord(1, 1),

            new Coord(-2, -1),
            new Coord(-2, 1),
    };

    private static final Coord[] verticalSecondNeighbours = {
            new Coord(0, -2),
            new Coord(0, 2),
    };

    private GameField gameField;
    final private Field<Double> impactField;
    private final ModelParams modelParams;

    public GameModelImpl(FieldParams fieldParams, ModelParams modelParams) {
        gameField = new GameFieldImpl(fieldParams);
        this.modelParams = modelParams;

        impactField = new FieldImpl<>(fieldParams,
                (dimensionOne, dimensionTwo) -> new Double[dimensionOne][dimensionTwo]);
        calculateImpact();
    }

    @Override
    public void setCell(int x, int y, boolean value) {
        gameField.setCell(x, y, value);
        calculateImpact();
    }

    @Override
    public boolean getCell(int x, int y) {
        return gameField.getCell(x, y);
    }

    @Override
    public GameField getGameField() {
        return gameField;
    }

    @Override
    public ImmutableField<Double> getImpacts() {
        return impactField;
    }

    @Override
    public void iterate() {
        final GameField newField = new GameFieldImpl(gameField.getParams());

        for (int y = 0; y < gameField.getHeight(); ++y) {
            for (int x = 0; x < gameField.getWidth() - (y % 2); ++x) {
                double impact = impactField.getCell(x, y);
                if (getCell(x, y)) { // alive now
                    if (impact < modelParams.getLifeBegin() || impact > modelParams.getLifeEnd()) {
                        newField.setCell(x, y, false);
                    } else {
                        newField.setCell(x, y, true);
                    }
                } else { // dead now
                    if (impact < modelParams.getBirthBegin() || impact > modelParams.getBirthEnd()) {
                        newField.setCell(x, y, false);
                    } else {
                        newField.setCell(x, y, true);
                    }
                }
            }
        }
        gameField = newField;
        calculateImpact();
    }

    @Override
    public void clear() {
        gameField.clear();
    }

    private int countFirstNeighbours(int x, int y) {
        return calculateNeighbours(x, y, horizontalFirstNeighbours, wideRowFirstNeighboursTopBottom);
    }

    private int countSecondNeighbours(int x, int y) {
        return calculateNeighbours(x, y, verticalSecondNeighbours, wideRowSecondNeighboursTopBottom);
    }

    private int calculateNeighbours(int x, int y, Coord[] deltasIndependent, Coord[] deltas) {
        int count = 0;
        for (Coord c : deltasIndependent) {
            int newX = x + c.getX();
            int newY = y + c.getY();
            if (gameField.validateIndexes(newX, newY) && gameField.getCell(newX, newY)) {
                ++count;
            }
        }
        for (Coord c : deltas) {
            int newX = x + ((y % 2 == 0) ? c.getX() : c.getX() + 1);
            int newY = y + c.getY();
            if (gameField.validateIndexes(newX, newY) && gameField.getCell(newX, newY)) {
                ++count;
            }
        }
        return count;
    }

    private void calculateImpact() {
        for (int y = 0; y < gameField.getHeight(); ++y) {
            for (int x = 0; x < gameField.getWidth() - (y % 2); ++x) {
                double impact = 0;
                impact += countFirstNeighbours(x, y) * modelParams.getFirstImpact();
                impact += countSecondNeighbours(x, y) * modelParams.getSecondImpact();
                impactField.setCell(x, y, impact);
            }
        }
    }
}
