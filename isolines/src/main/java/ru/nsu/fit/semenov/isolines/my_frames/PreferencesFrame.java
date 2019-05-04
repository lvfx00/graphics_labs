package ru.nsu.fit.semenov.isolines.my_frames;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.semenov.isolines.frame_utils.BaseFrame;
import ru.nsu.fit.semenov.isolines.frame_utils.FrameUtils;
import ru.nsu.fit.semenov.isolines.utils.MyDocumentFilter;
import ru.nsu.fit.semenov.isolines.utils.SliderAdapter;

import javax.swing.*;
import java.awt.*;

public class PreferencesFrame extends BaseFrame {

    public interface ResultListener {

        void onFinished(PreferencesValues preferencesValues);

    }

    public static class PreferencesValues {

        private final double a;
        private final double b;
        private final double c;
        private final double d;
        private final int k;
        private final int m;

        public PreferencesValues(double a, double b, double c, double d, int k, int m) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
            this.k = k;
            this.m = m;
        }

        public double getA() {
            return a;
        }

        public double getB() {
            return b;
        }

        public double getC() {
            return c;
        }

        public double getD() {
            return d;
        }

        public int getK() {
            return k;
        }

        public int getM() {
            return m;
        }

    }

    private static final int FRAME_WIDTH = 500;
    private static final int FRAME_HEIGHT = 400;

    private static final int MIN_GRID_SIZE = 2;
    private static final int MAX_GRID_SIZE = 100;

    private final JTextField aTextField = new JTextField();
    private final JTextField bTextField = new JTextField();
    private final JTextField cTextField = new JTextField();
    private final JTextField dTextField = new JTextField();
    private final JTextField kTextField = new JTextField();
    private final JTextField mTextField = new JTextField();

    private final ResultListener resultListener;

    public PreferencesFrame(
            @NotNull BaseFrame intentionFrame,
            @NotNull ResultListener resultListener,
            @NotNull PreferencesValues initialValues
    ) {
        super(FRAME_WIDTH, FRAME_HEIGHT, "Preferences", intentionFrame);
        setResizable(false);
        this.resultListener = resultListener;
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        add(initFunctionOptionsPanel(initialValues.a, initialValues.b, initialValues.c, initialValues.d));
        add(initGridOptionsPanel(initialValues.k, initialValues.m));
        add(initControlPanel());
    }

    @Override
    protected void okAction() {
        double a, b, c, d;
        int k, m;
        try {
            a = Double.parseDouble(aTextField.getText());
            b = Double.parseDouble(bTextField.getText());
            c = Double.parseDouble(cTextField.getText());
            d = Double.parseDouble(dTextField.getText());
            k = Integer.parseInt(kTextField.getText());
            m = Integer.parseInt(mTextField.getText());
        } catch (NumberFormatException err) {
            JOptionPane.showMessageDialog(null, "You have to specify all of the parameters!");
            return;
        }
        if (a >= b || c >= d) {
            JOptionPane.showMessageDialog(null, "Constraints: a < b && c < d");
            return;
        }
        resultListener.onFinished(new PreferencesValues(a, b, c, d, k, m));
        super.okAction();
    }

    private JPanel initFunctionOptionsPanel(double a, double b, double c, double d) {
        JPanel functionOptionsPanel = new JPanel();
        functionOptionsPanel.setLayout(new GridLayout(4, 2, 5, 5));
        functionOptionsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Function Region Parameters"));

        JLabel aLabel = FrameUtils.createJLabel("a:");
        FrameUtils.initJTextField(
                aTextField,
                8,
                MyDocumentFilter.getDoubleFilter(-Double.MAX_VALUE, Double.MAX_VALUE),
                String.valueOf(a),
                null,
                (JSlider[]) null
        );

        JLabel bLabel = FrameUtils.createJLabel("b:");
        FrameUtils.initJTextField(
                bTextField,
                8,
                MyDocumentFilter.getDoubleFilter(-Double.MAX_VALUE, Double.MAX_VALUE),
                String.valueOf(b),
                null,
                (JSlider[]) null
        );

        JLabel cLabel = FrameUtils.createJLabel("c:");
        FrameUtils.initJTextField(
                cTextField,
                8,
                MyDocumentFilter.getDoubleFilter(-Double.MAX_VALUE, Double.MAX_VALUE),
                String.valueOf(c),
                null,
                (JSlider[]) null
        );

        JLabel dLabel = FrameUtils.createJLabel("d:");
        FrameUtils.initJTextField(
                dTextField,
                8,
                MyDocumentFilter.getDoubleFilter(-Double.MAX_VALUE, Double.MAX_VALUE),
                String.valueOf(d),
                null,
                (JSlider[]) null
        );

        FrameUtils.addAllToPanel(
                functionOptionsPanel,
                aLabel,
                aTextField,
                bLabel,
                bTextField,
                cLabel,
                cTextField,
                dLabel,
                dTextField
        );

        return functionOptionsPanel;
    }


    private JPanel initGridOptionsPanel(int k, int m) {
        JPanel gridOptionsPanel = new JPanel();
        gridOptionsPanel.setLayout(new GridLayout(2, 3, 5, 5));
        gridOptionsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Grid Parameters")
        );

        JLabel kLabel = FrameUtils.createJLabel("k:");
        JSlider kSlider = FrameUtils.createJSlider(MIN_GRID_SIZE, k, MAX_GRID_SIZE, 20, kTextField);
        FrameUtils.initJTextField(
                kTextField,
                4,
                MyDocumentFilter.getIntFilter(MIN_GRID_SIZE, MAX_GRID_SIZE),
                String.valueOf(k),
                SliderAdapter.fromIntTextFieldSliderAdapter(),
                kSlider
        );

        JLabel mLabel = FrameUtils.createJLabel("m:");
        JSlider mSlider = FrameUtils.createJSlider(MIN_GRID_SIZE, m, MAX_GRID_SIZE, 20, mTextField);
        FrameUtils.initJTextField(
                mTextField,
                4,
                MyDocumentFilter.getIntFilter(MIN_GRID_SIZE, MAX_GRID_SIZE),
                String.valueOf(m),
                SliderAdapter.fromIntTextFieldSliderAdapter(),
                mSlider
        );

        FrameUtils.addAllToPanel(
                gridOptionsPanel,
                kLabel,
                kTextField,
                kSlider,
                mLabel,
                mTextField,
                mSlider
        );

        return gridOptionsPanel;
    }

}
