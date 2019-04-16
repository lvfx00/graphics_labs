package ru.nsu.fit.semenov.filter.my_frames;

import ru.nsu.fit.semenov.filter.frame.BaseFrame;
import ru.nsu.fit.semenov.filter.util.FrameUtils;
import ru.nsu.fit.semenov.filter.util.MyDocumentFilter;
import ru.nsu.fit.semenov.filter.util.MyKeyAdapter;

import javax.swing.*;
import javax.swing.text.PlainDocument;
import java.awt.*;

public class GammaSettingsFrame extends BaseFrame {

    public interface ResultListener {

        void onFinished(Result result);

    }

    public static class Result {

        private final float gamma;

        public Result(float gamma) {
            this.gamma = gamma;
        }

        public float getGamma() {
            return gamma;
        }

    }

    private static final float MIN_GAMMA = 0.01f;
    private static final float MAX_GAMMA = 5f;
    private static final float INITIAL_GAMMA = 1f;
    private static final int FRAME_WIDTH = 400;
    private static final int FRAME_HEIGHT = 170;

    private final ResultListener resultListener;
    private final JTextField gammaTextField = new JTextField();

    public GammaSettingsFrame(BaseFrame intentionFrame, ResultListener resultListener) {
        super(FRAME_WIDTH, FRAME_HEIGHT, "Gamma Parameters", intentionFrame);
        this.resultListener = resultListener;
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        add(initGammaPanel());
        add(initControlPanel());
    }

    @Override
    protected void okAction() {
        float gamma;
        try {
            gamma = Float.parseFloat(gammaTextField.getText());
        } catch (NumberFormatException err) {
            JOptionPane.showMessageDialog(null, "You have to specify all of the parameters!");
            return;
        }
        resultListener.onFinished(new Result(gamma));
        super.okAction();
    }

    private JPanel initGammaPanel() {
        JSlider gammaSlider = new JSlider(
                JSlider.HORIZONTAL,
                (int) (MIN_GAMMA * 100),
                (int) (MAX_GAMMA * 100),
                (int) (INITIAL_GAMMA * 100)
        );
        gammaSlider.setMajorTickSpacing(100);
        gammaSlider.setPaintTicks(true);
        gammaSlider.setPaintLabels(false);
        gammaSlider.addChangeListener(
                e -> gammaTextField.setText(String.valueOf(((float) gammaSlider.getValue()) / 100))
        );

        PlainDocument doc = (PlainDocument) gammaTextField.getDocument();
        doc.setDocumentFilter(MyDocumentFilter.getFloatFilter(MIN_GAMMA, MAX_GAMMA));
        gammaTextField.addKeyListener(new MyKeyAdapter(gammaSlider, gammaTextField) {
            @Override
            protected int toInt(String str) {
                float val = Float.parseFloat(str);
                return (int) (val * 100);
            }
        });
        gammaTextField.setText(String.valueOf(INITIAL_GAMMA));
        gammaTextField.setColumns(5);

        JLabel gammaLabel = new JLabel("Gamma Value");
        gammaLabel.setHorizontalAlignment(JLabel.RIGHT);

        JPanel gammaPanel = new JPanel();
        gammaPanel.setLayout(new FlowLayout());
        gammaPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Gamma parameters"));
        FrameUtils.addAllToPanel(
                gammaPanel,
                gammaLabel,
                gammaTextField,
                gammaSlider
        );
        return gammaPanel;
    }

}
