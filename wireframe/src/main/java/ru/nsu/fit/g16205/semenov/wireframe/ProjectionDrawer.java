package ru.nsu.fit.g16205.semenov.wireframe;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class ProjectionDrawer {

    private ProjectionDrawer() {
    }

    public static void drawProjection(
            @NotNull BufferedImage image,
            @NotNull List<Pair<IntPoint, IntPoint>> lines,
            @NotNull Color color
    ) {
        final Graphics2D graphics2D = image.createGraphics();
        graphics2D.setColor(color);
        for (Pair<IntPoint, IntPoint> line : lines) {
            graphics2D.drawLine(
                    line.getLeft().getX(),
                    line.getLeft().getY(),
                    line.getRight().getX(),
                    line.getRight().getY()
            );
        }
        graphics2D.dispose();
    }

}
