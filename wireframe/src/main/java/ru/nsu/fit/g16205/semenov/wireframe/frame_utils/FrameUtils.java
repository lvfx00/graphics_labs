package ru.nsu.fit.g16205.semenov.wireframe.frame_utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.nsu.fit.g16205.semenov.wireframe.utils.SliderAdapter;

import javax.swing.*;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;

public class FrameUtils {

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
        JSlider slider = new JSlider(
                JSlider.HORIZONTAL,
                minValue,
                maxValue,
                initValue
        );
        slider.setMajorTickSpacing(majorTickSpacing);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        if (linkedTextField != null) {
            slider.addChangeListener(e -> linkedTextField.setText(String.valueOf(slider.getValue())));
        }
        return slider;
    }

    public static JTextField createJTextField(
            int fieldWidth,
            DocumentFilter documentFilter,
            String initialText,
            @Nullable SliderAdapter sliderAdapter,
            @Nullable JSlider... sliders
    ) {
        JTextField jTextField = new JTextField();
        return initJTextField(jTextField, fieldWidth, documentFilter, initialText, sliderAdapter, sliders);
    }

    public static JTextField initJTextField(
            JTextField textField,
            int fieldWidth,
            DocumentFilter documentFilter,
            String initialText,
            @Nullable SliderAdapter sliderAdapter,
            @Nullable JSlider... sliders
    ) {
        textField.setColumns(fieldWidth);
        PlainDocument doc = (PlainDocument) textField.getDocument();
        doc.setDocumentFilter(documentFilter);
        textField.setText(initialText);

        if (sliders != null && sliderAdapter != null) {
            sliderAdapter.addSlider(sliders);
            textField.addKeyListener(sliderAdapter);
        }
        return textField;
    }

    public static JLabel createJLabel(String text) {
        JLabel jLabel = new JLabel(text);
        jLabel.setHorizontalAlignment(JLabel.RIGHT);
        return jLabel;
    }

}
