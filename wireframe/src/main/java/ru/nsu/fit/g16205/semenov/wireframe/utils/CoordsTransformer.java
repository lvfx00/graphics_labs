package ru.nsu.fit.g16205.semenov.wireframe.utils;

import ru.nsu.fit.g16205.semenov.wireframe.model.DoubleCoord;
import ru.nsu.fit.g16205.semenov.wireframe.model.DoubleRectangle;
import ru.nsu.fit.g16205.semenov.wireframe.model.IntCoord;

import java.awt.*;

public class CoordsTransformer {

    private final double minX;
    private final double maxX;
    private final double minY;
    private final double maxY;
    private final double pixelWidthInD;
    private final double pixelHeightInD;

    public CoordsTransformer(Dimension imageSize, DoubleRectangle functionDomain) {
        minX = functionDomain.getX();
        maxX = functionDomain.getX() + functionDomain.getWidth();
        minY = functionDomain.getY();
        maxY = functionDomain.getY() + functionDomain.getHeight();
        pixelWidthInD = (maxX - minX) / imageSize.width;
        pixelHeightInD = (maxY - minY) / imageSize.height;
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
