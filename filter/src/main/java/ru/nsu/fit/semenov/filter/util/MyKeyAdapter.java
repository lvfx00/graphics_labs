package ru.nsu.fit.semenov.filter.util;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MyKeyAdapter extends KeyAdapter {
    private final JSlider slider;
    private final JTextField textField;

    public MyKeyAdapter(JSlider slider, JTextField textField) {
        this.slider = slider;
        this.textField = textField;
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        String typed = textField.getText();
        try {
            int value = Integer.parseInt(typed);
            slider.setValue(value);
        } catch (NumberFormatException ignored) {
        }
    }
}
