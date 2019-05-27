package ru.nsu.fit.g16205.semenov.wireframe.my_frames;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.wireframe.Drawer;
import ru.nsu.fit.g16205.semenov.wireframe.camera.CameraTransformer;
import ru.nsu.fit.g16205.semenov.wireframe.frame_utils.FrameUtils;
import ru.nsu.fit.g16205.semenov.wireframe.model.camera.CameraParameters;
import ru.nsu.fit.g16205.semenov.wireframe.model.camera.CameraPosition;
import ru.nsu.fit.g16205.semenov.wireframe.model.camera.PyramidOfView;
import ru.nsu.fit.g16205.semenov.wireframe.frame_utils.BaseMainFrame;
import ru.nsu.fit.g16205.semenov.wireframe.model.figure.FigureData;
import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.DoublePoint3D;
import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.SphericalPoint;
import ru.nsu.fit.g16205.semenov.wireframe.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static ru.nsu.fit.g16205.semenov.wireframe.Drawer.*;

public class MainFrame extends BaseMainFrame {

    private static final Dimension MIN_FRAME_SIZE = new Dimension(700, 850);
    private static final Dimension INIT_FRAME_SIZE = new Dimension(700, 850);
    private static final CameraPosition INIT_CAMERA_POSITION = new CameraPosition(
            new DoublePoint3D(5, 5, 5),
            new DoublePoint3D(0, 0, 0),
            new DoublePoint3D(0, 1, 0)
    );
    private static final PyramidOfView INIT_PYRAMID_OF_VIEW = new PyramidOfView(6, 6, 12, 4);

    private Dimension imageSize;
    private final JComboBox<FigureData> figuresComboBox = new JComboBox<>();
    private final JLabel viewPortLabel = new JLabel();
    private final List<FigureData> figures = new ArrayList<>();
    private CameraParameters cameraParameters = new CameraParameters(INIT_PYRAMID_OF_VIEW, INIT_CAMERA_POSITION);
    private final CameraParametersPanel cameraParametersPanel = new CameraParametersPanel(cameraParameters);
    private CameraTransformer cameraTransformer = new CameraTransformer(cameraParameters);

    public MainFrame() {
        super(INIT_FRAME_SIZE.width, INIT_FRAME_SIZE.height, "Wireframe");
        initMenus();
        setMinimumSize(MIN_FRAME_SIZE);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.add(initLayeredPane());
        cameraParametersPanel.addChangeListener(this::updateParameters);
        mainPanel.add(cameraParametersPanel);
        mainPanel.add(initSelectedFigurePanel());
        add(mainPanel);
    }

    private @NotNull JPanel initSelectedFigurePanel() {
        final JPanel panel = new JPanel();
        panel.add(FrameUtils.createJLabel("Selected Figure:"));
        panel.add(figuresComboBox);
        return panel;
    }

    private @NotNull JLayeredPane initLayeredPane() {
        final JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.add(viewPortLabel, 0);
        layeredPane.setPreferredSize(new Dimension(getWidth(), getHeight()));
        MouseAdapter layeredPaneMouseAdapter = new LayeredPaneMouseAdapter();
        layeredPane.addComponentListener(new LayeredPaneComponentListener());
        layeredPane.addMouseListener(layeredPaneMouseAdapter);
        layeredPane.addMouseMotionListener(layeredPaneMouseAdapter);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                layeredPane.setPreferredSize(new Dimension(e.getComponent().getWidth(), e.getComponent().getHeight()));
            }
        });
        return layeredPane;
    }

    private void redrawAll() {
        final BufferedImage image = ImageUtils.createImage(imageSize, Color.WHITE);
        drawWorldOrts(image, cameraTransformer, 5);
        drawCube(image, cameraTransformer);
        figures.forEach(figureData -> drawFigure(image, figureData, cameraTransformer));
        viewPortLabel.setIcon(new ImageIcon(image));
        viewPortLabel.setSize(imageSize);
    }

    private void initMenus() {
        initFileMenu();
        initEditMenu();
    }

    private void initFileMenu() {
        addSubMenu("File", KeyEvent.getExtendedKeyCodeForChar('f'));

        addMenuItem("File/Open",
                "Open an existing document",
                KeyEvent.getExtendedKeyCodeForChar('o'),
                "open_file.png",
                this::openFileAction);

        addMenuItem("File/Exit",
                "Quit the application; prompts to save document",
                KeyEvent.getExtendedKeyCodeForChar('e'),
                "exit.png",
                this::exitAction);

        addToolBarButton("File/Open");

        addToolBarSeparator();
    }

    private void initEditMenu() {
        String submenu = "Edit";
        addSubMenu(submenu, KeyEvent.getExtendedKeyCodeForChar('e'));

        String menuPathString = submenu + "/Add Figure";
        addMenuItem(
                menuPathString,
                "Add new figure",
                KeyEvent.getExtendedKeyCodeForChar('a'),
                "add_figure.png",
                this::addFigureAction
        );
        addToolBarButton(menuPathString);

        menuPathString = submenu + "/Edit Figure";
        addMenuItem(
                menuPathString,
                "Edit existing figure",
                KeyEvent.getExtendedKeyCodeForChar('e'),
                "edit_figure.png",
                this::editFigureAction
        );
        addToolBarButton(menuPathString);

        menuPathString = submenu + "/Remove Figure";
        addMenuItem(
                menuPathString,
                "Remove existing figure",
                KeyEvent.getExtendedKeyCodeForChar('r'),
                "remove_figure.png",
                this::removeFigureAction
        );
        addToolBarButton(menuPathString);

        addToolBarSeparator();
    }

    private void openFileAction() {
    }

    private void exitAction() {
    }

    private void addFigureAction() {
        startNewFrame(new FigureEditFrame(this, null, figureData -> {
            figures.add(figureData);
            figuresComboBox.addItem(figureData);
            redrawAll();
        }));
    }

    private void editFigureAction() {
        FigureData selectedFigure = (FigureData) figuresComboBox.getSelectedItem();
        if (selectedFigure != null) {
            startNewFrame(new FigureEditFrame(this, selectedFigure, figureData -> {
                figures.remove(selectedFigure);
                figuresComboBox.removeItem(selectedFigure);
                figures.add(figureData);
                figuresComboBox.addItem(figureData);
                redrawAll();
            }));
        }
    }

    private void removeFigureAction() {
        FigureData selectedFigure = (FigureData) figuresComboBox.getSelectedItem();
        if (selectedFigure != null) {
            figures.remove(selectedFigure);
            figuresComboBox.removeItem(selectedFigure);
            redrawAll();
        }
    }

    private void updateParameters(@NotNull CameraParameters parameters) {
        cameraParameters = parameters;
        cameraTransformer = new CameraTransformer(cameraParameters);
        cameraParametersPanel.setValues(parameters);
        redrawAll();
    }

    private class LayeredPaneMouseAdapter extends MouseAdapter {
        private int x;
        private int y;

        @Override
        public void mousePressed(MouseEvent e) {
            x = e.getX();
            y = e.getY();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            final int dx = e.getX() - x;
            final int dy = e.getY() - y;
            y = e.getY();
            x = e.getX();

            final SphericalPoint oldPoint = SphericalPoint.fromDekartCoords(
                    cameraParameters.getCameraPosition().getCameraPoint()
            );
//            System.out.println("OLD " + oldPoint);

            double newEta = (oldPoint.getEta() + 1. / Math.PI / 100 * dx) % Math.PI;
            double newFi = (oldPoint.getFi() + 1. / Math.PI / 100 * -dy) % (Math.PI * 2);
            if (newFi < 0) {
                newFi += Math.PI * 2;
            }
            // TODO добавить полуоборот
            if (newEta < 0) {
                newEta += Math.PI;
            }
            final SphericalPoint cameraPoint = new SphericalPoint(oldPoint.getR(), newEta, newFi);
//            System.out.println("NEW " + cameraPoint);

            updateParameters(new CameraParameters(
                    cameraParameters.getPyramidOfView(),
                    new CameraPosition(
                            cameraPoint.toDekartCoords(),
                            cameraParameters.getCameraPosition().getViewPoint(),
                            cameraParameters.getCameraPosition().getUpVector()
                    )
            ));
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            // TODO
        }

    }

    private class LayeredPaneComponentListener extends ComponentAdapter {

        private static final double IMAGE_WIDTH_TO_HEIGHT_RATIO = 1;

        @Override
        public void componentResized(ComponentEvent e) {
            Component c = e.getComponent();
            imageSize = matchRatio(c.getWidth(), c.getHeight());
            redrawAll();
        }

        private Dimension matchRatio(int width, int height) {
            if (((double) width / height > IMAGE_WIDTH_TO_HEIGHT_RATIO)) {
                return new Dimension((int) Math.floor(height * IMAGE_WIDTH_TO_HEIGHT_RATIO), height);
            } else {
                return new Dimension(width, (int) Math.floor((double) width / IMAGE_WIDTH_TO_HEIGHT_RATIO));
            }
        }

    }

}
