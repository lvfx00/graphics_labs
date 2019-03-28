package ru.nsu.fit.g16205.semenov.life.model;

public interface GameModel {

    void setCell(int x, int y, boolean value);

    boolean getCell(int x, int y);

    GameField getGameField();

    ImmutableField<Double> getImpacts();

    void iterate();

    void clear();
}
