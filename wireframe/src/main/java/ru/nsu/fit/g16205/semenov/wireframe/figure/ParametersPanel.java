package ru.nsu.fit.g16205.semenov.wireframe.figure;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.wireframe.frame_utils.FrameUtils;
import ru.nsu.fit.g16205.semenov.wireframe.model.geometric.DoublePoint3D;
import ru.nsu.fit.g16205.semenov.wireframe.model.geometric.DoubleRectangle;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class ParametersPanel extends JPanel {

    public interface ChangeListener {

        void onParametersChanged(@NotNull FigureParameters parameters);

    }

    private static final int MAX_DEGREES = 180;
    private static final int SPINNERS_WIDTH = 6;
    private static final int INT_STEP = 1;
    private static final double DOUBLE_STEP = 0.01;

    private final List<ChangeListener> changeListeners = new ArrayList<>();

    public ParametersPanel(@NotNull DoubleRectangle definitionArea, @NotNull FigureParameters initialValues) {
        final double minA = definitionArea.getMinX();
        final double maxB = definitionArea.getMinX() + definitionArea.getWidth();
        final double minC = definitionArea.getMinY();
        final double maxD = definitionArea.getMinY() + definitionArea.getHeight();

        final JPanel abcdPanel = new JPanel();
        abcdPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        final JSpinner aSpinner = FrameUtils.createSpinner(
                new SpinnerNumberModel(initialValues.getA(), minA, maxB, DOUBLE_STEP),
                SPINNERS_WIDTH
        );
        final JSpinner bSpinner = FrameUtils.createSpinner(
                new SpinnerNumberModel(initialValues.getB(), minA, maxB, DOUBLE_STEP),
                SPINNERS_WIDTH
        );
        final JSpinner cSpinner = FrameUtils.createSpinner(
                new SpinnerNumberModel(initialValues.getC(), minC, maxD, DOUBLE_STEP),
                SPINNERS_WIDTH
        );
        final JSpinner dSpinner = FrameUtils.createSpinner(
                new SpinnerNumberModel(initialValues.getD(), minC, maxD, DOUBLE_STEP),
                SPINNERS_WIDTH
        );
        FrameUtils.addAllToPanel(
                abcdPanel,
                FrameUtils.createJLabel("A:"),
                aSpinner,
                FrameUtils.createJLabel("B:"),
                bSpinner,
                FrameUtils.createJLabel("C:"),
                cSpinner,
                FrameUtils.createJLabel("D:"),
                dSpinner
        );

        final JPanel nmkPanel = new JPanel();
        nmkPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        final JSpinner nSpinner = FrameUtils.createSpinner(
                new SpinnerNumberModel(initialValues.getN(), 1, 1000, INT_STEP),
                SPINNERS_WIDTH
        );
        final JSpinner mSpinner = FrameUtils.createSpinner(
                new SpinnerNumberModel(initialValues.getM(), 1, 1000, INT_STEP),
                SPINNERS_WIDTH
        );
        final JSpinner kSpinner = FrameUtils.createSpinner(
                new SpinnerNumberModel(initialValues.getK(), 1, 100, INT_STEP),
                SPINNERS_WIDTH
        );
        FrameUtils.addAllToPanel(
                nmkPanel,
                FrameUtils.createJLabel("N:"),
                nSpinner,
                FrameUtils.createJLabel("M:"),
                mSpinner,
                FrameUtils.createJLabel("K:"),
                kSpinner
        );

        final JPanel thetaPanel = new JPanel();
        thetaPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        final JSpinner thetaXSpinner = FrameUtils.createSpinner(
                new SpinnerNumberModel(radiansToDegrees(initialValues.getThetaX()), -MAX_DEGREES, MAX_DEGREES, INT_STEP),
                SPINNERS_WIDTH
        );
        final JSpinner thetaYSpinner = FrameUtils.createSpinner(
                new SpinnerNumberModel(radiansToDegrees(initialValues.getThetaY()), -MAX_DEGREES, MAX_DEGREES, INT_STEP),
                SPINNERS_WIDTH
        );
        final JSpinner thetaZSpinner = FrameUtils.createSpinner(
                new SpinnerNumberModel(radiansToDegrees(initialValues.getThetaZ()), -MAX_DEGREES, MAX_DEGREES, INT_STEP),
                SPINNERS_WIDTH
        );
        FrameUtils.addAllToPanel(
                thetaPanel,
                FrameUtils.createJLabel("thetaX:"),
                thetaXSpinner,
                FrameUtils.createJLabel("thetaY:"),
                thetaYSpinner,
                FrameUtils.createJLabel("thetaZ:"),
                thetaZSpinner
        );

        final JPanel cPointPanel = new JPanel();
        thetaPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        final DoublePoint3D initialCPoint = initialValues.getPointC();
        final JSpinner cxSpinner = FrameUtils.createSpinner(
                new SpinnerNumberModel(initialCPoint.getX(), -Double.MAX_VALUE, Double.MAX_VALUE, DOUBLE_STEP),
                SPINNERS_WIDTH + 4
        );
        final JSpinner cySpinner = FrameUtils.createSpinner(
                new SpinnerNumberModel(initialCPoint.getY(), -Double.MAX_VALUE, Double.MAX_VALUE, DOUBLE_STEP),
                SPINNERS_WIDTH + 4
        );
        final JSpinner czSpinner = FrameUtils.createSpinner(
                new SpinnerNumberModel(initialCPoint.getZ(), -Double.MAX_VALUE, Double.MAX_VALUE, DOUBLE_STEP),
                SPINNERS_WIDTH + 4
        );
        FrameUtils.addAllToPanel(
                cPointPanel,
                FrameUtils.createJLabel("Cx:"),
                cxSpinner,
                FrameUtils.createJLabel("Cy:"),
                cySpinner,
                FrameUtils.createJLabel("Cz:"),
                czSpinner
        );

        final JPanel setButtonPanel = new JPanel();
        setButtonPanel.setLayout(new FlowLayout());
        final JButton setButton = new JButton("Set values");
        setButton.addActionListener(e -> {
            double a = (Double) aSpinner.getValue();
            double b = (Double) bSpinner.getValue();
            double c = (Double) cSpinner.getValue();
            double d = (Double) dSpinner.getValue();
            int n = (Integer) nSpinner.getValue();
            int m = (Integer) mSpinner.getValue();
            int k = (Integer) kSpinner.getValue();
            double thetaX = degreesToRadians ((Double) thetaXSpinner.getValue());
            double thetaY = degreesToRadians ((Double) thetaYSpinner.getValue());
            double thetaZ = degreesToRadians ((Double) thetaZSpinner.getValue());
            double cx = (Double) cxSpinner.getValue();
            double cy = (Double) cySpinner.getValue();
            double cz = (Double) czSpinner.getValue();
            if (a > b || c > d) {
                JOptionPane.showMessageDialog(null, "Constraints: A < B && C < D");
                return;
            }
            FigureParameters figureParameters = new FigureParameters(
                    a, b, c, d, n, m, k, new DoublePoint3D(cx, cy, cz), thetaX, thetaY, thetaZ
            );
            for (ChangeListener cl : changeListeners) {
                cl.onParametersChanged(figureParameters);
            }
        });
        setButtonPanel.add(setButton);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        FrameUtils.addAllToPanel(this, abcdPanel, nmkPanel, thetaPanel, cPointPanel, setButtonPanel);
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
