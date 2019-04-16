package ru.nsu.fit.semenov.filter.my_frames;

import ru.nsu.fit.semenov.filter.frame.BaseFrame;
import ru.nsu.fit.semenov.filter.util.FrameUtils;
import ru.nsu.fit.semenov.filter.util.MyDocumentFilter;
import ru.nsu.fit.semenov.filter.util.MyKeyAdapter;

import javax.swing.*;
import javax.swing.text.PlainDocument;
import java.awt.*;

public class RotationSettingsFrame extends BaseFrame {

    public interface ResultListener {

        void onFinished(Result result);

    }

    public static class Result {

        private final int rotationAngle;

        public Result(int rotationAngle) {
            this.rotationAngle = rotationAngle;
        }

        public int getRotationAngle() {
            return rotationAngle;
        }

    }

    private static final int MIN_ROTATION_ANGLE = -180;
    private static final int MAX_ROTATION_ANGLE = 180;
    private static final int INITIAL_ROTATION_ANGLE = 0;
    private static final int FRAME_WIDTH = 400;
    private static final int FRAME_HEIGHT = 170;

    private final ResultListener resultListener;
    private final JTextField rotationAngleTextField = new JTextField();

    public RotationSettingsFrame(BaseFrame intentionFrame, ResultListener resultListener) {
        super(FRAME_WIDTH, FRAME_HEIGHT, "Error Diffusion Parameters", intentionFrame);
        this.resultListener = resultListener;
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        add(initRotationAnglePanel());
        add(initControlPanel());
    }

    @Override
    protected void okAction() {
        int rotationAngle;
        try {
            rotationAngle = Integer.parseInt(rotationAngleTextField.getText());
        } catch (NumberFormatException err) {
            JOptionPane.showMessageDialog(null, "You have to specify all of the parameters!");
            return;
        }
        resultListener.onFinished(new Result(rotationAngle));
        super.okAction();
    }

    private JPanel initRotationAnglePanel() {
        JSlider rotationAngleSlider = new JSlider(
                JSlider.HORIZONTAL,
                MIN_ROTATION_ANGLE,
                MAX_ROTATION_ANGLE,
                INITIAL_ROTATION_ANGLE
        );
        rotationAngleSlider.setMajorTickSpacing(90);
        rotationAngleSlider.setPaintTicks(true);
        rotationAngleSlider.setPaintLabels(true);
        rotationAngleSlider.addChangeListener(
                e -> rotationAngleTextField.setText(String.valueOf(rotationAngleSlider.getValue()))
        );

        PlainDocument doc = (PlainDocument) rotationAngleTextField.getDocument();
        doc.setDocumentFilter(MyDocumentFilter.getIntFilter(MIN_ROTATION_ANGLE, MAX_ROTATION_ANGLE));
        rotationAngleTextField.addKeyListener(new MyKeyAdapter(rotationAngleSlider, rotationAngleTextField));
        rotationAngleTextField.setText(String.valueOf(INITIAL_ROTATION_ANGLE));
        rotationAngleTextField.setColumns(5);

        JLabel rotationAngleLabel = new JLabel("Rotation Angle");
        rotationAngleLabel.setHorizontalAlignment(JLabel.RIGHT);

        JPanel paletteParametersPanel = new JPanel();
        paletteParametersPanel.setLayout(new FlowLayout());
        paletteParametersPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Palette size parameters"));
        FrameUtils.addAllToPanel(
                paletteParametersPanel,
                rotationAngleLabel,
                rotationAngleTextField,
                rotationAngleSlider
        );
        return paletteParametersPanel;
    }

}
