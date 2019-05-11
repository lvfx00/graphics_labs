package ru.nsu.fit.g16205.semenov.wireframe.generatrix;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.wireframe.frame_utils.BaseFrame;
import ru.nsu.fit.g16205.semenov.wireframe.frame_utils.FrameUtils;
import ru.nsu.fit.g16205.semenov.wireframe.frame_utils.MyDocumentFilter;
import ru.nsu.fit.g16205.semenov.wireframe.model.DoublePoint;
import ru.nsu.fit.g16205.semenov.wireframe.model.DoubleRectangle;
import ru.nsu.fit.g16205.semenov.wireframe.model.IntPoint;
import ru.nsu.fit.g16205.semenov.wireframe.utils.transformer.CoordsTransformer;
import ru.nsu.fit.g16205.semenov.wireframe.utils.ImageUtils;
import ru.nsu.fit.g16205.semenov.wireframe.utils.transformer.CoordsTransformerImpl;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GeneratrixFrame extends BaseFrame {

    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;
    private static final double IMAGE_WIDTH_TO_HEIGHT_RATIO = 3. / 2;
    private static final Color PREVIEW_BACKGROUND_COLOR = Color.BLACK;
    private static final int NO_SELECTED_POINT = -1;
    private static final int CIRCLE_RADIUS = 8;
    private static final DoubleRectangle DEFINITION_AREA = new DoubleRectangle(0, 0, 1, Math.PI * 2);
    private static final int INIT_N = 10;
    private static final int INIT_M = 10;
    private static final int INIT_K = 5;

    private final JLabel previewLabel = new JLabel();
    private final List<DoublePoint> anchorPoints = new ArrayList<>();
    private int selectedAnchorPointIndex = NO_SELECTED_POINT;
    private Dimension imageSize;
    private CoordsTransformer coordsTransformer;
    private BezierCurve bezierCurve = null;

    // [] for lambdas!
    private double[] a = new double[]{DEFINITION_AREA.getMinX()};
    private double[] b = new double[]{DEFINITION_AREA.getMinX() + DEFINITION_AREA.getWidth()};
    private double[] c = new double[]{DEFINITION_AREA.getMinY()};
    private double[] d = new double[]{DEFINITION_AREA.getMinY() + DEFINITION_AREA.getHeight()};
    private int[] n = new int[]{INIT_N};
    private int[] m = new int[]{INIT_M};
    private int[] k = new int[]{INIT_K};

    public GeneratrixFrame() {
        super(FRAME_WIDTH, FRAME_HEIGHT, "Generatrix Options", null);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.add(initLayeredPane());
//        mainPanel.add(initParametersPanel());
        mainPanel.add(initControlPanel());
        add(mainPanel);
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
            PreviewDrawer.drawBezierCurve(previewImage, bezierCurve, coordsTransformer, a[0], b[0]);
        }
        previewLabel.setIcon(new ImageIcon(previewImage));
        previewLabel.setBounds(0, 0, imageSize.width, imageSize.height);
    }

    private @Nullable
    DoublePoint findSelectedAnchorPoint(DoublePoint clicked) {
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

//    private @NotNull JPanel initParametersPanel() {
//        JPanel parametersPanel = new JPanel();
//        parametersPanel.setLayout(new GridLayout(0, 4, 10, 10));
//
//        FrameUtils.addAllToPanel(parametersPanel,
//                FrameUtils.createJLabel("a:"),
//                FrameUtils.createJTextFieldWithListeners(
//                        String.valueOf(a[0]),
//                        10,
//                        MyDocumentFilter.getDoubleFilter()
//                ),
//                )
//
//    }

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
