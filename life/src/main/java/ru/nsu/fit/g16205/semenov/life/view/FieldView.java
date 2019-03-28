package ru.nsu.fit.g16205.semenov.life.view;

import ru.nsu.fit.g16205.semenov.life.util.Coord;
import ru.nsu.fit.g16205.semenov.life.model.ImmutableField;

import java.awt.image.BufferedImage;

public interface FieldView {
    void updateView(ImmutableField<Boolean> field);

    void fillCellAliveByIndex(int x, int y);

    void fillCellDeadByIndex(int x, int y);

    Coord getCellIndexByCoord(int x, int y);

    void drawField();

    BufferedImage getImage();

    void showImpacts(ImmutableField<Double> impacts);

    void eraseImpacts(ImmutableField<Boolean> field);
}
