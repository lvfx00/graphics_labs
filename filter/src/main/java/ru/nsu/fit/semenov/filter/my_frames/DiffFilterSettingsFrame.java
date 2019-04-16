package ru.nsu.fit.semenov.filter.my_frames;

import ru.nsu.fit.semenov.filter.filters.DifferentiatingFilterType;
import ru.nsu.fit.semenov.filter.frame.BaseFrame;
import ru.nsu.fit.semenov.filter.util.FrameUtils;
import ru.nsu.fit.semenov.filter.util.MyDocumentFilter;
import ru.nsu.fit.semenov.filter.util.MyKeyAdapter;

import javax.swing.*;
import javax.swing.text.PlainDocument;
import java.awt.*;

import static ru.nsu.fit.semenov.filter.filters.DifferentiatingFilterType.*;

public class DiffFilterSettingsFrame extends BaseFrame {

    public interface ResultListener {

        void onFinished(Result result);

    }

    public static class Result {

        private final int limit;
        private final DifferentiatingFilterType type;

        public Result(int limit, DifferentiatingFilterType type) {
            this.limit = limit;
            this.type = type;
        }

        public int getLimit() {
            return limit;
        }

        public DifferentiatingFilterType getType() {
            return type;
        }

    }

    private static final int MIN_LIMIT_SIZE = 0;
    private static final int MAX_LIMIT_SIZE = 256;
    private static final int INITIAL_LIMIT_SIZE = 48;
    private static final int FRAME_WIDTH = 520;
    private static final int FRAME_HEIGHT = 200;

    private final ResultListener resultListener;
    private final JTextField limitTextField = new JTextField();
    private DifferentiatingFilterType filterType;

    public DiffFilterSettingsFrame(BaseFrame intentionFrame, ResultListener resultListener) {
        super(FRAME_WIDTH, FRAME_HEIGHT, "Differentiating Filter Settings", intentionFrame);
        this.resultListener = resultListener;
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        add(initParametersPanel());
        add(initDiffFilterTypePanel());
        add(initControlPanel());
    }

    @Override
    protected void okAction() {
        int limit;
        try {
            limit = Integer.parseInt(limitTextField.getText());
        } catch (NumberFormatException err) {
            JOptionPane.showMessageDialog(null, "You have to specify all of the parameters!");
            return;
        }
        resultListener.onFinished(new Result(limit, filterType));
        super.okAction();
    }

    private JPanel initDiffFilterTypePanel() {
        filterType = ROBERTS_OPERATOR;
        JRadioButton robertsOperatorButton = new JRadioButton("Roberts Operator", true);
        robertsOperatorButton.addActionListener(e -> filterType = ROBERTS_OPERATOR);
        JRadioButton sobelOperatorButton = new JRadioButton("Sobel Operator", false);
        sobelOperatorButton.addActionListener(e -> filterType = SOBEL_OPERATOR);
        JRadioButton borderSelectionButton = new JRadioButton("Border Selection", false);
        borderSelectionButton.addActionListener(e -> filterType = BORDER_SELECTION);
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(robertsOperatorButton);
        buttonGroup.add(sobelOperatorButton);
        buttonGroup.add(borderSelectionButton);
        JPanel filterTypePanel = new JPanel();
        filterTypePanel.setLayout(new FlowLayout());
        filterTypePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Filter Type"));
        filterTypePanel.add(robertsOperatorButton);
        filterTypePanel.add(sobelOperatorButton);
        filterTypePanel.add(borderSelectionButton);
        return filterTypePanel;
    }

    private JPanel initParametersPanel() {
        JSlider limitSlider = initJSlider(limitTextField);
        initJTextField(limitTextField, String.valueOf(INITIAL_LIMIT_SIZE), limitSlider);
        JLabel limitLabel = initJLabel("Limit level:");
        JPanel parametersPanel = new JPanel();
        parametersPanel.setLayout(new FlowLayout());
        FrameUtils.addAllToPanel(
                parametersPanel,
                limitLabel,
                limitTextField,
                limitSlider
        );
        return parametersPanel;
    }

    private JSlider initJSlider(JTextField textField) {
        JSlider slider = new JSlider(
                JSlider.HORIZONTAL,
                MIN_LIMIT_SIZE,
                MAX_LIMIT_SIZE,
                INITIAL_LIMIT_SIZE
        );
        slider.setMajorTickSpacing(64);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(e -> textField.setText(String.valueOf(slider.getValue())));
        return slider;
    }

    private void initJTextField(JTextField textField, String text, JSlider slider) {
        textField.setColumns(5);
        PlainDocument doc = (PlainDocument) textField.getDocument();
        doc.setDocumentFilter(MyDocumentFilter.getIntFilter(MIN_LIMIT_SIZE, MAX_LIMIT_SIZE));
        textField.addKeyListener(MyKeyAdapter.fromIntKeyAdapter(slider, textField));
        textField.setText(text);
    }

    private JLabel initJLabel(String text) {
        JLabel jLabel = new JLabel(text);
        jLabel.setHorizontalAlignment(JLabel.RIGHT);
        return jLabel;
    }
}
