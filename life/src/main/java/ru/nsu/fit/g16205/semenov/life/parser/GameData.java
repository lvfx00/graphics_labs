package ru.nsu.fit.g16205.semenov.life.parser;

import ru.nsu.fit.g16205.semenov.life.model.GameField;

public class GameData {
    private final GameField gameField;
    private final int cellSize;
    private final int borderWidth;

    public GameData(GameField gameField, int cellSize, int borderWidth) {
        this.gameField = gameField;
        this.cellSize = cellSize;
        this.borderWidth = borderWidth;
    }

    public GameField getGameField() {
        return gameField;
    }

    public int getCellSize() {
        return cellSize;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public void print() {
        System.out.println("Field width = " + gameField.getWidth());
        System.out.println("Field height = " + gameField.getHeight());
        System.out.println("Border width = " + borderWidth);
        System.out.println("Cell size = " + cellSize);

        for (int y = 0; y < gameField.getHeight(); ++y) {
            for (int x = 0; x < gameField.getWidth() - (y % 2); ++x) {
                if (gameField.getCell(x, y)) {
                    System.out.println("Alive on (" + x + ", " + y + ")");
                }
            }
        }
    }
}
