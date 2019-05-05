package ru.nsu.fit.g16205.semenov.wireframe.generatrix;

import org.ejml.simple.SimpleMatrix;
import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.wireframe.model.DoubleCoord;
import ru.nsu.fit.g16205.semenov.wireframe.model.DoubleRectangle;
import ru.nsu.fit.g16205.semenov.wireframe.model.IntCoord;
import ru.nsu.fit.g16205.semenov.wireframe.utils.CoordsTransformer;
import ru.nsu.fit.g16205.semenov.wireframe.utils.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.Stream;

public class PreviewCreator {

    private static final int N = 100;

    private static final Color CURVE_COLOR = Color.CYAN;

    private static final SimpleMatrix Ms = new SimpleMatrix(
            new double[][]{
                    {-1, 3, -3, 1},
                    {3, -6, 3, 0},
                    {-3, 0, 3, 0},
                    {1, 4, 1, 0}
            }
    ).scale(1. / 6);

    private static SimpleMatrix getTMatrix(double t) {
        return new SimpleMatrix(1, 4, true, new double[]{t * t * t, t * t, t, 1});
    }

    private PreviewCreator() {
    }

    public static Stream<SimpleMatrix> getCurvePoints(@NotNull double[][] anchorPoints) {
        SimpleMatrix Gs = new SimpleMatrix(anchorPoints);
        Stream.Builder<SimpleMatrix> streamBuilder = Stream.builder();

        for (int i = 1; i < anchorPoints.length - 2; ++i) {
            SimpleMatrix Gsi = Gs.rows(i - 1, i + 2);
            for (double t = 0.; t < 1.; t += 1. / N) {
                SimpleMatrix T = getTMatrix(t);
                SimpleMatrix point = T.mult(Ms).mult(Gsi);
                streamBuilder.add(point);
            }
        }

        return streamBuilder.build();
    }

    public static BufferedImage drawCurveByPoints(
            @NotNull double[][] anchorPoints,
            @NotNull BufferedImage image,
            @NotNull DoubleRectangle definitionArea
    ) {
        CoordsTransformer transformer = new CoordsTransformer(
                new Dimension(image.getWidth(), image.getHeight()),
                definitionArea
        );
        Stream<SimpleMatrix> points = getCurvePoints(anchorPoints);
        points.forEach(coords -> {
            IntCoord pixel = transformer.getPixelByCoords(coords.get(0, 0), coords.get(0, 1));
            image.setRGB(pixel.getX(), pixel.getY(), CURVE_COLOR.getRGB());
        });
        return image;
    }

    public static BufferedImage createAnchorPointsPreview(
            @NotNull Dimension imageSize,
            @NotNull DoubleRectangle definitionArea,
            @NotNull List<IntCoord> anchorPoints
    ) {
        // TODO
        BufferedImage image = ImageUtils.createOpaqueImage(imageSize.width, imageSize.height);
        return image;
    }

}
