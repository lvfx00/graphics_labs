package ru.nsu.fit.g16205.semenov.wireframe.my_frames;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.wireframe.model.figure.FigureParameters;
import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.DoublePoint3D;
import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.DoubleRectangle;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static ru.nsu.fit.g16205.semenov.wireframe.frame_utils.FrameUtils.*;


public class FigureParametersPanel extends JPanel {

    public interface ChangeListener {

        void onParametersChanged(@NotNull FigureParameters parameters);

    }

    private static final int MAX_DEGREES = 180;
    private static final int SPINNERS_WIDTH = 6;
    private static final int INT_STEP = 1;
    private static final double DOUBLE_STEP = 0.01;

    private final List<ChangeListener> changeListeners = new ArrayList<>();

    public FigureParametersPanel(@NotNull DoubleRectangle definitionArea, @NotNull FigureParameters initialValues) {

        final JPanel namePanel = new JPanel();
        namePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        final JTextField nameTextField = new JTextField(initialValues.getName(), 30);
        addAllToPanel(namePanel, createJLabel("Name:"), nameTextField);

        final double minA = definitionArea.getMinX();
        final double maxB = definitionArea.getMinX() + definitionArea.getWidth();
        final double minC = definitionArea.getMinY();
        final double maxD = definitionArea.getMinY() + definitionArea.getHeight();

        final JPanel abcdPanel = new JPanel();
        abcdPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        final JSpinner aSpinner = createSpinner(
                new SpinnerNumberModel(initialValues.getA(), minA, maxB, DOUBLE_STEP),
                SPINNERS_WIDTH
        );
        final JSpinner bSpinner = createSpinner(
                new SpinnerNumberModel(initialValues.getB(), minA, maxB, DOUBLE_STEP),
                SPINNERS_WIDTH
        );
        final JSpinner cSpinner = createSpinner(
                new SpinnerNumberModel(initialValues.getC(), minC, maxD, DOUBLE_STEP),
                SPINNERS_WIDTH
        );
        final JSpinner dSpinner = createSpinner(
                new SpinnerNumberModel(initialValues.getD(), minC, maxD, DOUBLE_STEP),
                SPINNERS_WIDTH
        );
        addAllToPanel(
                abcdPanel,
                createJLabel("A:"),
                aSpinner,
                createJLabel("B:"),
                bSpinner,
                createJLabel("C:"),
                cSpinner,
                createJLabel("D:"),
                dSpinner
        );

        final JPanel nmkPanel = new JPanel();
        nmkPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        final JSpinner nSpinner = createSpinner(
                new SpinnerNumberModel(initialValues.getN(), 1, 1000, INT_STEP),
                SPINNERS_WIDTH
        );
        final JSpinner mSpinner = createSpinner(
                new SpinnerNumberModel(initialValues.getM(), 1, 1000, INT_STEP),
                SPINNERS_WIDTH
        );
        final JSpinner kSpinner = createSpinner(
                new SpinnerNumberModel(initialValues.getK(), 1, 100, INT_STEP),
                SPINNERS_WIDTH
        );
        addAllToPanel(
                nmkPanel,
                createJLabel("N:"),
                nSpinner,
                createJLabel("M:"),
                mSpinner,
                createJLabel("K:"),
                kSpinner
        );

        final JPanel thetaPanel = new JPanel();
        thetaPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        final JSpinner thetaXSpinner = createSpinner(
                new SpinnerNumberModel(radiansToDegrees(initialValues.getThetaX()), -MAX_DEGREES, MAX_DEGREES, INT_STEP),
                SPINNERS_WIDTH
        );
        final JSpinner thetaYSpinner = createSpinner(
                new SpinnerNumberModel(radiansToDegrees(initialValues.getThetaY()), -MAX_DEGREES, MAX_DEGREES, INT_STEP),
                SPINNERS_WIDTH
        );
        final JSpinner thetaZSpinner = createSpinner(
                new SpinnerNumberModel(radiansToDegrees(initialValues.getThetaZ()), -MAX_DEGREES, MAX_DEGREES, INT_STEP),
                SPINNERS_WIDTH
        );
        addAllToPanel(
                thetaPanel,
                createJLabel("thetaX:"),
                thetaXSpinner,
                createJLabel("thetaY:"),
                thetaYSpinner,
                createJLabel("thetaZ:"),
                thetaZSpinner
        );

        final JPanel cPointPanel = new JPanel();
        thetaPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        final DoublePoint3D initialCPoint = initialValues.getPointC();
        final JSpinner cxSpinner = createSpinner(
                new SpinnerNumberModel(initialCPoint.getX(), -Double.MAX_VALUE, Double.MAX_VALUE, DOUBLE_STEP),
                SPINNERS_WIDTH + 4
        );
        final JSpinner cySpinner = createSpinner(
                new SpinnerNumberModel(initialCPoint.getY(), -Double.MAX_VALUE, Double.MAX_VALUE, DOUBLE_STEP),
                SPINNERS_WIDTH + 4
        );
        final JSpinner czSpinner = createSpinner(
                new SpinnerNumberModel(initialCPoint.getZ(), -Double.MAX_VALUE, Double.MAX_VALUE, DOUBLE_STEP),
                SPINNERS_WIDTH + 4
        );
        addAllToPanel(
                cPointPanel,
                createJLabel("Cx:"),
                cxSpinner,
                createJLabel("Cy:"),
                cySpinner,
                createJLabel("Cz:"),
                czSpinner
        );

        final JPanel colorPanel = new JPanel();
        colorPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        final JSpinner colorRedSpinner = createSpinner(
                new SpinnerNumberModel(initialValues.getColor().getRed(), 0, 255, INT_STEP),
                SPINNERS_WIDTH
        );
        final JSpinner colorGreenSpinner = createSpinner(
                new SpinnerNumberModel(initialValues.getColor().getGreen(), 0, 255, INT_STEP),
                SPINNERS_WIDTH
        );
        final JSpinner colorBlueSpinner = createSpinner(
                new SpinnerNumberModel(initialValues.getColor().getBlue(), 0, 255, INT_STEP),
                SPINNERS_WIDTH
        );
        addAllToPanel(
                colorPanel,
                createJLabel("RED:"),
                colorRedSpinner,
                createJLabel("GREEN:"),
                colorGreenSpinner,
                createJLabel("BLUE:"),
                colorBlueSpinner
        );

        final JPanel setButtonPanel = new JPanel();
        setButtonPanel.setLayout(new FlowLayout());
        final JButton setButton = new JButton("Set values");
        setButton.addActionListener(e -> {
            String name = nameTextField.getText();
            double a = (Double) aSpinner.getValue();
            double b = (Double) bSpinner.getValue();
            double c = (Double) cSpinner.getValue();
            double d = (Double) dSpinner.getValue();
            int n = (Integer) nSpinner.getValue();
            int m = (Integer) mSpinner.getValue();
            int k = (Integer) kSpinner.getValue();
            double thetaX = degreesToRadians((Double) thetaXSpinner.getValue());
            double thetaY = degreesToRadians((Double) thetaYSpinner.getValue());
            double thetaZ = degreesToRadians((Double) thetaZSpinner.getValue());
            double cx = (Double) cxSpinner.getValue();
            double cy = (Double) cySpinner.getValue();
            double cz = (Double) czSpinner.getValue();
            int red = (Integer) colorRedSpinner.getValue();
            int green = (Integer) colorGreenSpinner.getValue();
            int blue = (Integer) colorBlueSpinner.getValue();
            if (a > b || c > d) {
                JOptionPane.showMessageDialog(null, "Constraints: A < B && C < D");
                return;
            }
            FigureParameters figureParameters = new FigureParameters(
                    a, b, c, d,
                    n, m, k,
                    new DoublePoint3D(cx, cy, cz),
                    thetaX, thetaY, thetaZ,
                    new Color(red, green, blue),
                    name
            );
            for (ChangeListener cl : changeListeners) {
                cl.onParametersChanged(figureParameters);
            }
        });
        setButtonPanel.add(setButton);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        addAllToPanel(this, namePanel, abcdPanel, nmkPanel, thetaPanel, cPointPanel, colorPanel, setButtonPanel);
    }

    public void addChangeListener(@NotNull ChangeListener changeListener) {
        changeListeners.add(changeListener);
    }

    public void removeChangeListener(@NotNull ChangeListener changeListener) {
        changeListeners.remove(changeListener);
    }

    private static double radiansToDegrees(double radians) {
        return radians / Math.PI * MAX_DEGREES;
    }

    private static double degreesToRadians(double degrees) {
        return degrees / MAX_DEGREES * Math.PI;
    }

}
