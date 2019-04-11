package ru.nsu.fit.semenov.filter.my_frames;

import ru.nsu.fit.semenov.filter.frame.BaseFrame;
import ru.nsu.fit.semenov.filter.util.FrameUtils;
import ru.nsu.fit.semenov.filter.util.MyDocumentFilter;
import ru.nsu.fit.semenov.filter.util.MyKeyAdapter;

import javax.swing.*;
import javax.swing.text.PlainDocument;
import java.awt.*;

import static ru.nsu.fit.semenov.filter.util.ImageUtils.BITS_PER_COLOR_COMPONENT;

public final class ErrorDiffusionSettingsFrame extends BaseFrame {

    public interface ResultListener {

        void onFinished(Result result);

    }

    public static class Result {

        private final int redPaletteSize;
        private final int greenPaletteSize;
        private final int bluePaletteSize;

        public Result(int redPaletteSize, int greenPaletteSize, int bluePaletteSize) {
            this.redPaletteSize = redPaletteSize;
            this.greenPaletteSize = greenPaletteSize;
            this.bluePaletteSize = bluePaletteSize;
        }

        public int getRedPaletteSize() {
            return redPaletteSize;
        }

        public int getGreenPaletteSize() {
            return greenPaletteSize;
        }

        public int getBluePaletteSize() {
            return bluePaletteSize;
        }

    }

    private static final int MIN_PALETTE_SIZE = 2;
    private static final int MAX_PALETTE_SIZE = 1 << BITS_PER_COLOR_COMPONENT;
    private static final int INITIAL_PALETTE_SIZE = 16;
    private static final int FRAME_WIDTH = 400;
    private static final int FRAME_HEIGHT = 220;

    private final ResultListener resultListener;
    private final JTextField redPaletteTextField = new JTextField();
    private final JTextField greenPaletteTextField = new JTextField();
    private final JTextField bluePaletteTextField = new JTextField();

    public ErrorDiffusionSettingsFrame(BaseFrame intentionFrame, ResultListener resultListener) {
        super(FRAME_WIDTH, FRAME_HEIGHT, "Error Diffusion Parameters", intentionFrame);
        this.resultListener = resultListener;

        GridLayout gridLayout = new GridLayout(0, 1, 10, 10);
        setLayout(gridLayout);
        add(initPaletteParametersPanel());
        add(initControlPanel());
    }

    @Override
    protected void okAction() {
        int redPaletteSize;
        int greenPaletteSize;
        int bluePaletteSize;
        try {
            redPaletteSize = Integer.parseInt(redPaletteTextField.getText());
            greenPaletteSize = Integer.parseInt(greenPaletteTextField.getText());
            bluePaletteSize = Integer.parseInt(bluePaletteTextField.getText());
        } catch (NumberFormatException err) {
            JOptionPane.showMessageDialog(null, "You have to specify all of the parameters!");
            return;
        }
        resultListener.onFinished(new Result(redPaletteSize, greenPaletteSize, bluePaletteSize));
        super.okAction();
    }

    private JPanel initPaletteParametersPanel() {
        JSlider redPaletteSizeSlider = initJSlider(redPaletteTextField);
        JSlider greenPaletteSizeSlider = initJSlider(greenPaletteTextField);
        JSlider bluePaletteSizeSlider = initJSlider(bluePaletteTextField);

        initJTextField(redPaletteTextField, String.valueOf(INITIAL_PALETTE_SIZE), redPaletteSizeSlider);
        initJTextField(greenPaletteTextField, String.valueOf(INITIAL_PALETTE_SIZE), greenPaletteSizeSlider);
        initJTextField(bluePaletteTextField, String.valueOf(INITIAL_PALETTE_SIZE), bluePaletteSizeSlider);

        JLabel redPaletteSizeLabel = initJLabel("Red palette size");
        JLabel greenPaletteSizeLabel = initJLabel("Green palette size");
        JLabel bluePaletteSizeLabel = initJLabel("Blue palette size");

        JPanel paletteParametersPanel = new JPanel();
        paletteParametersPanel.setLayout(new GridLayout(3, 3, 10, 8));
        paletteParametersPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Palette size parameters"));
        FrameUtils.addAllToPanel(
                paletteParametersPanel,
                redPaletteSizeLabel,
                redPaletteTextField,
                redPaletteSizeSlider,
                greenPaletteSizeLabel,
                greenPaletteTextField,
                greenPaletteSizeSlider,
                bluePaletteSizeLabel,
                bluePaletteTextField,
                bluePaletteSizeSlider
        );

        return paletteParametersPanel;
    }

    private JSlider initJSlider(JTextField textField) {
        JSlider slider = new JSlider(
                JSlider.HORIZONTAL,
                MIN_PALETTE_SIZE,
                MAX_PALETTE_SIZE,
                INITIAL_PALETTE_SIZE
        );
        slider.setMajorTickSpacing(32);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(e -> textField.setText(String.valueOf(slider.getValue())));
        return slider;
    }

    private void initJTextField(JTextField textField, String text, JSlider slider) {
        PlainDocument doc = (PlainDocument) textField.getDocument();
        doc.setDocumentFilter(MyDocumentFilter.getIntFilter(MIN_PALETTE_SIZE, MAX_PALETTE_SIZE));
        textField.addKeyListener(new MyKeyAdapter(slider, textField));
        textField.setText(text);
    }

    private JLabel initJLabel(String text) {
        JLabel jLabel = new JLabel(text);
        jLabel.setHorizontalAlignment(JLabel.RIGHT);
        return jLabel;
    }

}
