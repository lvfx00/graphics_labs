package ru.nsu.fit.g16205.semenov.wireframe.utils;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.DoublePoint;
import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.DoubleRectangle;
import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.IntPoint;
import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.IntRectangle;

public class CoordsTransformer {

    private final DoublePoint offsetOnFunction;
    private final IntPoint offsetOnImage;
    private final double horizontalRatio;
    private final double verticalRatio;

    public CoordsTransformer(@NotNull DoubleRectangle functionArea, @NotNull IntRectangle imageArea) {
        offsetOnFunction = new DoublePoint(functionArea.getMinX(), functionArea.getMinY());
        offsetOnImage = new IntPoint(imageArea.getMinX(), imageArea.getMinY());
        horizontalRatio = functionArea.getWidth() / imageArea.getWidth();
        verticalRatio = functionArea.getHeight() / imageArea.getHeight();
    }

    public IntPoint toPixel(double x, double y) {
        return new IntPoint(
                (int) Math.floor((x - offsetOnFunction.getX()) / horizontalRatio) + offsetOnImage.getX(),
                (int) Math.floor((y - offsetOnFunction.getY()) / verticalRatio) + offsetOnImage.getY()
        );
    }

    public IntPoint toPixel(DoublePoint point) {
        return toPixel(point.getX(), point.getY());
    }

    public DoublePoint toCoords(int x, int y) {
        return new DoublePoint(
                (x - offsetOnImage.getX()) * horizontalRatio + horizontalRatio / 2 + offsetOnFunction.getX(),
                (y - offsetOnImage.getY()) * verticalRatio + verticalRatio / 2 + offsetOnFunction.getY()
        );
    }

    public DoublePoint toCoords(IntPoint point) {
        return toCoords(point.getX(), point.getY());
    }

}
