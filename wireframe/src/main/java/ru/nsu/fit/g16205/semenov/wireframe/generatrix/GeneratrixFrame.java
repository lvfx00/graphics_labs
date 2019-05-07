package ru.nsu.fit.g16205.semenov.wireframe.generatrix;

import org.ejml.simple.SimpleMatrix;
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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class GeneratrixFrame extends BaseFrame {

    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;
    private static final int CIRCLE_RADIUS = 5;
    private static final double IMAGE_WIDTH_TO_HEIGHT_RATIO = 3. / 2;
    private static final Color PREVIEW_IMAGE_COLOR = Color.BLACK;
    private static final Color CIRCLE_COLOR = Color.RED;

    private final DoubleRectangle definitionArea;
    private final JLabel previewLabel = new JLabel();
    private final List<DoublePoint> anchorPoints = new ArrayList<>();
    private List<IntPoint> anchorPointsOnImage;
    private @Nullable
    DoublePoint selectedAnchorPoint = null;
    private Dimension imageSize;
    private CoordsTransformer coordsTransformer;

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
        BufferedImage previewImage = ImageUtils.createImage(imageSize, PREVIEW_IMAGE_COLOR);

        anchorPointsOnImage = anchorPoints
                .stream()
                .map(c -> coordsTransformer.toPixel(c.getX(), c.getY()))
                .collect(Collectors.toList());
        PreviewCreator.drawAnchorPoints(anchorPointsOnImage, previewImage, CIRCLE_COLOR, CIRCLE_RADIUS);

        previewLabel.setIcon(new ImageIcon(previewImage));
        previewLabel.setBounds(0, 0, imageSize.width, imageSize.height);
    }

    private @Nullable
    IntPoint findSelectedAnchorPoint(IntPoint clicked) {
        for (IntPoint point : anchorPointsOnImage) {
            if (intersects(point.getX(), point.getY(), clicked.getX(), clicked.getY())) {
                return point;
            }
        }
        return null;
    }

    // TODO вынести куда-нибудь
    private boolean intersects(int x1, int y1, int x2, int y2) {
        return (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1) < CIRCLE_RADIUS * CIRCLE_RADIUS;
    }

    private static SimpleMatrix toMatrix(@NotNull Collection<DoublePoint> coords) {
        SimpleMatrix matrix = new SimpleMatrix(coords.size(), 2);
        int i = 0;
        for (DoublePoint c : coords) {
            matrix.set(i, 0, c.getX());
            matrix.set(i, 1, c.getY());
            ++i;
        }
        return matrix;
    }

    private class LayeredPaneComponentListener extends ComponentAdapter {

        @Override
        public void componentResized(ComponentEvent e) {
            Component c = e.getComponent();
            imageSize = matchRatio(c.getWidth(), c.getHeight());
            coordsTransformer = new CoordsTransformerImpl(
                    new DoublePoint(definitionArea.getMinX(), definitionArea.getMinY()),
                    definitionArea.getWidth()/ imageSize.width,
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
            IntPoint clicked = new IntPoint(e.getX(), e.getY());
            final IntPoint selectedPoint = findSelectedAnchorPoint(clicked);
            if (selectedPoint == null) {
                DoublePoint c = coordsTransformer.toCoords(e.getX(), e.getY());
                anchorPoints.add(c);
                selectedAnchorPoint = c;
            } else {
                selectedAnchorPoint = anchorPoints.get(anchorPointsOnImage.indexOf(selectedPoint));
            }
            refresh();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            selectedAnchorPoint = null;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (selectedAnchorPoint == null) {
                System.out.println("never happened");
                return;
            }
            DoublePoint newAnchorPoint = new DoublePoint(e.getX(), e.getY());
            anchorPoints.set(anchorPoints.indexOf(selectedAnchorPoint), newAnchorPoint);
            selectedAnchorPoint = newAnchorPoint;
            refresh();
        }

    }

}
