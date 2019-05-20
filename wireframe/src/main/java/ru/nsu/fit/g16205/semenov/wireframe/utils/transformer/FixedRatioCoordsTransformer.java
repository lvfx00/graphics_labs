package ru.nsu.fit.g16205.semenov.wireframe.utils.transformer;

import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.DoublePoint;
import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.IntPoint;

public class FixedRatioCoordsTransformer {

    private final DoublePoint offset;
    private final double ratio;

    public FixedRatioCoordsTransformer(double scaleRatio, DoublePoint offset) {
        this.ratio = scaleRatio;
        this.offset = offset;
    }

    public IntPoint toPixel(double x, double y) {
        return new IntPoint(
                (int) Math.floor((x - offset.getX()) / ratio),
                (int) Math.floor((y - offset.getY()) / ratio)
        );
    }

    public DoublePoint toCoords(int x, int y) {
        return new DoublePoint(
                x * ratio + ratio / 2 + offset.getX(),
                y * ratio + ratio / 2 + offset.getY()
        );
    }

}
