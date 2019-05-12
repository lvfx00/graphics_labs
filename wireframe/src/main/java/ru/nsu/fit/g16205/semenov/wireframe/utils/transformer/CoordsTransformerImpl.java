package ru.nsu.fit.g16205.semenov.wireframe.utils.transformer;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.wireframe.model.geometric.DoublePoint;
import ru.nsu.fit.g16205.semenov.wireframe.model.geometric.DoubleRectangle;
import ru.nsu.fit.g16205.semenov.wireframe.model.geometric.IntPoint;
import ru.nsu.fit.g16205.semenov.wireframe.model.geometric.IntRectangle;

public class CoordsTransformerImpl implements CoordsTransformer {

    private final DoublePoint offsetOnFunction;
    private final IntPoint offsetOnImage;
    private final double horizontalRatio;
    private final double verticalRatio;

    public CoordsTransformerImpl(@NotNull DoubleRectangle functionArea, @NotNull IntRectangle imageArea) {
        offsetOnFunction = new DoublePoint(functionArea.getMinX(), functionArea.getMinY());
        offsetOnImage = new IntPoint(imageArea.getMinX(), imageArea.getMinY());
        horizontalRatio = functionArea.getWidth() / imageArea.getWidth();
        verticalRatio = functionArea.getHeight() / imageArea.getHeight();
    }

    @Override
    public IntPoint toPixel(double x, double y) {
        return new IntPoint(
                (int) Math.floor((x - offsetOnFunction.getX()) / horizontalRatio) + offsetOnImage.getX(),
                (int) Math.floor((y - offsetOnFunction.getY()) / verticalRatio) + offsetOnImage.getY()
        );
    }

    @Override
    public IntPoint toPixel(DoublePoint point) {
        return toPixel(point.getX(), point.getY());
    }

    @Override
    public DoublePoint toCoords(int x, int y) {
        return new DoublePoint(
                (x - offsetOnImage.getX()) * horizontalRatio + horizontalRatio / 2 + offsetOnFunction.getX(),
                (y - offsetOnImage.getY()) * verticalRatio + verticalRatio / 2 + offsetOnFunction.getY()
        );
    }

    @Override
    public DoublePoint toCoords(IntPoint point) {
        return toCoords(point.getX(), point.getY());
    }

}
