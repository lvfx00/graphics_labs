package ru.nsu.fit.g16205.semenov.raytracing.my_frames;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.raytracing.model.camera.CameraParameters;
import ru.nsu.fit.g16205.semenov.raytracing.model.camera.CameraPosition;
import ru.nsu.fit.g16205.semenov.raytracing.model.camera.PyramidOfView;
import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.DoublePoint3D;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static ru.nsu.fit.g16205.semenov.raytracing.frame_utils.FrameUtils.*;

public class CameraParametersPanel extends JPanel {

    public interface ChangeListener {

        void onParametersChanged(@NotNull CameraParameters parameters);

    }

    private static final int SPINNERS_WIDTH = 8;
    private static final double DOUBLE_STEP = 0.1;

    private final List<ChangeListener> changeListeners = new ArrayList<>();

    private JSpinner swSpinner;
    private JSpinner shSpinner;
    private JSpinner zfSpinner;
    private JSpinner zbSpinner;
    private JSpinner xPCamSpinner;
    private JSpinner yPCamSpinner;
    private JSpinner zPCamSpinner;
    private JSpinner xPViewSpinner;
    private JSpinner yPViewSpinner;
    private JSpinner zPViewSpinner;
    private JSpinner xUpVectSpinner;
    private JSpinner yUpVectSpinner;
    private JSpinner zUpVectSpinner;

    public CameraParametersPanel(@NotNull CameraParameters initialValues) {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        addAllToPanel(
                this,
                initPyramidParametersPanel(initialValues.getPyramidOfView()),
                initPointOfCameraParametersPanel(initialValues.getCameraPosition().getCameraPoint()),
                initPointOfViewParametersPanel(initialValues.getCameraPosition().getViewPoint()),
                initUpVectorParametersPanel(initialValues.getCameraPosition().getUpVector()),
                initControlButtonsPanel()
        );
    }

    public void setValues(@NotNull CameraParameters cameraParameters) {
        final CameraPosition cameraPosition = cameraParameters.getCameraPosition();
        final PyramidOfView pyramidOfView = cameraParameters.getPyramidOfView();

        swSpinner.setValue(pyramidOfView.getSw());
        shSpinner.setValue(pyramidOfView.getSh());
        zfSpinner.setValue(pyramidOfView.getZf());
        zbSpinner.setValue(pyramidOfView.getZb());

        xPCamSpinner.setValue(cameraPosition.getCameraPoint().getX());
        yPCamSpinner.setValue(cameraPosition.getCameraPoint().getY());
        zPCamSpinner.setValue(cameraPosition.getCameraPoint().getZ());

        xPViewSpinner.setValue(cameraPosition.getViewPoint().getX());
        yPViewSpinner.setValue(cameraPosition.getViewPoint().getY());
        zPViewSpinner.setValue(cameraPosition.getViewPoint().getZ());

        xUpVectSpinner.setValue(cameraPosition.getUpVector().getX());
        yUpVectSpinner.setValue(cameraPosition.getUpVector().getY());
        zUpVectSpinner.setValue(cameraPosition.getUpVector().getZ());

    }

    public void addChangeListener(@NotNull ChangeListener changeListener) {
        changeListeners.add(changeListener);
    }

    public void removeChangeListener(@NotNull ChangeListener changeListener) {
        changeListeners.remove(changeListener);
    }

    private @NotNull JPanel initPyramidParametersPanel(@NotNull PyramidOfView pyramidOfView) {
        final JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        swSpinner = initDoubleSpinnerForLength(pyramidOfView.getSw());
        shSpinner = initDoubleSpinnerForLength(pyramidOfView.getSh());
        zfSpinner = initDoubleSpinnerForLength(pyramidOfView.getZf());
        zbSpinner = initDoubleSpinnerForLength(pyramidOfView.getZb());
        addAllToPanel(
                panel,
                createJLabel("Sw:"),
                swSpinner,
                createJLabel("Sh:"),
                shSpinner,
                createJLabel("Zf:"),
                zfSpinner,
                createJLabel("Zb:"),
                zbSpinner
        );
        return panel;
    }

    private @NotNull JPanel initPointOfCameraParametersPanel(@NotNull DoublePoint3D initCameraPoint) {
        final JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        xPCamSpinner = initDoubleSpinnerForCoords(initCameraPoint.getX());
        yPCamSpinner = initDoubleSpinnerForCoords(initCameraPoint.getY());
        zPCamSpinner = initDoubleSpinnerForCoords(initCameraPoint.getZ());
        addAllToPanel(
                panel,
                createJLabel("Pcam X:"),
                xPCamSpinner,
                createJLabel("Pcam Y:"),
                yPCamSpinner,
                createJLabel("Pcam Z:"),
                zPCamSpinner
        );
        return panel;
    }

    private @NotNull JPanel initPointOfViewParametersPanel(@NotNull DoublePoint3D initPointOfView) {
        final JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        xPViewSpinner = initDoubleSpinnerForCoords(initPointOfView.getX());
        yPViewSpinner = initDoubleSpinnerForCoords(initPointOfView.getY());
        zPViewSpinner = initDoubleSpinnerForCoords(initPointOfView.getZ());
        addAllToPanel(
                panel,
                createJLabel("Pview X:"),
                xPViewSpinner,
                createJLabel("Pview Y:"),
                yPViewSpinner,
                createJLabel("Pview Z:"),
                zPViewSpinner
        );
        return panel;
    }

    private @NotNull JPanel initUpVectorParametersPanel(@NotNull DoublePoint3D initUpVector) {
        final JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        xUpVectSpinner = initDoubleSpinnerForCoords(initUpVector.getX());
        yUpVectSpinner = initDoubleSpinnerForCoords(initUpVector.getY());
        zUpVectSpinner = initDoubleSpinnerForCoords(initUpVector.getZ());
        addAllToPanel(
                panel,
                createJLabel("UpVect X:"),
                xUpVectSpinner,
                createJLabel("UpVect Y:"),
                yUpVectSpinner,
                createJLabel("UpVect Z:"),
                zUpVectSpinner
        );
        return panel;
    }

    private @NotNull JPanel initControlButtonsPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        final JButton setButton = new JButton("Set values");
        setButton.addActionListener(e -> {
            final double sw = (Double) swSpinner.getValue();
            final double sh = (Double) shSpinner.getValue();
            final double zf = (Double) zfSpinner.getValue();
            final double zb = (Double) zbSpinner.getValue();
            final double PcamX = (Double) xPCamSpinner.getValue();
            final double PcamY = (Double) yPCamSpinner.getValue();
            final double PcamZ = (Double) zPCamSpinner.getValue();
            final double PviewX = (Double) xPViewSpinner.getValue();
            final double PviewY = (Double) yPViewSpinner.getValue();
            final double PviewZ = (Double) zPViewSpinner.getValue();
            final double upVectX = (Double) xUpVectSpinner.getValue();
            final double upVectY = (Double) yUpVectSpinner.getValue();
            final double upVectZ = (Double) zUpVectSpinner.getValue();
            if (zb >= zf) {
                JOptionPane.showMessageDialog(null, "Constraints: zb < zf");
                return;
            }
            CameraParameters cameraParameters = new CameraParameters(
                    new PyramidOfView(sw, sh, zf, zb),
                    new CameraPosition(
                            new DoublePoint3D(PcamX, PcamY, PcamZ),
                            new DoublePoint3D(PviewX, PviewY, PviewZ),
                            new DoublePoint3D(upVectX, upVectY, upVectZ)
                    )
            );
            for (ChangeListener cl : changeListeners) {
                cl.onParametersChanged(cameraParameters);
            }
        });
        panel.add(setButton);
        return panel;
    }

    private static @NotNull JSpinner initDoubleSpinnerForCoords(double initValue) {
        return createSpinner(
                new SpinnerNumberModel(initValue, -Double.MAX_VALUE, Double.MAX_VALUE, DOUBLE_STEP),
                SPINNERS_WIDTH
        );
    }

    private static @NotNull JSpinner initDoubleSpinnerForLength(double initValue) {
        return createSpinner(
                new SpinnerNumberModel(initValue, 0, Double.MAX_VALUE, DOUBLE_STEP),
                SPINNERS_WIDTH
        );
    }

}
