package ru.nsu.fit.g16205.semenov.wireframe.generatrix;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.nsu.fit.g16205.semenov.wireframe.frame_utils.BaseFrame;
import ru.nsu.fit.g16205.semenov.wireframe.model.DoublePoint;
import ru.nsu.fit.g16205.semenov.wireframe.model.DoubleRectangle;
import ru.nsu.fit.g16205.semenov.wireframe.model.IntPoint;
import ru.nsu.fit.g16205.semenov.wireframe.utils.transformer.CoordsTransformer;
import ru.nsu.fit.g16205.semenov.wireframe.utils.ImageUtils;
import ru.nsu.fit.g16205.semenov.wireframe.utils.transformer.CoordsTransformerImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GeneratrixFrame extends BaseFrame {

    @FunctionalInterface
    public interface ResultListener {

        void onFinished(@NotNull CurveData curveData);

    }

    private static final Dimension MIN_FRAME_SIZE = new Dimension(550, 550);
    private static final Dimension INIT_FRAME_SIZE = new Dimension(550, 550);
    private static final double IMAGE_WIDTH_TO_HEIGHT_RATIO = 3. / 2;
    private static final Color PREVIEW_BACKGROUND_COLOR = Color.BLACK;
    private static final int NO_SELECTED_POINT = -1;
    private static final int CIRCLE_RADIUS = 8;
    private static final DoubleRectangle DEFINITION_AREA = new DoubleRectangle(-1, -1, 2, 2);
    private static final DoubleRectangle ABCD_AREA = new DoubleRectangle(0, 0, 1, 2);
    private static final int INIT_N = 10;
    private static final int INIT_M = 10;
    private static final int INIT_K = 5;

    private final @Nullable ResultListener resultListener;
    private final JLabel previewLabel = new JLabel();
    private final List<DoublePoint> anchorPoints = new ArrayList<>();
    private int selectedAnchorPointIndex = NO_SELECTED_POINT;
    private Dimension imageSize;
    private CoordsTransformer coordsTransformer;
    private @Nullable BezierCurve bezierCurve = null;
    private @NotNull CurveParameters curveParameters = new CurveParameters(
            ABCD_AREA.getMinX(),
            ABCD_AREA.getMinX() + DEFINITION_AREA.getWidth(),
            DEFINITION_AREA.getMinY(),
            DEFINITION_AREA.getMinY() + DEFINITION_AREA.getHeight(),
            INIT_N,
            INIT_M,
            INIT_K
    );

    public GeneratrixFrame(
            @Nullable BaseFrame intentionFrame,
            @Nullable CurveData initialData,
            @Nullable ResultListener resultListener
    ) {
        super(INIT_FRAME_SIZE.width, INIT_FRAME_SIZE.height, "Generatrix Options", intentionFrame);
        this.resultListener = resultListener;
        setMinimumSize(MIN_FRAME_SIZE);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        JLayeredPane layeredPane = initLayeredPane();
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                layeredPane.setPreferredSize(new Dimension(e.getComponent().getWidth(), e.getComponent().getHeight()));
            }
        });
        mainPanel.add(layeredPane);

        if (initialData != null) {
            anchorPoints.addAll(BezierCurve.Adapter2D.matrixToPointsList(initialData.getCurve().getAnchorPoints()));
            curveParameters = initialData.getParameters();
        }
        mainPanel.add(initParametersPanel());
        mainPanel.add(initControlPanel());
        add(mainPanel);
    }

    @Override
    protected void okAction() {
        if (bezierCurve != null) {
            if (resultListener != null) {
                resultListener.onFinished(new CurveData(bezierCurve, curveParameters));
            }
            onWindowClose(null);
        } else {
            JOptionPane.showMessageDialog(null, "You have to define at least 4 points for curve");
        }
    }

    private void refresh() {
        BufferedImage previewImage = ImageUtils.createImage(imageSize, PREVIEW_BACKGROUND_COLOR);
        List<IntPoint> anchorPointsOnImage = anchorPoints
                .stream()
                .map(c -> coordsTransformer.toPixel(c.getX(), c.getY()))
                .collect(Collectors.toList());
        PreviewDrawer.drawAnchorPointsCircles(anchorPointsOnImage, previewImage, CIRCLE_RADIUS);
        if (anchorPoints.size() >= BezierCurve.MIN_POINTS_NUM) {
            bezierCurve = new BezierCurve(BezierCurve.Adapter2D.pointsToMatrix(anchorPoints));
            PreviewDrawer.drawBezierCurve(previewImage, bezierCurve, coordsTransformer, curveParameters.getA(), curveParameters.getB());
        } else {
            bezierCurve = null;
        }
        previewLabel.setIcon(new ImageIcon(previewImage));
        previewLabel.setBounds(0, 0, imageSize.width, imageSize.height);
    }

    private @Nullable DoublePoint findSelectedAnchorPoint(DoublePoint clicked) {
        for (DoublePoint point : anchorPoints) {
            if (intersects(point.getX(), point.getY(), clicked.getX(), clicked.getY())) {
                return point;
            }
        }
        return null;
    }

    private boolean intersects(double x1, double y1, double x2, double y2) {
        DoublePoint rectangle = coordsTransformer.toCoords(CIRCLE_RADIUS, CIRCLE_RADIUS);
        return (Math.abs(x1 - x2) < rectangle.getX() && Math.abs(y1 - y2) < rectangle.getY());
    }

    private @NotNull JLayeredPane initLayeredPane() {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.add(previewLabel, new Integer(0));
        layeredPane.addComponentListener(new LayeredPaneComponentListener());
        MouseAdapter layeredPaneMouseAdapter = new LayeredPaneMouseAdapter();
        layeredPane.addMouseListener(layeredPaneMouseAdapter);
        layeredPane.addMouseMotionListener(layeredPaneMouseAdapter);
        return layeredPane;
    }

    private @NotNull JPanel initParametersPanel() {
        final ParametersPanel parametersPanel = new ParametersPanel(DEFINITION_AREA, curveParameters);
        parametersPanel.addChangeListener(parameters -> {
            curveParameters = parameters;
            refresh();
        });
        return parametersPanel;
    }

    private class LayeredPaneComponentListener extends ComponentAdapter {

        @Override
        public void componentResized(ComponentEvent e) {
            Component c = e.getComponent();
            imageSize = matchRatio(c.getWidth(), c.getHeight());
            coordsTransformer = new CoordsTransformerImpl(
                    new DoublePoint(DEFINITION_AREA.getMinX(), DEFINITION_AREA.getMinY()),
                    DEFINITION_AREA.getWidth() / imageSize.width,
                    DEFINITION_AREA.getHeight() / imageSize.height
            );
            refresh();
        }

        private Dimension matchRatio(int width, int height) {
            if (((double) width / height > IMAGE_WIDTH_TO_HEIGHT_RATIO)) {
                return new Dimension((int) Math.floor(height * IMAGE_WIDTH_TO_HEIGHT_RATIO), height);
            } else {
                return new Dimension(width, (int) Math.floor((double) width / IMAGE_WIDTH_TO_HEIGHT_RATIO));
            }
        }

    }

    private class LayeredPaneMouseAdapter extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            DoublePoint clicked = coordsTransformer.toCoords(getValidPoint(e.getX(), e.getY()));
            switch (e.getButton()) {
                case MouseEvent.BUTTON1:
                    handleLeftButtonClick(clicked);
                    break;
                case MouseEvent.BUTTON3:
                    handleRightButtonClick(clicked);
                    break;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            selectedAnchorPointIndex = NO_SELECTED_POINT;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (selectedAnchorPointIndex == NO_SELECTED_POINT) return;
            DoublePoint clicked = coordsTransformer.toCoords(getValidPoint(e.getX(), e.getY()));
            anchorPoints.set(selectedAnchorPointIndex, clicked);
            refresh();
        }

        private void handleLeftButtonClick(DoublePoint clicked) {
            final DoublePoint selectedPoint = findSelectedAnchorPoint(clicked);
            if (selectedPoint == null) {
                anchorPoints.add(clicked);
                selectedAnchorPointIndex = anchorPoints.indexOf(clicked);
            } else {
                selectedAnchorPointIndex = anchorPoints.indexOf(selectedPoint);
            }
            refresh();
        }

        private void handleRightButtonClick(DoublePoint clicked) {
            final DoublePoint selectedPoint = findSelectedAnchorPoint(clicked);
            if (selectedPoint != null) {
                anchorPoints.remove(selectedPoint);
            }
            refresh();
        }

        private IntPoint getValidPoint(int x, int y) {
            return new IntPoint(
                    Math.max(Math.min(x, imageSize.width - CIRCLE_RADIUS), CIRCLE_RADIUS),
                    Math.max(Math.min(y, imageSize.height - CIRCLE_RADIUS), CIRCLE_RADIUS)
            );
        }

    }

}
