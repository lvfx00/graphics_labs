package ru.nsu.fit.semenov.isolines.model;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.nsu.fit.semenov.isolines.utils.CoordsTransformer;
import ru.nsu.fit.semenov.isolines.utils.DoubleCoord;
import ru.nsu.fit.semenov.isolines.utils.IntCoord;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public final class IsolinesDrawer {

    @FunctionalInterface
    public interface IntersectionProcessor {

        void process(Graphics2D graphics2D, Pair<IntCoord, IntCoord> isoline);

        default void setupGraphics(Graphics2D graphics2D) {
        }

    }

    private static final double DELTA = 0.0001;
    private static final NodeOffset[] NODE_OFFSETS = {
            new NodeOffset(0, 0, 1, 0),
            new NodeOffset(0, 0, 0, 1),
            new NodeOffset(1, 0, 1, 1),
            new NodeOffset(0, 1, 1, 1),
    };

    private IsolinesDrawer() {
    }

    public static BufferedImage drawMap(@NotNull BufferedImage image, BoundedFunction function, Color[] colors) {
        double step = (function.getMaxValue() - function.getMinValue()) / colors.length;
        final CoordsTransformer transformer =
                new CoordsTransformer(image.getWidth(), image.getHeight(), function.getDomain());

        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                DoubleCoord c = transformer.getCoordsByPixel(x, y);
                double value = function.apply(c.getX(), c.getY()) - function.getMinValue();
                Color color = colors[getIndexInBounds(value, step, 0, colors.length - 1)];
                image.setRGB(x, y, color.getRGB());
            }
        }

        return image;
    }

    public static BufferedImage drawInterpolatedMap(@NotNull BufferedImage image, BoundedFunction function, Color[] colors) {
        final double step = (function.getMaxValue() - function.getMinValue()) / (colors.length - 1);
        final CoordsTransformer transformer =
                new CoordsTransformer(image.getWidth(), image.getHeight(), function.getDomain());

        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                DoubleCoord c = transformer.getCoordsByPixel(x, y);
                double value = function.apply(c.getX(), c.getY()) - function.getMinValue();

                double coef = value / step - Math.floor(value / step);
                int index = getIndexInBounds(value, step, 0, colors.length - 2);

                int red = colors[index].getRed() + (int) ((colors[index + 1].getRed() - colors[index].getRed()) * coef);
                int green = colors[index].getGreen() + (int) ((colors[index + 1].getGreen() - colors[index].getGreen()) * coef);
                int blue = colors[index].getBlue() + (int) ((colors[index + 1].getBlue() - colors[index].getBlue()) * coef);

                image.setRGB(x, y, new Color(red, green, blue).getRGB());
            }
        }

        return image;
    }

    public static BufferedImage drawGrid(@NotNull BufferedImage image, int k, int m) {
        final int width = image.getWidth();
        final int height = image.getHeight();

        final Graphics2D graphics2D = image.createGraphics();
        graphics2D.setStroke(new BasicStroke(1));
        graphics2D.setColor(Color.BLACK);

        for (int i = 1; i < k - 1; ++i) {
            int x = width * i / (k - 1);
            graphics2D.drawLine(x, 0, x, height - 1);
        }
        for (int i = 1; i < m - 1; ++i) {
            int y = height * i / (m - 1);
            graphics2D.drawLine(0, y, width - 1, y);
        }

        graphics2D.dispose();
        return image;
    }

    public static BufferedImage drawControlPoints(
            @NotNull BufferedImage image,
            @NotNull BoundedFunction function,
            int k,
            int m,
            @NotNull List<Double> levels
    ) {
        IntersectionProcessor processor = new IntersectionProcessor() {

            @Override
            public void process(Graphics2D graphics2D, Pair<IntCoord, IntCoord> isoline) {
                graphics2D.drawOval(
                        isoline.getLeft().getX(),
                        isoline.getLeft().getY(),
                        2,
                        2
                );
                graphics2D.drawOval(
                        isoline.getRight().getX(),
                        isoline.getRight().getY(),
                        2,
                        2
                );
            }

            @Override
            public void setupGraphics(Graphics2D graphics2D) {
                graphics2D.setStroke(new BasicStroke(3));
                graphics2D.setColor(Color.RED);
            }

        };

        return processIsolines(image, processor, function, k, m, levels);
    }

    public static BufferedImage drawIsolines(
            @NotNull BufferedImage image,
            @NotNull BoundedFunction function,
            int k,
            int m,
            @NotNull List<Double> levels
    ) {
        IntersectionProcessor processor = new IntersectionProcessor() {

            @Override
            public void process(Graphics2D graphics2D, Pair<IntCoord, IntCoord> isoline) {
                graphics2D.drawLine(
                        isoline.getLeft().getX(),
                        isoline.getLeft().getY(),
                        isoline.getRight().getX(),
                        isoline.getRight().getY()
                );
            }

            @Override
            public void setupGraphics(Graphics2D graphics2D) {
                graphics2D.setStroke(new BasicStroke(1));
                graphics2D.setColor(Color.BLACK);
            }

        };

        return processIsolines(image, processor, function, k, m, levels);
    }

    public static BufferedImage processIsolines(
            @NotNull BufferedImage image,
            @NotNull IntersectionProcessor processor,
            @NotNull BoundedFunction function,
            int k,
            int m,
            @NotNull List<Double> levels
    ) {
        Graphics2D graphics2D = image.createGraphics();
        processor.setupGraphics(graphics2D);
        for (double level : levels) {
            for (Pair<IntCoord, IntCoord> isoline : getIsolines(function, image.getWidth(), image.getHeight(), k, m, level)) {
                processor.process(graphics2D, isoline);
            }
        }
        graphics2D.dispose();
        return image;
    }

    private static List<Pair<IntCoord, IntCoord>> getIsolines(
            @NotNull BoundedFunction function,
            int width,
            int height,
            int k,
            int m,
            double level
    ) {
        final CoordsTransformer transformer = new CoordsTransformer(width, height, function.getDomain());
        final double cellWidthInCoords = (transformer.getMaxX() - transformer.getMinX()) / (k - 1);
        final double cellHeightInCoords = (transformer.getMaxY() - transformer.getMinY()) / (m - 1);
        final List<Pair<IntCoord, IntCoord>> isolinesList = new ArrayList<>();
        final double[][] nodeValues = new double[m][k];
        final DoubleCoord[][] nodeCoordsInD = new DoubleCoord[m][k];

        for (int x = 0; x < k; ++x) {
            for (int y = 0; y < m; ++y) {
                nodeCoordsInD[y][x] = new DoubleCoord(
                        cellWidthInCoords * x + transformer.getMinX(),
                        cellHeightInCoords * y + transformer.getMinY()
                );
                nodeValues[y][x] = function.apply(nodeCoordsInD[y][x].getX(), nodeCoordsInD[y][x].getY());
            }
        }

        for (int x = 0; x < k - 1; ++x) {
            for (int y = 0; y < m - 1; ++y) {
                boolean done = false;
                while (!done) {
                    DoubleCoord[] intersections = new DoubleCoord[4];
                    int count = 0;
                    for (NodeOffset nodeOffset : NODE_OFFSETS) {
                        intersections[count] = getIntersection(
                                nodeCoordsInD[y + nodeOffset.y1Offset][x + nodeOffset.x1Offset],
                                nodeValues[y + nodeOffset.y1Offset][x + nodeOffset.x1Offset],
                                nodeCoordsInD[y + nodeOffset.y2Offset][x + nodeOffset.x2Offset],
                                nodeValues[y + nodeOffset.y2Offset][x + nodeOffset.x2Offset],
                                level
                        );
                        if (intersections[count] != null) {
                            count++;
                        }
                    }

                    switch (count) {
                        case 1:
                            System.out.println("1");
                            done = true;
                            break;
                        case 3:
                            System.out.println("3");
                            done = true;
                            break;
                        case 2:
                            isolinesList.add(transformer.makePairOfDToP(intersections[0], intersections[1]));
                            done = true;
                            break;
                        case 4: {
                            DoubleCoord central = new DoubleCoord(
                                    nodeCoordsInD[y][x].getX() + cellWidthInCoords / 2,
                                    nodeCoordsInD[y][x].getY() + cellHeightInCoords / 2
                            );
                            double centralValue = function.apply(central.getX(), central.getY());

                            DoubleCoord intersection00 = getIntersection(
                                    central, centralValue, nodeCoordsInD[y][x], nodeValues[y][x], level);
                            DoubleCoord intersection11 = getIntersection(
                                    central, centralValue, nodeCoordsInD[y + 1][x + 1], nodeValues[y + 1][x + 1], level);

                            if (intersection00 != null && intersection11 != null) {
                                isolinesList.add(transformer.makePairOfDToP(intersections[0], intersections[1]));
                                isolinesList.add(transformer.makePairOfDToP(intersections[2], intersections[3]));
                                done = true;
                                break;
                            }

                            DoubleCoord intersection01 = getIntersection(
                                    central, centralValue, nodeCoordsInD[y][x + 1], nodeValues[y][x + 1], level);
                            DoubleCoord intersection10 = getIntersection(
                                    central, centralValue, nodeCoordsInD[y + 1][x], nodeValues[y + 1][x], level);
                            if (intersection01 != null && intersection10 != null) {
                                isolinesList.add(transformer.makePairOfDToP(intersections[1], intersections[3]));
                                isolinesList.add(transformer.makePairOfDToP(intersections[0], intersections[2]));
                            }

                            done = true;
                            break;
                        }
                        default:
                            done = true;
                    }
                }
            }
        }
        return isolinesList;
    }

    // asserts that coords specified in increasing order
    private static @Nullable DoubleCoord getIntersection(DoubleCoord c1, double f1, DoubleCoord c2, double f2, double level) {
        if (level < f1 && level < f2) return null;
        if (level > f1 && level > f2) return null;
        double ratio = (level - f1) / (f2 - f1);
        return new DoubleCoord(c1.getX() + (c2.getX() - c1.getX()) * ratio, c1.getY() + (c2.getY() - c1.getY()) * ratio);
    }

    private static int getIndexInBounds(double value, double step, int minIndex, int maxIndex) {
        int index = (int) Math.floor(value / step);
        if (index < minIndex) {
            index = minIndex;
        }
        if (index > maxIndex) {
            index = maxIndex;
        }
        return index;
    }

    private static class NodeOffset {

        private final int x1Offset;
        private final int y1Offset;
        private final int x2Offset;
        private final int y2Offset;

        public NodeOffset(int x1Offset, int y1Offset, int x2Offset, int y2Offset) {
            this.x1Offset = x1Offset;
            this.y1Offset = y1Offset;
            this.x2Offset = x2Offset;
            this.y2Offset = y2Offset;
        }

        public int getX1Offset() {
            return x1Offset;
        }

        public int getY1Offset() {
            return y1Offset;
        }

        public int getX2Offset() {
            return x2Offset;
        }

        public int getY2Offset() {
            return y2Offset;
        }

    }

}
