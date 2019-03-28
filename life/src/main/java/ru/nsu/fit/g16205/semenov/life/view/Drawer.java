package ru.nsu.fit.g16205.semenov.life.view;

import ru.nsu.fit.g16205.semenov.life.util.Coord;
import ru.nsu.fit.g16205.semenov.life.model.FieldParams;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class Drawer {
    public static final double HALF_OF_ROOT_OF_3 = Math.sqrt(3.0) / 2.0;

    private final BufferedImage img;
    private final Graphics2D graphics2D;

    private final int imgMinX;
    private final int imgMaxX;
    private final int imgMinY;
    private final int imgMaxY;

    public Drawer(BufferedImage img) {
        this.img = img;

        imgMinX = img.getMinX();
        imgMaxX = imgMinX + img.getWidth();
        imgMinY = img.getMinY();
        imgMaxY = imgMinY + img.getHeight();

        graphics2D = img.createGraphics();
        graphics2D.setColor(Color.BLUE);
        graphics2D.setFont(new Font("Sans", Font.BOLD, 20));
    }

    /**
     * @param x x coordinate
     * @param y y coordinate
     * @return true if coords are valid and false otherwise
     */
    public boolean validateCoord(int x, int y) {
        return !(x < imgMinX ||
                x >= imgMaxX ||
                y < imgMinY ||
                y >= imgMaxY);
    }

    // thicknessOrientation: false - vertical, true - horizontal
    private void drawThickPoint(int x, int y, int colorRGB, int width, boolean thicknessOrientation) {
        if (width < 1) {
            throw new IllegalArgumentException("Invalid width specified");
        }

        int xToDraw;
        int yToDraw;
        for (int i = 1; i <= width; ++i) {
            int thicknessOffset = (i % 2 == 0) ? -(i / 2) : (i / 2);

            if (thicknessOrientation) {
                xToDraw = x + thicknessOffset;
                yToDraw = y;
            } else {
                xToDraw = x;
                yToDraw = y + thicknessOffset;
            }

            if (validateCoord(xToDraw, yToDraw)) {
                img.setRGB(xToDraw, yToDraw, colorRGB);
            } else {
                throw new IllegalArgumentException("Invalid point specified (" + xToDraw + ", " + yToDraw + ")");
            }
        }
    }

    private void drawLineBresenham(int x1, int y1, int x2, int y2, int colorRGB, int width) {
        CoordTransformer coordTransformer = new CoordTransformer(x1, y1, x2, y2);

        Coord normalizedPoint2 = CoordTransformer.normalize(x1, y1, x2, y2);
        int dx = normalizedPoint2.getX();
        int dy = normalizedPoint2.getY();

        int err = 0;
        int y = 0;
        for (int x = 0; x < dx; ++x) {
            Coord originCoord = coordTransformer.transformBack(x, y);
            drawThickPoint(originCoord.getX(), originCoord.getY(), colorRGB, width, coordTransformer.isSwapXandY());

            err += 2 * dy;
            if (err > dx) {
                err -= 2 * dx;
                y += 1;
            }
        }
    }

    private void drawEntireHexahedron(Hexahedron hex, int colorRGB, int width) {
        drawUpperPartOfHexahedron(hex, colorRGB, width);
        drawMediumPartOfHexahedron(hex, colorRGB, width);
        drawLowerPartOfHexahedron(hex, colorRGB, width);
    }

    private void drawUpperPartOfHexahedron(Hexahedron hex, int colorRGB, int width) {
        drawLineBresenham(hex.getAx(), hex.getAy(), hex.getCx(), hex.getCy(), colorRGB, width);
        drawLineBresenham(hex.getBx(), hex.getBy(), hex.getAx(), hex.getAy(), colorRGB, width);
    }

    private void drawMediumPartOfHexahedron(Hexahedron hex, int colorRGB, int width) {
        drawLineBresenham(hex.getCx(), hex.getCy(), hex.getEx(), hex.getEy(), colorRGB, width);
        drawLineBresenham(hex.getDx(), hex.getDy(), hex.getBx(), hex.getBy(), colorRGB, width);
    }

    private void drawLowerPartOfHexahedron(Hexahedron hex, int colorRGB, int width) {
        drawLineBresenham(hex.getEx(), hex.getEy(), hex.getFx(), hex.getFy(), colorRGB, width);
        drawLineBresenham(hex.getFx(), hex.getFy(), hex.getDx(), hex.getDy(), colorRGB, width);
    }

    public void drawField(FieldParams fieldParams, ViewParams viewParams) {
        spanFill(1, 1, viewParams.getBackgroundColor());

        int len = viewParams.getHexLength();
        int fieldHeight = fieldParams.getHeight();
        int fieldWidth = fieldParams.getWidth();

        int offsetInRow = (int) (2 * HALF_OF_ROOT_OF_3 * len);
        int offsetInColumn = 3 * len;

        int startRowX = viewParams.getFieldOffset() + (int) (HALF_OF_ROOT_OF_3 * len);
        int startRowY = viewParams.getFieldOffset();

        int startSubRowX = startRowX + (int) (HALF_OF_ROOT_OF_3 * len);
        int startSubRowY = startRowY + 3 * len / 2;

        for (int y = 0; y < fieldHeight; ++y) {
            if (y % 2 == 0) {
                for (int x = 0; x < fieldWidth; ++x) {
                    Hexahedron hex = new Hexahedron(
                            startRowX + x * offsetInRow,
                            startRowY + (y / 2) * offsetInColumn,
                            len);

                    drawEntireHexahedron(hex, viewParams.getBorderColor(), viewParams.getBorderWidth());
                }
            } else {
                for (int x = 0; x < fieldWidth - 1; ++x) {
                    Hexahedron hex = new Hexahedron(
                            startSubRowX + x * offsetInRow,
                            startSubRowY + (y / 2) * offsetInColumn,
                            len);

                    drawMediumPartOfHexahedron(hex, viewParams.getBorderColor(), viewParams.getBorderWidth());
                    if (y == fieldHeight - 1) {
                        drawLowerPartOfHexahedron(hex, viewParams.getBorderColor(), viewParams.getBorderWidth());
                    }
                }
            }
        }
    }

    public void spanFill(int x, int y, int colorRGB) {
        if (!validateCoord(x, y)) {
            throw new IllegalArgumentException("Invalid point specified (" + x + ", " + y + ")");
        }

        int oldColor = img.getRGB(x, y);
        if (oldColor == colorRGB) {
            return;
        }

        Queue<Coord> stack = new ArrayDeque<>();
        stack.add(new Coord(x, y));

        while (!stack.isEmpty()) {
            Coord c = stack.remove();
            if (img.getRGB(c.getX(), c.getY()) == oldColor) {
                for (int i = 0; validateCoord(c.getX() - i, c.getY()) &&
                        img.getRGB(c.getX() - i, c.getY()) == oldColor; ++i) {
                    img.setRGB(c.getX() - i, c.getY(), colorRGB);
                    checkVertical(c.getX() - i, c.getY(), oldColor, stack);
                }
                for (int i = 1; validateCoord(c.getX() + i, c.getY()) &&
                        img.getRGB(c.getX() + i, c.getY()) == oldColor; ++i) {
                    img.setRGB(c.getX() + i, c.getY(), colorRGB);
                    checkVertical(c.getX() + i, c.getY(), oldColor, stack);
                }
            }
        }
    }

    public void drawDigit(int x, int y, double val) {
        int valX10AsInt = (int) (val * 10);
        if (valX10AsInt > 0) {
            graphics2D.drawString("" + valX10AsInt / 10 + "." + valX10AsInt % 10, x - 13, y + 5);
        } else {
            graphics2D.drawString("" + valX10AsInt / 10, x - 5, y + 5);
        }
    }

    private void checkVertical(int x, int y, int oldColor, Queue<Coord> stack) {
        if (validateCoord(x, y + 1) && img.getRGB(x, y + 1) == oldColor) {
            stack.add(new Coord(x, y + 1));
        }
        if (validateCoord(x, y - 1) && img.getRGB(x, y - 1) == oldColor) {
            stack.add(new Coord(x, y - 1));
        }
    }

    private static class CoordTransformer {
        private final boolean swapXandY;
        private final boolean oppositeSignX;
        private final boolean oppositeSignY;
        private final Coord offset;

        public static Coord normalize(int x1, int y1, int x2, int y2) {
            int dx = x2 - x1;
            int dy = y2 - y1;

            if (dx < 0) {
                dx *= -1;
            }
            if (dy < 0) {
                dy *= -1;
            }
            if (dx < dy) {
                int temp = dy;
                dy = dx;
                dx = temp;
            }
            return new Coord(dx, dy);
        }

        public CoordTransformer(boolean swapXandY, boolean oppositeSignX, boolean oppositeSignY, Coord offset) {
            this.swapXandY = swapXandY;
            this.oppositeSignX = oppositeSignX;
            this.oppositeSignY = oppositeSignY;
            this.offset = offset;
        }

        public CoordTransformer(int x1, int y1, int x2, int y2) {
            int dx = x2 - x1;
            int dy = y2 - y1;

            offset = new Coord(x1, y1);
            oppositeSignX = dx < 0;
            oppositeSignY = dy < 0;

            swapXandY = Math.abs(dx) < Math.abs(dy);
        }

        public Coord transformBack(int x, int y) {
            int newX = swapXandY ? y : x;
            int newY = swapXandY ? x : y;

            if (oppositeSignX) {
                newX = -newX;
            }

            if (oppositeSignY) {
                newY = -newY;
            }

            newX += offset.getX();
            newY += offset.getY();

            return new Coord(newX, newY);
        }

        public boolean isSwapXandY() {
            return swapXandY;
        }

        public boolean isOppositeSignX() {
            return oppositeSignX;
        }

        public boolean isOppositeSignY() {
            return oppositeSignY;
        }

        public Coord getOffset() {
            return offset;
        }
    }

    private static class Hexahedron {

        private int Ax, Ay, Bx, By, Cx, Cy, Dx, Dy, Ex, Ey, Fx, Fy;

        public Hexahedron(int Ax, int Ay, int len) {
            this.Ax = Ax;
            this.Ay = Ay;
            Bx = (int) (Ax - HALF_OF_ROOT_OF_3 * len);
            By = Ay + len / 2;
            Cx = (int) (Ax + HALF_OF_ROOT_OF_3 * len);
            Cy = Ay + len / 2;
            Dx = (int) (Ax - HALF_OF_ROOT_OF_3 * len);
            Dy = Ay + 3 * len / 2;
            Ex = (int) (Ax + HALF_OF_ROOT_OF_3 * len);
            Ey = Ay + 3 * len / 2;
            Fx = Ax;
            Fy = Ay + 2 * len;
        }

        public int getAx() {
            return Ax;
        }

        public int getAy() {
            return Ay;
        }

        public int getBx() {
            return Bx;
        }

        public int getBy() {
            return By;
        }

        public int getCx() {
            return Cx;
        }

        public int getCy() {
            return Cy;
        }

        public int getDx() {
            return Dx;
        }

        public int getDy() {
            return Dy;
        }

        public int getEx() {
            return Ex;
        }

        public int getEy() {
            return Ey;
        }

        public int getFx() {
            return Fx;
        }

        public int getFy() {
            return Fy;
        }
    }
}
