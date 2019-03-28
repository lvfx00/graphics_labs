package ru.nsu.fit.g16205.semenov.life.frames;

import ru.nsu.fit.g16205.semenov.life.model.Restrictions;
import ru.nsu.fit.g16205.semenov.life.model.SelectionMode;
import ru.nsu.fit.g16205.semenov.life.controller.ReplaceModeMouseAdapter;
import ru.nsu.fit.g16205.semenov.life.controller.XorModeMouseAdapter;
import ru.nsu.fit.g16205.semenov.life.model.*;
import ru.nsu.fit.g16205.semenov.life.my_model.FieldParamsImpl;
import ru.nsu.fit.g16205.semenov.life.my_model.GameModelImpl;
import ru.nsu.fit.g16205.semenov.life.parser.GameData;
import ru.nsu.fit.g16205.semenov.life.parser.Parser;
import ru.nsu.fit.g16205.semenov.life.util.FileUtils;
import ru.nsu.fit.g16205.semenov.life.view.FieldView;
import ru.nsu.fit.g16205.semenov.life.view.FieldViewImpl;
import ru.nsu.fit.g16205.semenov.life.view.ViewParams;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import static javax.swing.JOptionPane.*;

public class MyFrame extends MainFrame {
    private FieldParams fieldParams;
    private ModelParams modelParams;
    private ViewParams viewParams;

    private GameModel gameModel;
    private FieldView fieldView;

    private final Restrictions restrictions = new Restrictions();

    private final int DELAY = 500; //milliseconds
    private final Timer timer = new Timer(DELAY, evt -> stepForwardAction());

    private final JLabel imageLabel;
    private JButton clearFieldButton;
    private JButton stepForwardButton;
    private JToggleButton replaceModeToggleButton;
    private JToggleButton xorModeToggleButton;
    private JToggleButton runButton;
    private JToggleButton impactButton;

    private File fileToSave;

    private SelectionMode currentSelectionMode = SelectionMode.REPLACE;
    private ReplaceModeMouseAdapter replaceModeMouseAdapter;
    private XorModeMouseAdapter xorModeMouseAdapter;

    private boolean impactIsShowing = false;

    public MyFrame() {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exitAction();
            }
        });

        initMenus();

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.LEFT);
        imageLabel.setVerticalAlignment(SwingConstants.TOP);

        JScrollPane scrollPane = new JScrollPane(
                imageLabel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        add(scrollPane, BorderLayout.CENTER);

        fieldParams = new FieldParamsImpl(10, 10);
        modelParams = new ModelParams.Builder().build();
        viewParams = new ViewParams.Builder().build();

        initGame(fieldParams, modelParams, viewParams, null);
    }

    private void initMenus() {
        initFileMenu();
        initModifyMenu();
        initActionMenu();
        initAboutMenu();
    }

    public void initGame(FieldParams fieldParams,
                         ModelParams modelParams,
                         ViewParams viewParams,
                         ImmutableField<Boolean> initValues) {
        initGame(fieldParams, modelParams, viewParams, initValues, SelectionMode.REPLACE);
    }

    // initValues may be null
    public void initGame(FieldParams fieldParams,
                         ModelParams modelParams,
                         ViewParams viewParams,
                         ImmutableField<Boolean> initValues,
                         SelectionMode selectionMode) {
        // clean-up old game
        if (timer.isRunning()) {
            stopTimer();
        }
        removeMouseListeners();

        this.fieldParams = fieldParams;
        this.modelParams = modelParams;
        this.viewParams = viewParams;

        gameModel = new GameModelImpl(fieldParams, modelParams);
        fieldView = new FieldViewImpl(viewParams, fieldParams);

        if (initValues != null) {
            for (int y = 0; y < initValues.getHeight(); ++y) {
                for (int x = 0; x < initValues.getWidth() - (y % 2); ++x) {
                    gameModel.setCell(x, y, initValues.getCell(x, y));
                }
            }
        }

        replaceModeMouseAdapter = new ReplaceModeMouseAdapter(fieldView, gameModel, this);
        xorModeMouseAdapter = new XorModeMouseAdapter(fieldView, gameModel, this);
        switch (selectionMode) {
            case REPLACE:
                enableReplaceModeAction();
                break;
            case XOR:
                enableXorModeAction();
                break;
            default:
                throw new AssertionError("Invalid selection mode specified");
        }

        fieldView.drawField();
        fieldView.updateView(gameModel.getGameField());
        imageLabel.setIcon(new ImageIcon(fieldView.getImage()));

        if (isImpactShowing()) {
            showImpactAction();
            impactButton.setSelected(false);
        }
        if (viewParams.getHexLength() < restrictions.getMinCellSizeToShowImpact()) {
            impactButton.setEnabled(false);
        } else {
            impactButton.setEnabled(true);
        }

        SwingUtilities.updateComponentTreeUI(this);
    }

    private void removeMouseListeners() {
        switch (currentSelectionMode) {
            case REPLACE:
                imageLabel.removeMouseListener(replaceModeMouseAdapter);
                imageLabel.removeMouseMotionListener(replaceModeMouseAdapter);
                break;
            case XOR:
                imageLabel.removeMouseListener(xorModeMouseAdapter);
                imageLabel.removeMouseMotionListener(xorModeMouseAdapter);
                break;
            default:
                throw new AssertionError("Invalid selection mode: " + currentSelectionMode.toString());
        }
    }

    private void activateSelectionMode(SelectionMode selectionMode) {
        removeMouseListeners();
        switch (selectionMode) {
            case REPLACE:
                imageLabel.addMouseMotionListener(replaceModeMouseAdapter);
                imageLabel.addMouseListener(replaceModeMouseAdapter);
                currentSelectionMode = SelectionMode.REPLACE;
                break;
            case XOR:
                imageLabel.addMouseMotionListener(xorModeMouseAdapter);
                imageLabel.addMouseListener(xorModeMouseAdapter);
                currentSelectionMode = SelectionMode.XOR;
                break;
            default:
                throw new AssertionError("Invalid selection mode: " + selectionMode.toString());
        }
    }

    private void setSelectionEnabled(boolean val) {
        if (val) {
            activateSelectionMode(currentSelectionMode);
        } else {
            removeMouseListeners();
        }
    }

    private void initFileMenu() {
        addSubMenu("File", KeyEvent.getExtendedKeyCodeForChar('f'));

        addMenuItem("File/New",
                "Create a new document",
                KeyEvent.getExtendedKeyCodeForChar('n'),
                "new_file.png",
                this::newFileAction);

        addMenuItem("File/Open",
                "Open an existing document",
                KeyEvent.getExtendedKeyCodeForChar('o'),
                "open_file.png",
                this::openFileAction);

        addMenuItem("File/Save",
                "Save an active document",
                KeyEvent.getExtendedKeyCodeForChar('s'),
                "save.png",
                this::saveFileAction);

        addMenuItem("File/Save As",
                "Save an active document with a new name",
                KeyEvent.getExtendedKeyCodeForChar('a'),
                "save_as.png",
                this::saveAsFileAction);

        addMenuItem("File/Exit",
                "Quit the application; prompts to save document",
                KeyEvent.getExtendedKeyCodeForChar('e'),
                "exit.png",
                this::exitAction);

        addToolBarButton("File/New");
        addToolBarButton("File/Open");
        addToolBarButton("File/Save");

        addToolBarSeparator();
    }

    private void initActionMenu() {
        addSubMenu("Action", KeyEvent.getExtendedKeyCodeForChar('a'));

        addMenuItem("Action/Clear",
                "Clear entire field",
                KeyEvent.getExtendedKeyCodeForChar('c'),
                "clear_field.png",
                this::clearFieldAction);

        addMenuItem("Action/Step Forward",
                "Show next state",
                KeyEvent.getExtendedKeyCodeForChar('s'),
                "next_step.png",
                this::stepForwardAction);

        addMenuItem("Action/Run",
                "Start life",
                KeyEvent.getExtendedKeyCodeForChar('r'),
                "run_life.png",
                this::runAction);

        clearFieldButton = addToolBarButton("Action/Clear");
        stepForwardButton = addToolBarButton("Action/Step Forward");
        runButton = addToolBarToggleButton("Action/Run");

        addToolBarSeparator();
    }

    private void initModifyMenu() {
        addSubMenu("Modify", KeyEvent.getExtendedKeyCodeForChar('m'));

        addMenuItem("Modify/Options",
                "Modify field size, cell size, border width",
                KeyEvent.getExtendedKeyCodeForChar('o'),
                "options.png",
                this::openOptionsAction);

        addMenuItem("Modify/Replace",
                "Change cell's state to live on click",
                KeyEvent.getExtendedKeyCodeForChar('r'),
                "replace_mode.png",
                this::enableReplaceModeAction);

        addMenuItem("Modify/XOR",
                "Change cell's state to another on click",
                KeyEvent.getExtendedKeyCodeForChar('x'),
                "xor_mode.png",
                this::enableXorModeAction);

        addMenuItem("Modify/Impact",
                "Show impact values",
                KeyEvent.getExtendedKeyCodeForChar('i'),
                "impact.png",
                this::showImpactAction);

        addToolBarButton("Modify/Options");
        replaceModeToggleButton = addToolBarToggleButton("Modify/Replace");
        xorModeToggleButton = addToolBarToggleButton("Modify/XOR");
        impactButton = addToolBarToggleButton("Modify/Impact");
        addToolBarSeparator();
    }

    private void initAboutMenu() {
        addSubMenu("Help", KeyEvent.getExtendedKeyCodeForChar('h'));

        addMenuItem("Help/About",
                "Information about author and product version",
                KeyEvent.getExtendedKeyCodeForChar('a'),
                "about.png",
                this::aboutAction);

        addToolBarButton("Help/About");
    }

    private void aboutAction() {
        AboutFrame aboutFrame = new AboutFrame(this);
        aboutFrame.setVisible(true);
        setEnabled(false);
    }

    private void newFileAction() {
        fileToSave = null;
        NewFileFrame newFileFrame = new NewFileFrame(this);
        newFileFrame.setVisible(true);
        setEnabled(false);
    }

    private void openFileAction() {
        final File file = FileUtils.getOpenFileName(this, "txt", "Text files");
        if (file == null) {
            return;
        }

        final GameData gameData;
        try {
            gameData = Parser.load(file);
        } catch (IllegalArgumentException | IOException e) {
            showMessageDialog(null, "I/O error happened :(");
            System.err.println(e.getMessage());
            return;
        }

        fileToSave = file;

        fieldParams = gameData.getGameField().getParams();
        modelParams = new ModelParams.Builder().build();
        viewParams = new ViewParams.Builder()
                .setBorderWidth(gameData.getBorderWidth())
                .setHexLength(gameData.getCellSize())
                .build();

        initGame(fieldParams, modelParams, viewParams, gameData.getGameField());
    }

    private void saveFileAction() {
        if (fileToSave == null) {
            saveAsFileAction();
        } else {
            saveToFile(fileToSave);
        }
    }

    private void saveAsFileAction() {
        final File file = FileUtils.getSaveFileName(this, "txt", "Text files");
        if (file != null) {
            fileToSave = file;
            saveToFile(file);
        }
    }

    private void saveToFile(File file) {
        GameData gameData = new GameData(gameModel.getGameField(), viewParams.getHexLength(), viewParams.getBorderWidth());
        try {
            Parser.save(gameData, file);
        } catch (IOException e) {
            // TODO show message about error
            System.err.println(e.getMessage());
        }
    }

    private void exitAction() {
        int result = JOptionPane.showConfirmDialog(null, "Do you want to save current document?");
        switch (result) {
            case CANCEL_OPTION:
                break;
            case OK_OPTION:
                saveFileAction();
                System.exit(0);
                break;
            case NO_OPTION:
                System.exit(0);
                break;
            default:
                throw new AssertionError("Invalid option specified");
        }
    }

    private void openOptionsAction() {
        OptionsFrame optionsFrame = new OptionsFrame(this);
        setEnabled(false);
        optionsFrame.setVisible(true);
    }

    private void enableReplaceModeAction() {
        switch (currentSelectionMode) {
            case REPLACE:
                break;
            case XOR:
                xorModeToggleButton.setSelected(false);
                break;
            default:
                throw new AssertionError("Invalid selection mode: " + currentSelectionMode.toString());
        }
        replaceModeToggleButton.setSelected(true);

        if (timer.isRunning()) {
            currentSelectionMode = SelectionMode.REPLACE;
        } else {
            activateSelectionMode(SelectionMode.REPLACE);
        }
    }

    private void enableXorModeAction() {
        switch (currentSelectionMode) {
            case REPLACE:
                replaceModeToggleButton.setSelected(false);
                break;
            case XOR:
                break;
            default:
                throw new AssertionError("Invalid selection mode: " + currentSelectionMode.toString());
        }
        xorModeToggleButton.setSelected(true);

        if (timer.isRunning()) {
            currentSelectionMode = SelectionMode.XOR;
        } else {
            activateSelectionMode(SelectionMode.XOR);
        }
    }

    private void showImpactAction() {
        if (impactIsShowing) {
            fieldView.eraseImpacts(gameModel.getGameField());
        } else {
            fieldView.showImpacts(gameModel.getImpacts());
        }
        impactIsShowing = !impactIsShowing;
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void clearFieldAction() {
        if (isImpactShowing()) {
            fieldView.eraseImpacts(gameModel.getGameField());
        }
        gameModel.clear();
        fieldView.updateView(gameModel.getGameField());
        SwingUtilities.updateComponentTreeUI(this);
        if (isImpactShowing()) {
            fieldView.showImpacts(gameModel.getImpacts());
        }
    }

    private void stepForwardAction() {
        if (isImpactShowing()) {
            fieldView.eraseImpacts(gameModel.getGameField());
        }
        gameModel.iterate();
        fieldView.updateView(gameModel.getGameField());
        if (isImpactShowing()) {
            fieldView.showImpacts(gameModel.getImpacts());
        }
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void runAction() {
        if (timer.isRunning()) {
            stopTimer();
        } else {
            startTimer();
        }
    }

    private void startTimer() {
        timer.start();
        clearFieldButton.setEnabled(false);
        stepForwardButton.setEnabled(false);
        setSelectionEnabled(false);
    }

    private void stopTimer() {
        timer.stop();
        clearFieldButton.setEnabled(true);
        stepForwardButton.setEnabled(true);
        setSelectionEnabled(true);
        runButton.setSelected(false);
    }

    public boolean isImpactShowing() {
        return impactIsShowing;
    }

    public FieldParams getFieldParams() {
        return fieldParams;
    }

    public ModelParams getModelParams() {
        return modelParams;
    }

    public ViewParams getViewParams() {
        return viewParams;
    }

    public SelectionMode getCurrentSelectionMode() {
        return currentSelectionMode;
    }

    public Restrictions getRestrictions() {
        return restrictions;
    }

    public GameModel getGameModel() {
        return gameModel;
    }
}
