package ru.nsu.fit.g16205.semenov.raytracing.frame_utils;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import java.util.function.Predicate;

public class MyDocumentFilter extends DocumentFilter {
    private final Predicate<String> validationPredicate;

    public static MyDocumentFilter getIntFilter(int minValue, int maxValue) {
        return new MyDocumentFilter(str -> {
            if (str.equals("")) return true;
            try {
                int val = Integer.parseInt(str);
                return val >= minValue && val <= maxValue;
            } catch (NumberFormatException e) {
                return false;
            }
        });
    }

    public static MyDocumentFilter getDoubleFilter(double minValue, double maxValue) {
        return new MyDocumentFilter(str -> {
            if (str.equals("")) return true;
            try {
                double val = Double.parseDouble(str);
                return val >= minValue && val <= maxValue;
            } catch (NumberFormatException e) {
                return false;
            }
        });
    }

    public static MyDocumentFilter getFloatFilter(float minValue, float maxValue) {
        return new MyDocumentFilter(str -> {
            if (str.equals("")) return true;
            try {
                double val = Float.parseFloat(str);
                return val >= minValue && val <= maxValue;
            } catch (NumberFormatException e) {
                return false;
            }
        });
    }

    public MyDocumentFilter(Predicate<String> validationPredicate) {
        this.validationPredicate = validationPredicate;
    }

    @Override
    public void insertString(
            FilterBypass fb,
            int offset,
            String string,
            AttributeSet attr
    ) throws BadLocationException {

        Document doc = fb.getDocument();
        StringBuilder sb = new StringBuilder();
        sb.append(doc.getText(0, doc.getLength()));
        sb.insert(offset, string);

        if (validationPredicate.test(sb.toString())) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(
            FilterBypass fb,
            int offset,
            int length,
            String text,
            AttributeSet attrs
    ) throws BadLocationException {

        Document doc = fb.getDocument();
        StringBuilder sb = new StringBuilder();
        sb.append(doc.getText(0, doc.getLength()));
        sb.replace(offset, offset + length, text);

        if (validationPredicate.test(sb.toString())) {
            super.replace(fb, offset, length, text, attrs);
        }
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        Document doc = fb.getDocument();
        StringBuilder sb = new StringBuilder();
        sb.append(doc.getText(0, doc.getLength()));
        sb.delete(offset, offset + length);

        if (validationPredicate.test(sb.toString())) {
            super.remove(fb, offset, length);
        }
    }
}
