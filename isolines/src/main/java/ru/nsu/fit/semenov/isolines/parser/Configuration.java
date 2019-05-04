package ru.nsu.fit.semenov.isolines.parser;


import java.awt.*;
import java.util.List;

public class Configuration {

    private final int k;
    private final int m;
    private final List<Color> legendColors;
    private final Color isolinesColor;

    public Configuration(int k, int m, List<Color> legendColors, Color isolinesColor) {
        this.k = k;
        this.m = m;
        this.legendColors = legendColors;
        this.isolinesColor = isolinesColor;
    }

    public int getK() {
        return k;
    }

    public int getM() {
        return m;
    }

    public List<Color> getLegendColors() {
        return legendColors;
    }

    public Color getIsolinesColor() {
        return isolinesColor;
    }

}
