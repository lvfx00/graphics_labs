package ru.nsu.fit.semenov.isolines;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.nsu.fit.semenov.isolines.utils.Coord;

import java.awt.*;
import java.awt.image.BufferedImage;

public final class IsolinesDrawer {

    private static final NodeOffset[] NODE_OFFSETS = {
            new NodeOffset(0, 0, 1, 0),
            new NodeOffset(0, 0, 0, 1),
            new NodeOffset(1, 0, 1, 1),
            new NodeOffset(0, 1, 1, 1),
    };

    private IsolinesDrawer() {
    }

    public static BufferedImage drawMap(@NotNull BufferedImage image, BoundedFunction function, Color[] colors) {
        final double step = (function.getMaxValue() - function.getMinValue()) / colors.length;

        final int width = image.getWidth();
        final int height = image.getHeight();
        final double pixelWidthInD = (function.getMaxX() - function.getMinX()) / width;
        final double pixelHeightInD = (function.getMaxY() - function.getMinY()) / height;

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                double XcoordInD = x * pixelWidthInD + pixelHeightInD / 2;
                double YcoordInD = y * pixelHeightInD + pixelHeightInD / 2;

                double value = function.apply(XcoordInD, YcoordInD) - function.getMinValue();
                Color color = colors[(int) Math.floor(value / step)];
                image.setRGB(x, y, color.getRGB());
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

    // YjXi = f00, Yj+1Xi = f10, YjXi+1 = f01, Yj+1Xi+1 = f11
    public static BufferedImage drawIsolines(@NotNull BufferedImage image, BoundedFunction function, int k, int m, double level) {
        final int width = image.getWidth();
        final int height = image.getHeight();
        final double pixelWidthInD = (function.getMaxX() - function.getMinX()) / width;
        final double pixelHeightInD = (function.getMaxY() - function.getMinY()) / height;
        final double cellWidth = (function.getMaxX() - function.getMinX()) / (k - 1);
        final double cellHeight = (function.getMaxY() - function.getMinY()) / (m - 1);

        final Graphics2D graphics2D = image.createGraphics();
        graphics2D.setStroke(new BasicStroke(1));
        graphics2D.setColor(Color.BLACK);

        double[][] nodeValues = new double[m][k];
        Coord[][] nodeCoordsInD = new Coord[m][k];
        for (int x = 0; x < k; ++x) {
            for (int y = 0; y < m; ++y) {
                nodeCoordsInD[y][x] = new Coord(cellWidth * x, cellHeight * y);
                nodeValues[y][x] = function.apply(cellWidth * x, cellHeight * y);
            }
        }

        for (int x = 0; x < k - 1; ++x) {
            for (int y = 0; y < m - 1; ++y) {
                Coord[] intersections = new Coord[4];
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
                    case 0:
                        break;
                    case 1:
                    case 3:
                        // TODO ???
                        break;
                    case 2:
                        graphics2D.setColor(Color.RED);
                        graphics2D.setStroke(new BasicStroke(2));

                        graphics2D.drawOval(
                                (int) Math.floor(intersections[0].getX() / pixelWidthInD),
                                (int) Math.floor(intersections[0].getY() / pixelHeightInD),
                                2,
                                2
                        );
                        graphics2D.drawOval(
                                (int) Math.floor(intersections[1].getX() / pixelWidthInD),
                                (int) Math.floor(intersections[1].getY() / pixelHeightInD),
                                2,
                                2
                        );

                        graphics2D.setColor(Color.BLACK);
                        graphics2D.setStroke(new BasicStroke(1));

                        graphics2D.drawLine(
                                (int) Math.floor(intersections[0].getX() / pixelWidthInD),
                                (int) Math.floor(intersections[0].getY() / pixelHeightInD),
                                (int) Math.floor(intersections[1].getX() / pixelWidthInD),
                                (int) Math.floor(intersections[1].getY() / pixelHeightInD)
                        );
                        break;
                    case 4:
                        break;
                    default:
                        // TODO ???
                }

            }
        }

        graphics2D.dispose();
        return image;
    }

    // TODO assert f1 or f2 == c
    // asserts that coords specified in increasing order
    private static @Nullable Coord getIntersection(Coord c1, double f1, Coord c2, double f2, double level) {
        if (level < f1 && level < f2) return null;
        if (level > f1 && level > f2) return null;
        double ratio = (level - f1) / (f2 - f1);
        return new Coord(c1.getX() + (c2.getX() - c1.getX()) * ratio, c1.getY() + (c2.getY() - c1.getY()) * ratio);
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
