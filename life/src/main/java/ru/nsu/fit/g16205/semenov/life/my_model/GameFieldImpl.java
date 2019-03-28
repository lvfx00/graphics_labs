package ru.nsu.fit.g16205.semenov.life.my_model;

import ru.nsu.fit.g16205.semenov.life.model.Field;
import ru.nsu.fit.g16205.semenov.life.model.FieldParams;
import ru.nsu.fit.g16205.semenov.life.model.GameField;

public class GameFieldImpl implements GameField {
    private final Field<Boolean> field;

    public GameFieldImpl(FieldParams fieldParams) {
        field = new FieldImpl<>(
                fieldParams,
                (dimensionOne, dimensionTwo) -> new Boolean[dimensionOne][dimensionTwo]);
        clear();
    }

    public int countAliveCells() {
        int count = 0;
        for (int y = 0; y < getHeight(); ++y) {
            for (int x = 0; x < getWidth() - (y % 2 == 0 ? 0 : 1); ++x) {
                if (getCell(x, y)) {
                    count++;
                }
            }
        }
        return count;
    }

    public void clear() {
        field.setAll(false);
    }

    @Override
    public FieldParams getParams() {
        return field.getParams();
    }

    @Override
    public Boolean getCell(int x, int y) {
        return field.getCell(x, y);
    }

    @Override
    public void setCell(int x, int y, Boolean value) {
        field.setCell(x, y, value);
    }

    @Override
    public void setAll(Boolean value) {
        field.setAll(value);
    }

    @Override
    public Field<Boolean> getResizedCopy(FieldParams fieldParams, Boolean defaultValue) {
        return field.getResizedCopy(fieldParams, defaultValue);
    }

    @Override
    public int getWidth() {
        return field.getWidth();
    }

    @Override
    public int getHeight() {
        return field.getHeight();
    }

    @Override
    public boolean validateIndexes(int x, int y) {
        return field.validateIndexes(x, y);
    }
}
