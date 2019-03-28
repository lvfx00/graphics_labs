package ru.nsu.fit.g16205.semenov.life.model;

public interface ImmutableField<T> extends FieldParams {

    FieldParams getParams();

    T getCell(int x, int y);

}
