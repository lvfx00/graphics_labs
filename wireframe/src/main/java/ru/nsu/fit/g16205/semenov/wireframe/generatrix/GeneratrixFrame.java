package ru.nsu.fit.g16205.semenov.wireframe.generatrix;

import ru.nsu.fit.g16205.semenov.wireframe.frame_utils.BaseFrame;
import ru.nsu.fit.g16205.semenov.wireframe.model.IntCoord;
import ru.nsu.fit.g16205.semenov.wireframe.utils.ImageUtils;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class GeneratrixFrame extends BaseFrame {

    private static final Color FRAME_BACKGOUND_COLOR = Color.BLACK;
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;

    private static final int CIRCLE_RADIUS = 5;

    private BufferedImage previewImage;
    private final JLabel previewLabel = new JLabel();
    private final List<IntCoord> anchorPointsList = new ArrayList<>();
    private @Nullable IntCoord selectedAnchorPoint = null;

    public GeneratrixFrame() {
        super(FRAME_WIDTH, FRAME_HEIGHT, "Generatrix Options", null);
        getContentPane().setBackground(FRAME_BACKGOUND_COLOR);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        JPanel parametersPanel = new JPanel();
        JPanel previewPanel = new JPanel();
        previewPanel.add(previewLabel);
        mainPanel.add(previewPanel);
        mainPanel.add(parametersPanel);
        add(mainPanel);

        previewPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Component c = e.getComponent();
                // TODO scale by one ratio !!!
                previewImage = ImageUtils.resizeImage(previewImage, c.getWidth(), c.getHeight());
                previewLabel.setIcon(new ImageIcon(previewImage));
            }
        });

    }

    private void addAnchorPoint(int x, int y) {

    }

    private @Nullable IntCoord findSelectedAnchorPoint(int x, int y) {
        for (IntCoord coord : anchorPointsList) {
            if (intersects(coord.getX(), coord.getY(), x, y, CIRCLE_RADIUS)) {
                return coord;
            }
        }
        return null;
    }

    private static boolean intersects(int x1, int y1, int x2, int y2, int radius) {
        return (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 -y1) < radius * radius;
    }

    private class GeneratrixPreviewMouseAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            final IntCoord selectedPoint = findSelectedAnchorPoint(e.getX(), e.getY());
            if (selectedPoint == null) {
                addAnchorPoint(e.getX(), e.getY());
            } else {
                selectedAnchorPoint = selectedPoint;
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (selectedAnchorPoint == null) {
                return;
            }
        }

    }

}
