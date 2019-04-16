package ru.nsu.fit.semenov.filter.util;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public abstract class MyKeyAdapter extends KeyAdapter {
    private final JSlider slider;
    private final JTextField textField;

    public static MyKeyAdapter fromIntKeyAdapter(JSlider slider, JTextField textField) {
        return new MyKeyAdapter(slider, textField) {
            @Override
            protected int toInt(String str) {
                return Integer.parseInt(str);
            }
        };
    }

    public MyKeyAdapter(JSlider slider, JTextField textField) {
        this.slider = slider;
        this.textField = textField;
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        String typed = textField.getText();
        try {
            slider.setValue(toInt(typed));
        } catch (NumberFormatException ignored) {
        }
    }

    protected abstract int toInt(String str) throws NumberFormatException;
}
