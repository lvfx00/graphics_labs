package ru.nsu.fit.g16205.semenov.life.frames;

import ru.nsu.fit.g16205.semenov.life.model.FieldParams;
import ru.nsu.fit.g16205.semenov.life.model.ModelParams;
import ru.nsu.fit.g16205.semenov.life.my_model.FieldParamsImpl;
import ru.nsu.fit.g16205.semenov.life.util.MyDocumentFilter;
import ru.nsu.fit.g16205.semenov.life.view.ViewParams;

import javax.swing.*;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class NewFileFrame extends JFrame {
    private static final int WIDTH = 300;
    private static final int HEIGHT = 150;

    private final MyFrame myFrame;

    NewFileFrame(MyFrame myFrame) {
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
        setTitle("New Document");

        JLabel widthLabel = new JLabel("Width:");
        widthLabel.setHorizontalAlignment(JLabel.RIGHT);
        JLabel heightLabel = new JLabel("Height:");
        heightLabel.setHorizontalAlignment(JLabel.RIGHT);

        JTextField widthTextField = new JTextField();
        JTextField heightTextField = new JTextField();
        PlainDocument doc = (PlainDocument) heightTextField.getDocument();
        doc.setDocumentFilter(MyDocumentFilter.getIntFilter(0, myFrame.getRestrictions().getMaxFieldHeight()));
        doc = (PlainDocument) widthTextField.getDocument();
        doc.setDocumentFilter(MyDocumentFilter.getIntFilter(0, myFrame.getRestrictions().getMaxFieldWidth()));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> backToMainFrame());

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            int width;
            int height;
            try {
                width = Integer.parseInt(widthTextField.getText());
                height = Integer.parseInt(heightTextField.getText());
            } catch (NumberFormatException err) {
                return;
            }

            FieldParams fieldParams = new FieldParamsImpl(width, height);
            ModelParams modelParams = new ModelParams.Builder().build();
            ViewParams viewParams = new ViewParams.Builder().build();
            myFrame.initGame(fieldParams, modelParams, viewParams, null);
            backToMainFrame();
        });

        GridLayout gridLayout = new GridLayout(0, 2, 10, 10);
        setLayout(gridLayout);
        add(widthLabel);
        add(widthTextField);
        add(heightLabel);
        add(heightTextField);
        add(cancelButton);
        add(okButton);
    }

    private void backToMainFrame() {
        dispose();
        myFrame.setEnabled(true);
        myFrame.setVisible(true);
    }
}
