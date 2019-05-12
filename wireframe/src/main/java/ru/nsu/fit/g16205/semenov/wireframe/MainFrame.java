package ru.nsu.fit.g16205.semenov.wireframe;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.nsu.fit.g16205.semenov.wireframe.frame_utils.BaseMainFrame;
import ru.nsu.fit.g16205.semenov.wireframe.figure.FigureData;
import ru.nsu.fit.g16205.semenov.wireframe.figure.FigureEditFrame;

import java.awt.*;
import java.awt.event.KeyEvent;

public class MainFrame extends BaseMainFrame {

    private static final Dimension INIT_FRAME_SIZE = new Dimension(800, 600);

    private @Nullable FigureData figureData = null;
    private @NotNull FigureEditFrame.ResultListener resultListener = this::addFigure;

    public MainFrame() {
        super(INIT_FRAME_SIZE.width, INIT_FRAME_SIZE.height, "Wireframe");

        initMenus();
    }

    private void initMenus() {
        initFileMenu();
        initEditMenu();
    }

    private void initFileMenu() {
        addSubMenu("File", KeyEvent.getExtendedKeyCodeForChar('f'));

        addMenuItem("File/Open",
                "Open an existing document",
                KeyEvent.getExtendedKeyCodeForChar('o'),
                "open_file.png",
                this::openFileAction);

        addMenuItem("File/Exit",
                "Quit the application; prompts to save document",
                KeyEvent.getExtendedKeyCodeForChar('e'),
                "exit.png",
                this::exitAction);

        addToolBarButton("File/Open");

        addToolBarSeparator();
    }

    private void initEditMenu() {
        String submenu = "Edit";
        addSubMenu(submenu, KeyEvent.getExtendedKeyCodeForChar('e'));

        String menuPathString = submenu + "/Add Figure";
        addMenuItem(
                menuPathString,
                "Add new figure",
                KeyEvent.getExtendedKeyCodeForChar('a'),
                "add_figure.png",
                this::addFigureAction
        );
        addToolBarButton(menuPathString);

        menuPathString = submenu + "/Edit Figure";
        addMenuItem(
                menuPathString,
                "Edit existing figure",
                KeyEvent.getExtendedKeyCodeForChar('e'),
                "edit_figure.png",
                this::editFigureAction
        );
        addToolBarButton(menuPathString);

        addToolBarSeparator();
    }

    private void openFileAction() {
    }

    private void exitAction() {
    }

    private void addFigureAction() {
        startNewFrame(new FigureEditFrame(this, null, resultListener));
    }

    private void editFigureAction() {
        startNewFrame(new FigureEditFrame(this, figureData, resultListener));
    }

    private void addFigure(@NotNull FigureData figureData) {
        this.figureData = figureData;

    }

}
