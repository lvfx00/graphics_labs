package ru.nsu.fit.g16205.semenov.life.model;

public interface FieldParams {

    int getWidth();

    int getHeight();

    boolean validateIndexes(int x, int y);

}
