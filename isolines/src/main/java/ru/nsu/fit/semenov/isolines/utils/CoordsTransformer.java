package ru.nsu.fit.semenov.isolines.utils;

import org.apache.commons.lang3.tuple.Pair;

public class CoordsTransformer {

    private final double minX;
    private final double maxX;
    private final double minY;
    private final double maxY;
    private final int imageWidth;
    private final int imageHeight;
    private final double pixelWidthInD;
    private final double pixelHeightInD;

    public CoordsTransformer(int imageWidth, int imageHeight, Rectangle functionDomain) {
        minX = functionDomain.getX();
        maxX = functionDomain.getX() + functionDomain.getWidth();
        minY = functionDomain.getY();
        maxY = functionDomain.getY() + functionDomain.getHeight();
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        pixelWidthInD = (maxX - minX) / this.imageWidth;
        pixelHeightInD = (maxY - minY) / this.imageHeight;
    }

    // TODO убрать или рефакторить
    public Pair<IntCoord, IntCoord> makePairOfDToP(DoubleCoord c1, DoubleCoord c2) {
        return Pair.of(
                new IntCoord(
                        (int) Math.floor((c1.getX() - minX) / pixelWidthInD),
                        (int) Math.floor((c1.getY() - minY) / pixelHeightInD)
                ),
                new IntCoord(
                        (int) Math.floor((c2.getX() - minX) / pixelWidthInD),
                        (int) Math.floor((c2.getY() - minY) / pixelHeightInD)
                )
        );
    }

    public IntCoord getPixelByCoords(double x, double y) {
        return new IntCoord(
                (int) Math.floor((x - minX) / pixelWidthInD),
                (int) Math.floor((y - minY) / pixelHeightInD)
        );
    }

    public DoubleCoord getCoordsByPixel(int x, int y) {
        return new DoubleCoord(
                x * pixelWidthInD + pixelWidthInD / 2 + minX,
                y * pixelHeightInD + pixelHeightInD / 2 + minY
        );
    }

    public double getMinX() {
        return minX;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxY() {
        return maxY;
    }

}
