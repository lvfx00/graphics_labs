package ru.nsu.fit.g16205.semenov.life.my_model;

import ru.nsu.fit.g16205.semenov.life.model.FieldParams;

public class FieldParamsImpl implements FieldParams {
    private final int width;
    private final int height;

    public FieldParamsImpl(int width, int height) {
        if (width < 1 || height < 1) {
            throw new IllegalArgumentException("Invalid arguments specified");
        }
        this.width = width;
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public boolean validateIndexes(int x, int y) {
        int rowWidth = width - (y % 2);
        return x >= 0 &&
                x < rowWidth &&
                y >= 0 &&
                y < height;
    }
}
