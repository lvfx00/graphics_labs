package ru.nsu.fit.g16205.semenov.wireframe.my_frames;

import org.apache.commons.lang3.tuple.Pair;
import org.ejml.simple.SimpleMatrix;
import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.wireframe.ProjectionDrawer;
import ru.nsu.fit.g16205.semenov.wireframe.SurfaceCreator;
import ru.nsu.fit.g16205.semenov.wireframe.camera.CameraTransformer;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainFrame extends BaseMainFrame {

    private static final Dimension MIN_FRAME_SIZE = new Dimension(800, 600);
    private static final Dimension INIT_FRAME_SIZE = new Dimension(800, 600);
    private static final CameraPosition INIT_CAMERA_POSITION = new CameraPosition(
            new DoublePoint3D(5, 5, 5),
            new DoublePoint3D(0, 0, 0),
            new DoublePoint3D(0, 1, 0)
    );
    private static final PyramidOfView INIT_PYRAMID_OF_VIEW = new PyramidOfView(6, 6, 12, 4);

    private final JLabel viewPortLabel = new JLabel();
    private final List<FigureData> figures = new ArrayList<>();
    private CameraParameters cameraParameters = new CameraParameters(INIT_PYRAMID_OF_VIEW, INIT_CAMERA_POSITION);
    private CameraTransformer cameraTransformer = new CameraTransformer(cameraParameters);
    private BufferedImage viewPortImage;
    private SphericalPoint cameraPoint = SphericalPoint.fromDekartCoords(INIT_CAMERA_POSITION.getCameraPoint());

    public MainFrame() {
        super(INIT_FRAME_SIZE.width, INIT_FRAME_SIZE.height, "Wireframe");
        initMenus();

        setMinimumSize(MIN_FRAME_SIZE);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        viewPortImage = ImageUtils.createImage(new Dimension(500, 500), Color.WHITE);
        viewPortLabel.setIcon(new ImageIcon(viewPortImage));
        viewPortLabel.setSize(new Dimension(800, 600));

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.add(viewPortLabel, 0);
        layeredPane.setPreferredSize(new Dimension(getWidth(), getHeight()));
        MouseAdapter layeredPaneMouseAdapter = new LayeredPaneMouseAdapter();
        layeredPane.addMouseListener(layeredPaneMouseAdapter);
        layeredPane.addMouseMotionListener(layeredPaneMouseAdapter);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                layeredPane.setPreferredSize(new Dimension(e.getComponent().getWidth(), e.getComponent().getHeight()));
            }
        });
        mainPanel.add(layeredPane);

        final CameraParametersPanel cameraParametersPanel = new CameraParametersPanel(cameraParameters);
        cameraParametersPanel.addChangeListener(parameters -> {
            cameraParameters = parameters;
            cameraTransformer = new CameraTransformer(cameraParameters);
            redrawAll();
        });
        mainPanel.add(cameraParametersPanel);
        add(mainPanel);
    }

    private void redrawAll() {
        viewPortImage = ImageUtils.createImage(new Dimension(500, 500), Color.WHITE);
        for (FigureData figureData : figures) {
            drawFigure(figureData);
        }
        drawWorldOrts();
        drawCube();
        viewPortLabel.setIcon(new ImageIcon(viewPortImage));
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

        addToolBarSeparator();
    }

    private void openFileAction() {
    }

    private void exitAction() {
    }

    private void addFigureAction() {
        startNewFrame(new FigureEditFrame(this, null, figureData -> {
            figures.add(figureData);
            drawFigure(figureData);
        }));
    }

    private void editFigureAction() {
        // TODO
    }

    private void drawFigure(@NotNull FigureData figureData) {
        ProjectionDrawer.drawProjection(
                viewPortImage,
                cameraTransformer.worldToViewPort(
                        SurfaceCreator.createSurface(figureData),
                        new Dimension(viewPortImage.getWidth(), viewPortImage.getHeight())
                ),
                Color.BLACK // TODO add color choosing
        );
    }

    private void drawWorldOrts() {
        ProjectionDrawer.drawProjection(
                viewPortImage,
                cameraTransformer.worldToViewPort(
                        Collections.singletonList(Pair.of(
                                new DoublePoint3D(0, 0, 0),
                                new DoublePoint3D(5, 0, 0)
                        )),
                        new Dimension(viewPortImage.getWidth(), viewPortImage.getHeight())
                ),
                Color.BLUE
        );
        ProjectionDrawer.drawProjection(
                viewPortImage,
                cameraTransformer.worldToViewPort(
                        Collections.singletonList(Pair.of(
                                new DoublePoint3D(0, 0, 0),
                                new DoublePoint3D(0, 5, 0)
                        )),
                        new Dimension(viewPortImage.getWidth(), viewPortImage.getHeight())
                ),
                Color.RED
        );
        ProjectionDrawer.drawProjection(
                viewPortImage,
                cameraTransformer.worldToViewPort(
                        Collections.singletonList(Pair.of(
                                new DoublePoint3D(0, 0, 0),
                                new DoublePoint3D(0, 0, 5)
                        )),
                        new Dimension(viewPortImage.getWidth(), viewPortImage.getHeight())
                ),
                Color.GREEN
        );
    }

    private void drawCube() {
        ProjectionDrawer.drawProjection(
                viewPortImage,
                cameraTransformer.worldToViewPort(
                        Arrays.asList(
                                Pair.of(new DoublePoint3D(0, 0, 0), new DoublePoint3D(1, 0, 0)),
                                Pair.of(new DoublePoint3D(0, 0, 0), new DoublePoint3D(0, 1, 0)),
                                Pair.of(new DoublePoint3D(0, 0, 0), new DoublePoint3D(0, 0, 1)),
                                Pair.of(new DoublePoint3D(1, 0, 0), new DoublePoint3D(1, 1, 0)),
                                Pair.of(new DoublePoint3D(1, 0, 0), new DoublePoint3D(1, 0, 1)),
                                Pair.of(new DoublePoint3D(0, 1, 0), new DoublePoint3D(1, 1, 0)),
                                Pair.of(new DoublePoint3D(0, 1, 0), new DoublePoint3D(0, 1, 1)),
                                Pair.of(new DoublePoint3D(0, 0, 1), new DoublePoint3D(1, 0, 1)),
                                Pair.of(new DoublePoint3D(0, 0, 1), new DoublePoint3D(0, 1, 1)),
                                Pair.of(new DoublePoint3D(0, 1, 1), new DoublePoint3D(1, 1, 1)),
                                Pair.of(new DoublePoint3D(1, 1, 0), new DoublePoint3D(1, 1, 1)),
                                Pair.of(new DoublePoint3D(1, 0, 1), new DoublePoint3D(1, 1, 1))
                        ),
                        new Dimension(viewPortImage.getWidth(), viewPortImage.getHeight())
                ),
                Color.MAGENTA
        );
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

            System.out.println("OLD " + cameraPoint);
            double newEta = (cameraPoint.getEta() + 1. / Math.PI / 100 * dy) % Math.PI;
            double newFi = (cameraPoint.getFi() + 1. / Math.PI / 100 * dx) % (Math.PI * 2);
            if (newEta < 0) {
                newEta += Math.PI;
            }
            if (newFi < 0) {
                newFi += Math.PI * 2;
            }
            cameraPoint = new SphericalPoint(cameraPoint.getR(), newEta, newFi);
            System.out.println("NEW " + cameraPoint);
            cameraParameters.getCameraPosition().setCameraPoint(cameraPoint.toDekartCoords());
            cameraTransformer = new CameraTransformer(cameraParameters);
            redrawAll();
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
        }

    }

}
