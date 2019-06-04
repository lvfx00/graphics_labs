package ru.nsu.fit.g16205.semenov.raytracing.frame_utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;

public class FrameUtils {

    @FunctionalInterface
    public interface TextFieldListener {

        void textChanged(@NotNull String text);

    }

    public static @NotNull JPanel addAllToPanel(@NotNull JPanel panel, Component... components) {
        for (Component c : components) {
            panel.add(c);
        }
        return panel;
    }

    public static @NotNull JSlider createJSlider(
            int minValue,
            int initValue,
            int maxValue,
            int majorTickSpacing,
            @Nullable JTextField linkedTextField
    ) {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, minValue, maxValue, initValue);
        slider.setMajorTickSpacing(majorTickSpacing);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        if (linkedTextField != null) {
            slider.addChangeListener(e -> linkedTextField.setText(String.valueOf(slider.getValue())));
        }
        return slider;
    }

    public static JTextField initJTextField(
            @NotNull JTextField textField,
            @NotNull String initialText,
            int fieldWidth,
            @NotNull DocumentFilter documentFilter
    ) {
        textField.setColumns(fieldWidth);
        PlainDocument doc = (PlainDocument) textField.getDocument();
        doc.setDocumentFilter(documentFilter);
        textField.setText(initialText);
        return textField;
    }

    public static JTextField createJTextField(
            @NotNull String initialText,
            int fieldWidth,
            @NotNull DocumentFilter documentFilter
    ) {
        JTextField jTextField = new JTextField();
        return initJTextField(jTextField, initialText, fieldWidth, documentFilter);
    }

    public static JTextField addListener(
            @NotNull JTextField jTextField,
            @NotNull TextFieldListener listener
    ) {
        jTextField.addActionListener(e -> listener.textChanged(jTextField.getText()));
        return jTextField;
    }

    public static JTextField linkSlider(
            @NotNull JTextField textField,
            @NotNull SliderAdapter sliderAdapter,
            @NotNull JSlider slider
    ) {
        sliderAdapter.addSlider(slider);
        textField.addKeyListener(sliderAdapter);
        return textField;
    }

    public static JLabel createJLabel(String text) {
        JLabel jLabel = new JLabel(text);
        jLabel.setHorizontalAlignment(JLabel.RIGHT);
        return jLabel;
    }

    public static @NotNull JSpinner createSpinner(@NotNull AbstractSpinnerModel spinnerModel, int width) {
        if (width < 1) {
            throw new IllegalArgumentException("Invalid width specified");
        }
        JSpinner spinner = new JSpinner(spinnerModel);
        Component mySpinnerEditor = spinner.getEditor();
        JFormattedTextField jftf = ((JSpinner.DefaultEditor) mySpinnerEditor).getTextField();
        jftf.setColumns(width);
        return spinner;
    }

}
