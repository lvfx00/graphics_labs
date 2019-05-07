package ru.nsu.fit.g16205.semenov.wireframe.utils.transformer;

import ru.nsu.fit.g16205.semenov.wireframe.model.DoublePoint;
import ru.nsu.fit.g16205.semenov.wireframe.model.IntPoint;

public class CoordsTransformerImpl implements CoordsTransformer {

    private final DoublePoint offsetInCoords;
    private final double horizontalRatio;
    private final double verticalRatio;

    public CoordsTransformerImpl(DoublePoint offsetInCoords, double horizontalRatio, double verticalRatio) {
        this.offsetInCoords = offsetInCoords;
        this.horizontalRatio = horizontalRatio;
        this.verticalRatio = verticalRatio;
    }

    @Override
    public IntPoint toPixel(double x, double y) {
        return new IntPoint(
                (int) Math.floor((x - offsetInCoords.getX()) / horizontalRatio),
                (int) Math.floor((y - offsetInCoords.getY()) / verticalRatio)
        );
    }

    @Override
    public DoublePoint toCoords(int x, int y) {
        return new DoublePoint(
                x * horizontalRatio + horizontalRatio / 2 + offsetInCoords.getX(),
                y * verticalRatio + verticalRatio / 2 + offsetInCoords.getY()
        );
    }

}
