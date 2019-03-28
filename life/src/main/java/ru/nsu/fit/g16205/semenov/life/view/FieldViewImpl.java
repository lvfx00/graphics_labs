package ru.nsu.fit.g16205.semenov.life.view;

import ru.nsu.fit.g16205.semenov.life.util.Coord;
import ru.nsu.fit.g16205.semenov.life.model.FieldParams;
import ru.nsu.fit.g16205.semenov.life.model.ImmutableField;

import java.awt.image.BufferedImage;
import java.util.*;

import static ru.nsu.fit.g16205.semenov.life.view.Drawer.HALF_OF_ROOT_OF_3;

public class FieldViewImpl implements FieldView, FieldParams {
    private final ViewParams viewParams;
    private final FieldParams fieldParams;

    private final Drawer drawer;

    private final BufferedImage image;

    private final Map<Coord, Coord> cellCentersToCellIndexes = new HashMap<>();
    private final Map<Coord, Coord> cellIndexesToCellCenters = new HashMap<>();

    public FieldViewImpl(ViewParams viewParams, FieldParams fieldParams) {
        this.viewParams = viewParams;
        this.fieldParams = fieldParams;

        int imageWidth = (int) (viewParams.getHexLength() * Math.sqrt(3.0) * getWidth()) +
                2 * viewParams.getFieldOffset();
        int imageHeight = viewParams.getHexLength() * 3 * getHeight() / 2 +
                2 * viewParams.getFieldOffset() + viewParams.getHexLength() / 2;

        image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        drawer = new Drawer(image);

        // init maps
        int len = viewParams.getHexLength();
        int offsetInRow = (int) (2 * HALF_OF_ROOT_OF_3 * len);
        int offsetInColumn = 3 * len;

        int startRowX = viewParams.getFieldOffset() + (int) (HALF_OF_ROOT_OF_3 * len);
        int startRowY = viewParams.getFieldOffset();

        int startSubRowX = startRowX + (int) (HALF_OF_ROOT_OF_3 * len);
        int startSubRowY = startRowY + 3 * len / 2;

        int fieldHeight = getHeight();
        int fieldWidth = getWidth();
        for (int y = 0; y < fieldHeight; ++y) {
            for (int x = 0; x < fieldWidth; ++x) {
                int centerX;
                int centerY;
                if (y % 2 == 0) {
                    centerX = startRowX + x * offsetInRow;
                    centerY = startRowY + (y / 2) * offsetInColumn + viewParams.getHexLength();
                } else {
                    if (x == fieldWidth - 1) {
                        break;
                    }
                    centerX = startSubRowX + x * offsetInRow;
                    centerY = startSubRowY + (y / 2) * offsetInColumn + viewParams.getHexLength();
                }
                Coord centerCoord = new Coord(centerX, centerY);
                Coord indexCoord = new Coord(x, y);
                cellIndexesToCellCenters.put(indexCoord, centerCoord);
                cellCentersToCellIndexes.put(centerCoord, indexCoord);
            }
        }
    }

    @Override
    public void updateView(ImmutableField<Boolean> field) {
        if (field.getWidth() != getWidth() ||
                field.getHeight() != getHeight()) {
            throw new IllegalArgumentException("Invalid field parameters");
        }

        for (int y = 0; y < field.getHeight(); ++y) {
            for (int x = 0; x < field.getWidth() - (y % 2); ++x) {
                if (field.getCell(x, y)) {
                    fillCellAliveByIndex(x, y);
                } else {
                    fillCellDeadByIndex(x, y);
                }
            }
        }
    }

    @Override
    public void fillCellAliveByIndex(int x, int y) {
        Coord cellCenter = cellIndexesToCellCenters.get(new Coord(x, y));
        if (cellCenter != null) {
            drawer.spanFill(cellCenter.getX(), cellCenter.getY(), viewParams.getAliveCellColor());
        }
    }

    @Override
    public void fillCellDeadByIndex(int x, int y) {
        Coord cellCenter = cellIndexesToCellCenters.get(new Coord(x, y));
        if (cellCenter != null) {
            drawer.spanFill(cellCenter.getX(), cellCenter.getY(), viewParams.getDeadCellColor());
        }
    }

    // TODO переделать на +1 окружающий ряд
    @Override
    public Coord getCellIndexByCoord(int x, int y) {
        // set center to cell[0][0].B
        int normX = x - viewParams.getFieldOffset();
        int normY = y - viewParams.getFieldOffset() - viewParams.getHexLength() / 2;

        if (!drawer.validateCoord(x, y) ||
                image.getRGB(x, y) == viewParams.getBorderColor() ||
                image.getRGB(x, y) == viewParams.getBackgroundColor()) {
            return null;
        }

        int rowNumber = normY / (3 * viewParams.getHexLength() / 2);
        boolean mayBeAtNextRow = normY % (3 * viewParams.getHexLength() / 2) > viewParams.getHexLength();

        int columnNumber = normX / (int) (Math.sqrt(3.0) * viewParams.getHexLength());

        if (!mayBeAtNextRow) {
            if (rowNumber % 2 == 0) {
                return new Coord(columnNumber, rowNumber);
            } else {
                // right part of hexagon in thin-row
                if (normX % (int) (Math.sqrt(3.0) * viewParams.getHexLength()) >
                        (Math.sqrt(3.0) * viewParams.getHexLength() / 2)) {
                    if (columnNumber < getWidth() - 1) {
                        return new Coord(columnNumber, rowNumber);
                    } else {
                        return null;
                    }
                    // left part of hexagon in thin-row
                } else {
                    if (columnNumber > 0) {
                        return new Coord(columnNumber - 1, rowNumber);
                    } else {
                        return null;
                    }
                }
            }
        } else {
            List<Coord> closestCoords = new ArrayList<>();
            if (validateIndexes(columnNumber, rowNumber)) {
                closestCoords.add(getCellCenter(new Coord(columnNumber, rowNumber)));
            }
            if (validateIndexes(columnNumber, rowNumber + 1)) {
                closestCoords.add(getCellCenter(new Coord(columnNumber, rowNumber + 1)));
            }
            if (rowNumber % 2 == 0) {
                if (validateIndexes(columnNumber - 1, rowNumber + 1)) {
                    closestCoords.add(getCellCenter(new Coord(columnNumber - 1, rowNumber + 1)));
                }
            } else {
                if (validateIndexes(columnNumber - 1, rowNumber)) {
                    closestCoords.add(getCellCenter(new Coord(columnNumber - 1, rowNumber)));
                }
            }
            return cellCentersToCellIndexes.get(findClosest(x, y, closestCoords));
        }
    }

    @Override
    public void drawField() {
        drawer.drawField(fieldParams, viewParams);
    }

    @Override
    public BufferedImage getImage() {
        return image;
    }

    @Override
    public void showImpacts(ImmutableField<Double> impacts) {
        if (impacts.getWidth() != getWidth() ||
                impacts.getHeight() != getHeight()) {
            throw new IllegalArgumentException("Invalid impacts field parameters");
        }

        for (int y = 0; y < getHeight(); ++y) {
            for (int x = 0; x < getWidth() - (y % 2); ++x) {
                Coord cellCenter = cellIndexesToCellCenters.get(new Coord(x, y));
                drawer.drawDigit(cellCenter.getX(),
                        cellCenter.getY(),
                        impacts.getCell(x, y));
            }
        }
    }

    @Override
    public void eraseImpacts(ImmutableField<Boolean> field) {
        for (int y = 0; y < getHeight(); ++y) {
            for (int x = 0; x < getWidth() - (y % 2); ++x) {
                eraseImpact(x, y, field.getCell(x, y));
            }
        }
    }

    private void eraseImpact(int x, int y, boolean isAlive) {
        Coord cellCenter = cellIndexesToCellCenters.get(new Coord(x, y));
        int dx = (int) (viewParams.getHexLength() * HALF_OF_ROOT_OF_3) - 5;
        for (int h = -1 * dx; h <= dx; h++) {
            drawer.spanFill(cellCenter.getX() + h,
                    cellCenter.getY() + 3,
                    isAlive ? viewParams.getAliveCellColor() : viewParams.getDeadCellColor()
            );
        }
    }

    private Coord getCellCenter(Coord c) {
        return cellIndexesToCellCenters.get(c);
    }

    @Override
    public int getWidth() {
        return fieldParams.getWidth();
    }

    @Override
    public int getHeight() {
        return fieldParams.getHeight();
    }

    @Override
    public boolean validateIndexes(int x, int y) {
        return fieldParams.validateIndexes(x, y);
    }

    private static Coord findClosest(int x, int y, List<Coord> coords) {
        Coord closest = null;
        double distance = Double.MAX_VALUE;

        for (Coord c : coords) {
            double newDist = Math.sqrt((c.getX() - x) * (c.getX() - x) + (c.getY() - y) * (c.getY() - y));
            if (newDist < distance) {
                closest = c;
                distance = newDist;
            }
        }
        return closest;
    }

}
