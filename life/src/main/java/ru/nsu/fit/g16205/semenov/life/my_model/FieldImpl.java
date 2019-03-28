package ru.nsu.fit.g16205.semenov.life.my_model;

import ru.nsu.fit.g16205.semenov.life.model.Field;
import ru.nsu.fit.g16205.semenov.life.model.FieldParams;
import ru.nsu.fit.g16205.semenov.life.util.TwoDimensionalArrayBuilder;

// TODO make field interface and immutable field interface
public class FieldImpl<T> implements Field<T> {
    private final T[][] cells;
    private final FieldParams params;
    private final TwoDimensionalArrayBuilder<? extends T> arrayBuilder;

    public FieldImpl(FieldParams params, TwoDimensionalArrayBuilder<? extends T> arrayBuilder) {
        this.params = params;
        this.arrayBuilder = arrayBuilder;
        cells = arrayBuilder.build(params.getHeight(), params.getWidth());
    }

    @Override
    public FieldParams getParams() {
        return params;
    }

    @Override
    public T getCell(int x, int y) {
        if (!validateIndexes(x, y)) {
            throw new IndexOutOfBoundsException();
        }
        return cells[y][x];
    }

    @Override
    public void setCell(int x, int y, T value) {
        if (!validateIndexes(x, y)) {
            throw new IndexOutOfBoundsException();
        }
        cells[y][x] = value;
    }

    @Override
    public void setAll(T value) {
        for (int y = 0; y < getHeight(); ++y) {
            for (int x = 0; x < getWidth() - (y % 2); ++x) {
                setCell(x, y, value);
            }
        }
    }

    @Override
    public Field<T> getResizedCopy(FieldParams fieldParams, T defaultValue) {
        Field<T> newField = new FieldImpl<>(fieldParams, arrayBuilder);
        newField.setAll(defaultValue);

        int minWidth = Math.min(fieldParams.getWidth(), getWidth());
        int minHeight = Math.min(fieldParams.getHeight(), getHeight());

        for (int y = 0; y < minHeight; ++y) {
            for (int x = 0; x < minWidth - (y % 2); ++x) {
                newField.setCell(x, y, getCell(x, y));
            }
        }

        return newField;
    }

    @Override
    public int getWidth() {
        return params.getWidth();
    }

    @Override
    public int getHeight() {
        return params.getHeight();
    }

    @Override
    public boolean validateIndexes(int x, int y) {
        return params.validateIndexes(x, y);
    }
}
