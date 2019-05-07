package ru.nsu.fit.g16205.semenov.wireframe.utils.transformer;

import ru.nsu.fit.g16205.semenov.wireframe.model.DoublePoint;
import ru.nsu.fit.g16205.semenov.wireframe.model.IntPoint;

public interface CoordsTransformer {

    IntPoint toPixel(double x, double y);

    DoublePoint toCoords(int x, int y);
}
