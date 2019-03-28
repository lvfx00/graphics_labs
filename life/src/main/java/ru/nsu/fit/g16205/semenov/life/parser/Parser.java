package ru.nsu.fit.g16205.semenov.life.parser;

import ru.nsu.fit.g16205.semenov.life.model.GameField;
import ru.nsu.fit.g16205.semenov.life.my_model.FieldParamsImpl;
import ru.nsu.fit.g16205.semenov.life.my_model.GameFieldImpl;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Parser {
    private static final String COMMENT = "//";

    public static GameData load(File file) throws IOException, IllegalArgumentException {
        final int width, height, cellSize, borderWidth, aliveCount;
        final GameField gameField;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            Stream<String> stream = br.lines();
            List<String> pureStrings = stream.map(str -> {
                int index = str.indexOf(COMMENT);
                if (index != -1) {
                    return str.substring(0, index);
                }
                return str;
            }).collect(Collectors.toList());

            final String[] fieldSizes = pureStrings.get(0).split("\\s+");
            if (fieldSizes.length == 2) {
                width = Integer.valueOf(fieldSizes[0]);
                height = Integer.valueOf(fieldSizes[1]);
            } else {
                throw new IllegalArgumentException("Parsing failed. 1st line is invalid");
            }

            // TODO add int count check
            borderWidth = Integer.valueOf(pureStrings.get(1).split("\\s+")[0]);
            cellSize = Integer.valueOf(pureStrings.get(2).split("\\s+")[0]);
            aliveCount = Integer.valueOf(pureStrings.get(3).split("\\s+")[0]);

            FieldParamsImpl fieldParams = new FieldParamsImpl(width, height);
            gameField = new GameFieldImpl(fieldParams);

            for (int i = 4; i < 4 + aliveCount; ++i) {
                final String[] splitedCoords = pureStrings.get(i).split("\\s");
                if (splitedCoords.length == 2) {
                    final int x = Integer.valueOf(splitedCoords[0]);
                    final int y = Integer.valueOf(splitedCoords[1]);

                    if (fieldParams.validateIndexes(x, y)) {
                        gameField.setCell(x, y, true);
                    } else {
                        throw new IllegalArgumentException("Parsing failed. Invalid alive cell coords: (" +
                                x + ", " + y + ")");
                    }
                } else {
                    throw new IllegalArgumentException("Parsing failed. Line " + (i + 1) + " is invalid");
                }
            }

            return new GameData(gameField, cellSize, borderWidth);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Parsing failed. Invalid line count in file");
        }
    }

    public static void save(GameData data, File file) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(data.getGameField().getWidth() + " " + data.getGameField().getHeight());
            bw.newLine();
            bw.write(Integer.toString(data.getBorderWidth()));
            bw.newLine();
            bw.write(Integer.toString(data.getCellSize()));
            bw.newLine();
            bw.write(Integer.toString(data.getGameField().countAliveCells()));
            bw.newLine();
            for (int y = 0; y < data.getGameField().getHeight(); ++y) {
                for (int x = 0; x < data.getGameField().getWidth() - (y % 2 == 0 ? 0 : 1); ++x) {
                    if (data.getGameField().getCell(x, y)) {
                        bw.write(x + " " + y);
                        bw.newLine();
                    }
                }
            }
            bw.flush();
        }
    }
}
