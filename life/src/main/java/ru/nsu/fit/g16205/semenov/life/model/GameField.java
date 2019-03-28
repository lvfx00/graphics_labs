package ru.nsu.fit.g16205.semenov.life.model;

public interface GameField extends Field<Boolean> {

    int countAliveCells();

    void clear();
}
