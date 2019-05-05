package ru.nsu.fit.g16205.semenov.wireframe.utils;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SliderAdapter extends KeyAdapter {
    private final List<JSlider> sliderList = new ArrayList<>();

    public static SliderAdapter fromIntTextFieldSliderAdapter() {
        return new SliderAdapter() {
            @Override
            protected int toInt(KeyEvent keyEvent) {
                return Integer.parseInt(((JTextField) keyEvent.getSource()).getText());
            }
        };
    }

    public void addSlider(JSlider... sliders) {
        sliderList.addAll(Arrays.asList(sliders));
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        try {
            for (JSlider slider : sliderList) {
                slider.setValue(toInt(ke));
            }
        } catch (NumberFormatException ignored) {
        }
    }

    protected abstract int toInt(KeyEvent keyEvent) throws NumberFormatException;
}
