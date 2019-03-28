package ru.nsu.fit.g16205.semenov.life.frames;

import ru.nsu.fit.g16205.semenov.life.model.SelectionMode;
import ru.nsu.fit.g16205.semenov.life.model.FieldParams;
import ru.nsu.fit.g16205.semenov.life.model.ImmutableField;
import ru.nsu.fit.g16205.semenov.life.model.ModelParams;
import ru.nsu.fit.g16205.semenov.life.my_model.FieldParamsImpl;
import ru.nsu.fit.g16205.semenov.life.util.MyDocumentFilter;
import ru.nsu.fit.g16205.semenov.life.util.MyKeyAdapter;
import ru.nsu.fit.g16205.semenov.life.view.ViewParams;

import javax.swing.*;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class OptionsFrame extends JFrame {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;

    private final MyFrame myFrame;

    private final JTextField birthBeginTextField = new JTextField();
    private final JTextField birthEndTextField = new JTextField();
    private final JTextField lifeBeginTextField = new JTextField();
    private final JTextField lifeEndTextField = new JTextField();
    private final JTextField firstImpactTextField = new JTextField();
    private final JTextField secondImpactTextField = new JTextField();

    private JRadioButton replaceModeButton;
    private JRadioButton xorModeButton;

    private final JTextField fieldWidthTextField = new JTextField();
    private final JTextField fieldHeightTextField = new JTextField();

    private final JTextField borderSizeTextField = new JTextField();
    private final JTextField cellSizeTextField = new JTextField();

    OptionsFrame(MyFrame myFrame) {
        this.myFrame = myFrame;

        setSize(WIDTH, HEIGHT);
        setResizable(false);

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                backToMainFrame();
            }
        });

        setLocationByPlatform(true);
        setTitle("Options");

        GridLayout gridLayout = new GridLayout(0, 1, 10, 10);
        setLayout(gridLayout);

        add(joinHorizontally(initConstantsPanel(), initSelectionModePanel()));
        add(initViewParametersPanel());
        add(joinHorizontally(initFieldSizePanel(), initControlPanel()));
    }

    private JPanel initConstantsPanel() {
        JLabel birthBeginLabel = new JLabel("BIRTH_BEGIN:");
        birthBeginLabel.setHorizontalAlignment(JLabel.RIGHT);
        JLabel birthEndLabel = new JLabel("BIRTH_END:");
        birthEndLabel.setHorizontalAlignment(JLabel.RIGHT);
        JLabel lifeBeginLabel = new JLabel("LIFE_BEGIN:");
        lifeBeginLabel.setHorizontalAlignment(JLabel.RIGHT);
        JLabel lifeEndLabel = new JLabel("LIFE_END:");
        lifeEndLabel.setHorizontalAlignment(JLabel.RIGHT);
        JLabel firstImpactLabel = new JLabel("FIRST_IMPACT:");
        firstImpactLabel.setHorizontalAlignment(JLabel.RIGHT);
        JLabel secondImpactLabel = new JLabel("SECOND_IMPACT:");
        secondImpactLabel.setHorizontalAlignment(JLabel.RIGHT);

        PlainDocument doc = (PlainDocument) birthBeginTextField.getDocument();
        doc.setDocumentFilter(MyDocumentFilter.getDoubleFilter(0, myFrame.getRestrictions().getMaxBirthBeginValue()));
        doc = (PlainDocument) birthEndTextField.getDocument();
        doc.setDocumentFilter(MyDocumentFilter.getDoubleFilter(0, myFrame.getRestrictions().getMaxBirthEndValue()));
        doc = (PlainDocument) lifeBeginTextField.getDocument();
        doc.setDocumentFilter(MyDocumentFilter.getDoubleFilter(0, myFrame.getRestrictions().getMaxLifeBeginValue()));
        doc = (PlainDocument) lifeEndTextField.getDocument();
        doc.setDocumentFilter(MyDocumentFilter.getDoubleFilter(0, myFrame.getRestrictions().getMaxLifeEndValue()));
        doc = (PlainDocument) firstImpactTextField.getDocument();
        doc.setDocumentFilter(MyDocumentFilter.getDoubleFilter(0, myFrame.getRestrictions().getMaxFirstImpactValue()));
        doc = (PlainDocument) secondImpactTextField.getDocument();
        doc.setDocumentFilter(MyDocumentFilter.getDoubleFilter(0, myFrame.getRestrictions().getMaxSecondImpactValue()));

        JPanel constantsPanel = new JPanel();
        constantsPanel.setLayout(new GridLayout(6, 2, 10, 8));
        constantsPanel.add(birthBeginLabel);
        constantsPanel.add(birthBeginTextField);
        constantsPanel.add(birthEndLabel);
        constantsPanel.add(birthEndTextField);
        constantsPanel.add(lifeBeginLabel);
        constantsPanel.add(lifeBeginTextField);
        constantsPanel.add(lifeEndLabel);
        constantsPanel.add(lifeEndTextField);
        constantsPanel.add(firstImpactLabel);
        constantsPanel.add(firstImpactTextField);
        constantsPanel.add(secondImpactLabel);
        constantsPanel.add(secondImpactTextField);

        constantsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Constants"));

        birthBeginTextField.setText(String.valueOf(myFrame.getModelParams().getBirthBegin()));
        birthEndTextField.setText(String.valueOf(myFrame.getModelParams().getBirthEnd()));
        lifeBeginTextField.setText(String.valueOf(myFrame.getModelParams().getLifeBegin()));
        lifeEndTextField.setText(String.valueOf(myFrame.getModelParams().getLifeEnd()));
        firstImpactTextField.setText(String.valueOf(myFrame.getModelParams().getFirstImpact()));
        secondImpactTextField.setText(String.valueOf(myFrame.getModelParams().getSecondImpact()));

        return constantsPanel;
    }

    private JPanel initSelectionModePanel() {
        replaceModeButton = new JRadioButton("Replace mode",
                myFrame.getCurrentSelectionMode().equals(SelectionMode.REPLACE));
        xorModeButton = new JRadioButton("Xor mode",
                myFrame.getCurrentSelectionMode().equals(SelectionMode.XOR));

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(replaceModeButton);
        buttonGroup.add(xorModeButton);

        JPanel selectionModePanel = new JPanel();
        selectionModePanel.setLayout(new GridLayout(2, 1));
        selectionModePanel.add(replaceModeButton);
        selectionModePanel.add(xorModeButton);

        selectionModePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Mode"));

        return selectionModePanel;
    }

    private JPanel initFieldSizePanel() {
        JLabel fieldWidthLabel = new JLabel("Width:");
        fieldWidthLabel.setHorizontalAlignment(JLabel.RIGHT);
        JLabel fieldHeightLabel = new JLabel("Height:");
        fieldHeightLabel.setHorizontalAlignment(JLabel.RIGHT);

        PlainDocument doc = (PlainDocument) fieldWidthTextField.getDocument();
        doc.setDocumentFilter(MyDocumentFilter.getIntFilter(1, myFrame.getRestrictions().getMaxFieldWidth()));
        doc = (PlainDocument) fieldHeightTextField.getDocument();
        doc.setDocumentFilter(MyDocumentFilter.getIntFilter(1, myFrame.getRestrictions().getMaxFieldHeight()));

        JPanel fieldSizePanel = new JPanel();
        fieldSizePanel.setLayout(new GridLayout(2, 2, 10, 8));
        fieldSizePanel.add(fieldWidthLabel);
        fieldSizePanel.add(fieldWidthTextField);
        fieldSizePanel.add(fieldHeightLabel);
        fieldSizePanel.add(fieldHeightTextField);

        fieldSizePanel.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createEtchedBorder(),
                        "Field size"
                )
        );

        fieldWidthTextField.setText(String.valueOf(myFrame.getFieldParams().getWidth()));
        fieldHeightTextField.setText(String.valueOf(myFrame.getFieldParams().getHeight()));

        return fieldSizePanel;
    }

    private JPanel initViewParametersPanel() {
        JSlider borderSizeSlider = new JSlider(
                JSlider.HORIZONTAL,
                myFrame.getRestrictions().getMinBorderSize(),
                myFrame.getRestrictions().getMaxBorderSize(),
                myFrame.getViewParams().getBorderWidth()
        );
        borderSizeSlider.setMajorTickSpacing(1);
        borderSizeSlider.setPaintTicks(true);
        borderSizeSlider.setPaintLabels(true);
        borderSizeSlider.addChangeListener(e -> borderSizeTextField.setText(String.valueOf(borderSizeSlider.getValue())));

        JSlider cellSizeSlider = new JSlider(
                JSlider.HORIZONTAL,
                myFrame.getRestrictions().getMinCellSize(),
                myFrame.getRestrictions().getMaxCellSize(),
                myFrame.getViewParams().getHexLength()
        );
        cellSizeSlider.setMajorTickSpacing(15);
        cellSizeSlider.setMinorTickSpacing(5);
        cellSizeSlider.setPaintTicks(true);
        cellSizeSlider.setPaintLabels(true);
        cellSizeSlider.addChangeListener(e -> cellSizeTextField.setText(String.valueOf(cellSizeSlider.getValue())));

        JLabel borderSizeLabel = new JLabel("Border size:");
        borderSizeLabel.setHorizontalAlignment(JLabel.RIGHT);
        JLabel cellSizeLabel = new JLabel("Cell size:");
        cellSizeLabel.setHorizontalAlignment(JLabel.RIGHT);

        PlainDocument doc = (PlainDocument) borderSizeTextField.getDocument();
        doc.setDocumentFilter(
                MyDocumentFilter.getIntFilter(
                        myFrame.getRestrictions().getMinBorderSize(),
                        myFrame.getRestrictions().getMaxBorderSize()
                )
        );
        borderSizeTextField.addKeyListener(new MyKeyAdapter(borderSizeSlider, borderSizeTextField));

        doc = (PlainDocument) cellSizeTextField.getDocument();
        doc.setDocumentFilter(
                MyDocumentFilter.getIntFilter(
                        myFrame.getRestrictions().getMinCellSize(),
                        myFrame.getRestrictions().getMaxCellSize()
                )
        );
        cellSizeTextField.addKeyListener(new MyKeyAdapter(cellSizeSlider, cellSizeTextField));

        JPanel viewParametersPanel = new JPanel();
        viewParametersPanel.setLayout(new GridLayout(2, 3, 10, 8));
        viewParametersPanel.add(borderSizeLabel);
        viewParametersPanel.add(borderSizeTextField);
        viewParametersPanel.add(borderSizeSlider);
        viewParametersPanel.add(cellSizeLabel);
        viewParametersPanel.add(cellSizeTextField);
        viewParametersPanel.add(cellSizeSlider);

        viewParametersPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "View parameters"));

        borderSizeTextField.setText(String.valueOf(myFrame.getViewParams().getBorderWidth()));
        cellSizeTextField.setText(String.valueOf(myFrame.getViewParams().getHexLength()));

        return viewParametersPanel;
    }

    private JPanel initControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(2, 1, 10, 10));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> backToMainFrame());

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> okAction());

        controlPanel.add(cancelButton);
        controlPanel.add(okButton);

        return controlPanel;
    }

    private boolean isConstantsValid(
            double birthBeginValue,
            double birthEndValue,
            double lifeBeginValue,
            double lifeEndValue
    ) {
        return lifeBeginValue <= birthBeginValue &&
                birthBeginValue <= birthEndValue &&
                birthEndValue <= lifeEndValue;
    }

    private void okAction() {
        double birthBeginValue;
        double birthEndValue;
        double lifeBeginValue;
        double lifeEndValue;
        double firstImpactValue;
        double secondIpactValue;
        try {
            birthBeginValue = Double.parseDouble(birthBeginTextField.getText());
            birthEndValue = Double.parseDouble(birthEndTextField.getText());
            lifeBeginValue = Double.parseDouble(lifeBeginTextField.getText());
            lifeEndValue = Double.parseDouble(lifeEndTextField.getText());
            firstImpactValue = Double.parseDouble(firstImpactTextField.getText());
            secondIpactValue = Double.parseDouble(secondImpactTextField.getText());
        } catch (NumberFormatException err) {
            JOptionPane.showMessageDialog(null, "You have to specify all parameters!");
            return;
        }

        if (!isConstantsValid(birthBeginValue, birthEndValue, lifeBeginValue, lifeEndValue)) {
            JOptionPane.showMessageDialog(null, "Invalid constant values specified.\n" +
                    "Make sure that LIVE_BEGIN ≤ BIRTH_BEGIN ≤ BIRTH_END ≤ LIVE_END");
            return;
        }

        ModelParams newModelParams = new ModelParams.Builder()
                .setBirthBegin(birthBeginValue)
                .setBirthEnd(birthEndValue)
                .setLifeBegin(lifeBeginValue)
                .setLifeEnd(lifeEndValue)
                .setFirstImpact(firstImpactValue)
                .setSecondImpact(secondIpactValue)
                .build();

        SelectionMode newSelectionMode;
        if (replaceModeButton.isSelected()) {
            newSelectionMode = SelectionMode.REPLACE;
        } else if (xorModeButton.isSelected()) {
            newSelectionMode = SelectionMode.XOR;
        } else {
            throw new AssertionError("Unable to determine selection mode");
        }

        int fieldWidth;
        int fieldHeight;
        try {
            fieldWidth = Integer.parseInt(fieldWidthTextField.getText());
            fieldHeight = Integer.parseInt(fieldHeightTextField.getText());
        } catch (NumberFormatException err) {
            JOptionPane.showMessageDialog(null, "You have to specify all parameters!");
            return;
        }

        FieldParams newFieldParams = new FieldParamsImpl(fieldWidth, fieldHeight);
        ImmutableField<Boolean> newInitValues =
                myFrame.getGameModel().getGameField().getResizedCopy(newFieldParams, false);

        int borderSize;
        int cellSize;
        try {
            borderSize = Integer.parseInt(borderSizeTextField.getText());
            cellSize = Integer.parseInt(cellSizeTextField.getText());
        } catch (NumberFormatException err) {
            JOptionPane.showMessageDialog(null, "You have to specify all parameters!");
            return;
        }

        ViewParams newViewParams = new ViewParams(
                cellSize,
                borderSize,
                myFrame.getViewParams().getFieldOffset(),
                myFrame.getViewParams().getAliveCellColor(),
                myFrame.getViewParams().getDeadCellColor(),
                myFrame.getViewParams().getBackgroundColor(),
                myFrame.getViewParams().getBorderColor()
        );

        myFrame.initGame(
                newFieldParams,
                newModelParams,
                newViewParams,
                newInitValues,
                newSelectionMode
        );
        backToMainFrame();
    }

    private JPanel joinHorizontally(JPanel one, JPanel two) {
        GridLayout gridLayout = new GridLayout(1, 2, 10, 10);
        JPanel jPanel = new JPanel();
        jPanel.setLayout(gridLayout);
        jPanel.add(one);
        jPanel.add(two);
        return jPanel;
    }

    private void backToMainFrame() {
        dispose();
        myFrame.setEnabled(true);
        myFrame.setVisible(true);
    }
}
