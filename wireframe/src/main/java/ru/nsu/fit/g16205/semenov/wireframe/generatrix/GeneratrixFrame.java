package ru.nsu.fit.g16205.semenov.wireframe.generatrix;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.wireframe.frame_utils.BaseFrame;
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
    private static final int CIRCLE_RADIUS = 8;
    private static final double IMAGE_WIDTH_TO_HEIGHT_RATIO = 3. / 2;
    private static final Color PREVIEW_BACKGROUND_COLOR = Color.BLACK;
    private static final Color CIRCLE_COLOR = Color.RED;
    private static final Color INACTIVE_CURVE_COLOR = Color.LIGHT_GRAY;
    private static final Color ACTIVE_CURVE_COLOR = Color.CYAN;
    private static final int NO_SELECTED_POINT = -1;

    private final JLabel previewLabel = new JLabel();
    private final DoubleRectangle definitionArea;
    private final List<DoublePoint> anchorPoints = new ArrayList<>();
    private int selectedAnchorPointIndex = NO_SELECTED_POINT;
    private Dimension imageSize;
    private CoordsTransformer coordsTransformer;
    private BezierCurve bezierCurve = null;

    private double startActive = 0.1;
    private double endActive = 0.8;
    private int n = 100;

    public GeneratrixFrame(@NotNull DoubleRectangle definitionArea) {
        super(FRAME_WIDTH, FRAME_HEIGHT, "Generatrix Options", null);
        this.definitionArea = definitionArea;

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.add(previewLabel, new Integer(0));
        layeredPane.addComponentListener(new LayeredPaneComponentListener());
        MouseAdapter layeredPaneMouseAdapter = new LayeredPaneMouseAdapter();
        layeredPane.addMouseListener(layeredPaneMouseAdapter);
        layeredPane.addMouseMotionListener(layeredPaneMouseAdapter);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.add(layeredPane);
        add(mainPanel);
    }

    private void refresh() {
        BufferedImage previewImage = ImageUtils.createImage(imageSize, PREVIEW_BACKGROUND_COLOR);

        List<IntPoint> anchorPointsOnImage = anchorPoints
                .stream()
                .map(c -> coordsTransformer.toPixel(c.getX(), c.getY()))
                .collect(Collectors.toList());
        PreviewCreator.drawAnchorPointsCircles(anchorPointsOnImage, previewImage, CIRCLE_COLOR, CIRCLE_RADIUS);

        if (anchorPoints.size() >= BezierCurve.MIN_POINTS_NUM) {
            bezierCurve = new BezierCurve(BezierCurve2DAdapter.pointsToMatrix(anchorPoints));
            PreviewCreator.drawBezierCurve(
                    bezierCurve,
                    previewImage,
                    coordsTransformer,
                    ACTIVE_CURVE_COLOR,
                    INACTIVE_CURVE_COLOR,
                    endActive * bezierCurve.getTotalLength(),
                    startActive * bezierCurve.getTotalLength(),
                    1000
            );
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

    private class LayeredPaneComponentListener extends ComponentAdapter {

        @Override
        public void componentResized(ComponentEvent e) {
            Component c = e.getComponent();
            imageSize = matchRatio(c.getWidth(), c.getHeight());
            coordsTransformer = new CoordsTransformerImpl(
                    new DoublePoint(definitionArea.getMinX(), definitionArea.getMinY()),
                    definitionArea.getWidth() / imageSize.width,
                    definitionArea.getHeight() / imageSize.height
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
