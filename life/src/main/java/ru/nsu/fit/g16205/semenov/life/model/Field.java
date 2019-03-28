package ru.nsu.fit.g16205.semenov.life.model;

public interface Field<T> extends ImmutableField<T> {

    void setCell(int x, int y, T value);

    void setAll(T value);

    Field<T> getResizedCopy(FieldParams fieldParams, T defaultValue);
}
