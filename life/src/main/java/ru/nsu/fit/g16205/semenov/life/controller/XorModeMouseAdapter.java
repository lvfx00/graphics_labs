package ru.nsu.fit.g16205.semenov.life.controller;

import ru.nsu.fit.g16205.semenov.life.frames.MyFrame;
import ru.nsu.fit.g16205.semenov.life.util.Coord;
import ru.nsu.fit.g16205.semenov.life.model.GameModel;
import ru.nsu.fit.g16205.semenov.life.view.FieldView;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class XorModeMouseAdapter extends MouseAdapter {
    private final FieldView fieldView;
    private final GameModel gameModel;
    private final MyFrame myFrame;
    private Coord lastCell = null;

    public XorModeMouseAdapter(FieldView fieldView, GameModel gameModel, MyFrame myFrame) {
        this.fieldView = fieldView;
        this.gameModel = gameModel;
        this.myFrame = myFrame;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        lastCell = null;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        handleMouseEvent(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        handleMouseEvent(e);
    }

    private void handleMouseEvent(MouseEvent e) {
        Coord cell = fieldView.getCellIndexByCoord(e.getX(), e.getY());
        if (cell != null) {
            if (!cell.equals(lastCell)) {
                if (myFrame.isImpactShowing()) {
                    fieldView.eraseImpacts(gameModel.getGameField());
                }

                lastCell = cell;
                boolean currentCellState = gameModel.getCell(cell.getX(), cell.getY());
                gameModel.setCell(cell.getX(), cell.getY(), !currentCellState);


                if (currentCellState) {
                    fieldView.fillCellDeadByIndex(cell.getX(), cell.getY());
                } else {
                    fieldView.fillCellAliveByIndex(cell.getX(), cell.getY());
                }

                if (myFrame.isImpactShowing()) {
                    fieldView.showImpacts(gameModel.getImpacts());
                }
                SwingUtilities.updateComponentTreeUI(myFrame);
            }
        } else {
            lastCell = null;
        }
    }
}
