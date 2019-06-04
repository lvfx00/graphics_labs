package ru.nsu.fit.g16205.semenov.raytracing.my_frames;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.raytracing.camera.CameraTransformer;
import ru.nsu.fit.g16205.semenov.raytracing.frame_utils.FrameUtils;
import ru.nsu.fit.g16205.semenov.raytracing.model.camera.CameraParameters;
import ru.nsu.fit.g16205.semenov.raytracing.model.camera.CameraPosition;
import ru.nsu.fit.g16205.semenov.raytracing.model.camera.PyramidOfView;
import ru.nsu.fit.g16205.semenov.raytracing.frame_utils.BaseMainFrame;
import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.DoublePoint3D;
import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.SphericalPoint;
import ru.nsu.fit.g16205.semenov.raytracing.utils.FileUtils;
import ru.nsu.fit.g16205.semenov.raytracing.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import static java.lang.Math.PI;
import static javax.swing.JOptionPane.*;
import static ru.nsu.fit.g16205.semenov.raytracing.Drawer.*;

public class MainFrame extends BaseMainFrame {

    private static final Dimension MIN_FRAME_SIZE = new Dimension(700, 850);
    private static final Dimension INIT_FRAME_SIZE = new Dimension(700, 850);
    private static final CameraPosition INIT_CAMERA_POSITION = new CameraPosition(
            new DoublePoint3D(5, 5, 5),
            new DoublePoint3D(0, 0, 0),
            new DoublePoint3D(0, 1, 0)
    );
    private static final PyramidOfView INIT_PYRAMID_OF_VIEW = new PyramidOfView(6, 6, 12, 4);
    private static final CameraParameters INIT_CAMERA_PARAMETERS = new CameraParameters(
            INIT_PYRAMID_OF_VIEW,
            INIT_CAMERA_POSITION
    );

    private Dimension imageSize;
    private final JLabel viewPortLabel = new JLabel();
    private CameraParameters cameraParameters = INIT_CAMERA_PARAMETERS;
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
        add(mainPanel);
    }

    private @NotNull JLayeredPane initLayeredPane() {
        final JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.add(viewPortLabel, 0);
        layeredPane.setPreferredSize(new Dimension(getWidth(), getHeight()));
        MouseAdapter layeredPaneMouseAdapter = new LayeredPaneMouseAdapter();
        layeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Component c = e.getComponent();
                if (c.getWidth() > c.getHeight()) {
                    imageSize = new Dimension(c.getHeight(), c.getHeight());
                } else {
                    imageSize = new Dimension(c.getWidth(), c.getWidth());
                }
                redrawAll();
            }
        });
        layeredPane.addMouseListener(layeredPaneMouseAdapter);
        layeredPane.addMouseMotionListener(layeredPaneMouseAdapter);
        layeredPane.addMouseWheelListener(layeredPaneMouseAdapter);
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
        viewPortLabel.setIcon(new ImageIcon(image));
        viewPortLabel.setSize(imageSize);
    }

    private void initMenus() {
        initFileMenu();
        initAboutMenu();
    }

    private void initFileMenu() {
        addSubMenu("File", KeyEvent.getExtendedKeyCodeForChar('f'));

        addMenuItem("File/New",
                "Open a new document",
                KeyEvent.getExtendedKeyCodeForChar('n'),
                "new_file.png",
                this::newFileAction);

        addToolBarButton("File/New");

        addMenuItem("File/Open",
                "Open an existing document",
                KeyEvent.getExtendedKeyCodeForChar('o'),
                "open_file.png",
                this::openFileAction);

        addToolBarButton("File/Open");

        addMenuItem("File/Save As",
                "Save current document",
                KeyEvent.getExtendedKeyCodeForChar('s'),
                "save_as.png",
                this::saveFileAction);

        addToolBarButton("File/Save As");

        addMenuItem("File/Exit",
                "Quit the application; prompts to save document",
                KeyEvent.getExtendedKeyCodeForChar('e'),
                "exit.png",
                this::exitAction);

        addToolBarSeparator();
    }


    private void initAboutMenu() {
        addSubMenu("About", KeyEvent.getExtendedKeyCodeForChar('a'));

        addMenuItem("About/About",
                "About author",
                KeyEvent.getExtendedKeyCodeForChar('a'),
                "about.png",
                this::aboutAction);

        addToolBarButton("About/About");
    }

    private void aboutAction() {
        startNewFrame(new AboutFrame(this));
    }

    private void newFileAction() {
        // TODO
    }

    private void openFileAction() {
        // TODO
    }

    private void saveFileAction() {
        // TODO
    }

    private void exitAction() {
        int result = JOptionPane.showConfirmDialog(null, "Do you want to save current document?");
        switch (result) {
            case CANCEL_OPTION:
                break;
            case OK_OPTION:
                saveFileAction();
                System.exit(0);
                break;
            case NO_OPTION:
                System.exit(0);
                break;
            default:
                throw new AssertionError("Invalid option specified");
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
        private int direction = 1;

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

            double newEta = oldPoint.getEta() + 1. / PI / 100 * dx * direction;
            double newFi = (oldPoint.getFi() + 1. / PI / 100 * -dy) % (PI * 2);
            if (newFi < 0) {
                newFi += PI * 2;
            }
            if (newEta < 0) {
                newEta += 2 * PI;
                direction = -direction;
            } else if (newEta > PI) {
                direction = -direction;
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
            updateParameters(new CameraParameters(
                    new PyramidOfView(
                            cameraParameters.getPyramidOfView().getSw(),
                            cameraParameters.getPyramidOfView().getSh(),
                            cameraParameters.getPyramidOfView().getZf(),
                            cameraParameters.getPyramidOfView().getZb() + ((double) e.getWheelRotation()) / 10
                    ),
                    cameraParameters.getCameraPosition()
            ));
        }

    }

}
