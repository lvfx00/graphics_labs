package ru.nsu.fit.semenov.isolines.parser;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Parser {

    private Parser() {
    }

    public static Configuration parseFile(@NotNull File file) throws IOException, ParseException {
        try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(file)))) {
            int k, m, n;
            final List<Color> legendColors = new ArrayList<>();
            Color isolineColor;

            k = readNextInt(scanner);
            m = readNextInt(scanner);
            n = readNextInt(scanner);

            for (int i = 0; i < n; ++i) {
                legendColors.add(readNextColor(scanner));
            }

            isolineColor = readNextColor(scanner);

            return new Configuration(k, m, legendColors, isolineColor);
        }
    }

    private static int readNextInt(@NotNull Scanner scanner) throws ParseException {
        if (scanner.hasNextInt()) {
            return scanner.nextInt();
        } else {
            throw new ParseException("", 0);
        }
    }

    private static Color readNextColor(@NotNull Scanner scanner) throws ParseException {
        int[] colorComponents = new int[3];
        for (int j = 0; j < 3; ++j) {
            colorComponents[j] = readNextInt(scanner);
        }
        return new Color(colorComponents[0], colorComponents[1], colorComponents[2]);
    }

}
