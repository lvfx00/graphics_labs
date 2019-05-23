package ru.nsu.fit.g16205.semenov.wireframe.camera;

import org.ejml.simple.SimpleMatrix;
import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.wireframe.model.camera.PyramidOfView;

public class ViewPortProjector {

    private ViewPortProjector() {
    }

    public static SimpleMatrix getProjectionMatrix(@NotNull PyramidOfView pyramid) {
        final double sw = pyramid.getSw();
        final double sh = pyramid.getSh();
        final double zf = pyramid.getZf();
        final double zb = pyramid.getZb();
        return new SimpleMatrix(
                4,
                4,
                true,
                new double[]{
                        2 / sw * zb, 0, 0, 0,
                        0, 2 / sh * zb, 0, 0,
                        0, 0, zf / (zf - zb), -zf * zb / (zf - zb),
                        0, 0, 1, 0
                }
        );
    }

}
