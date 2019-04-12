package ru.nsu.fit.semenov.filter.filters;

public interface Filter {

    int[] apply(int x, int y, int width, int height, int[][][] colorComponents);

}
