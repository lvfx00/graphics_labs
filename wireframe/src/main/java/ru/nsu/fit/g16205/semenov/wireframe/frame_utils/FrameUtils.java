package ru.nsu.fit.g16205.semenov.wireframe.frame_utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    public static JTextField initJTextFieldWithListeners(
            @NotNull JTextField textField,
            @NotNull String initialText,
            int fieldWidth,
            @NotNull DocumentFilter documentFilter,
            @NotNull TextFieldListener... listeners
    ) {
        initJTextField(textField, initialText, fieldWidth, documentFilter);
        for (TextFieldListener listener : listeners) {
            textField.addActionListener(e -> listener.textChanged(textField.getText()));
        }
        return textField;
    }

    public static JTextField createJTextFieldWithListeners(
            @NotNull String initialText,
            int fieldWidth,
            @NotNull DocumentFilter documentFilter,
            @NotNull TextFieldListener... listeners
    ) {
        JTextField jTextField = new JTextField();
        return initJTextFieldWithListeners(jTextField, initialText, fieldWidth, documentFilter, listeners);
    }

    public static JTextField initJTextFieldForSlider(
            @NotNull JTextField textField,
            int fieldWidth,
            @NotNull DocumentFilter documentFilter,
            @NotNull String initialText,
            @NotNull SliderAdapter sliderAdapter,
            @NotNull JSlider... sliders
    ) {
        initJTextField(textField, initialText, fieldWidth, documentFilter);
        sliderAdapter.addSlider(sliders);
        textField.addKeyListener(sliderAdapter);
        return textField;
    }

    public static JTextField createJTextFieldForSlider(
            int fieldWidth,
            @NotNull DocumentFilter documentFilter,
            @NotNull String initialText,
            @NotNull SliderAdapter sliderAdapter,
            @NotNull JSlider... sliders
    ) {
        JTextField jTextField = new JTextField();
        return initJTextFieldForSlider(jTextField, fieldWidth, documentFilter, initialText, sliderAdapter, sliders);
    }

    public static JLabel createJLabel(String text) {
        JLabel jLabel = new JLabel(text);
        jLabel.setHorizontalAlignment(JLabel.RIGHT);
        return jLabel;
    }

}
