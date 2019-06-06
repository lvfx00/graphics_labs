package ru.nsu.fit.g16205.semenov.wireframe.my_frames;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.wireframe.camera.CameraTransformer;
import ru.nsu.fit.g16205.semenov.wireframe.frame_utils.FrameUtils;
import ru.nsu.fit.g16205.semenov.wireframe.model.camera.CameraParameters;
import ru.nsu.fit.g16205.semenov.wireframe.model.camera.CameraPosition;
import ru.nsu.fit.g16205.semenov.wireframe.model.camera.PyramidOfView;
import ru.nsu.fit.g16205.semenov.wireframe.frame_utils.BaseMainFrame;
import ru.nsu.fit.g16205.semenov.wireframe.model.figure.FigureData;
import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.DoublePoint3D;
import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.SphericalPoint;
import ru.nsu.fit.g16205.semenov.wireframe.parser.Config;
import ru.nsu.fit.g16205.semenov.wireframe.parser.Parser;
import ru.nsu.fit.g16205.semenov.wireframe.utils.FileUtils;
import ru.nsu.fit.g16205.semenov.wireframe.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.PI;
import static javax.swing.JOptionPane.*;
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
    private static final Color INIT_BACKGROUND_COLOR = Color.WHITE;
    private static final CameraParameters INIT_CAMERA_PARAMETERS = new CameraParameters(
            INIT_PYRAMID_OF_VIEW,
            INIT_CAMERA_POSITION,
            INIT_BACKGROUND_COLOR
    );

    private Dimension imageSize;
    private final JComboBox<FigureData> figuresComboBox = new JComboBox<>();
    private final JLabel viewPortLabel = new JLabel();
    private final List<FigureData> figures = new ArrayList<>();
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
        final BufferedImage image = ImageUtils.createImage(imageSize, cameraParameters.getBackgroundColor());
        drawWorldOrts(image, cameraTransformer, 5);
        drawCube(image, cameraTransformer);
        figures.forEach(figureData -> drawFigure(image, figureData, cameraTransformer));
        viewPortLabel.setIcon(new ImageIcon(image));
        viewPortLabel.setSize(imageSize);
    }

    private void initMenus() {
        initFileMenu();
        initEditMenu();
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
        figures.clear();
        figuresComboBox.removeAllItems();
        updateParameters(INIT_CAMERA_PARAMETERS);
    }

    private void openFileAction() {
        final File file = FileUtils.getOpenFileName(this, "txt", "Text files");
        if (file == null) {
            return;
        }
        final Config config;
        try {
            config = Parser.parseFile(file);
        } catch (IOException e) {
            showMessageDialog(null, "I/O error happened :(");
            return;
        } catch (ParseException e) {
            showMessageDialog(null, "Invalid file specified :(");
            return;
        }
        figures.clear();
        figures.addAll(config.getFigureDataList());
        figuresComboBox.removeAllItems();
        config.getFigureDataList().forEach(figuresComboBox::addItem);
        updateParameters(config.getCameraParameters());
    }

    private void saveFileAction() {
        final File file = FileUtils.getSaveFileName(this, "txt", "Text files");
        if (file == null) {
            return;
        }
        final Config config = new Config(figures, cameraParameters);
        try {
            Parser.saveToFile(config, file);
        } catch (IOException e) {
            showMessageDialog(null, "I/O error happened :(");
        }
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

            updateParameters(new CameraParameters(
                    cameraParameters.getPyramidOfView(),
                    new CameraPosition(
                            cameraPoint.toDekartCoords(),
                            cameraParameters.getCameraPosition().getViewPoint(),
                            cameraParameters.getCameraPosition().getUpVector()
                    ),
                    cameraParameters.getBackgroundColor()
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
                    cameraParameters.getCameraPosition(),
                    cameraParameters.getBackgroundColor()
            ));
        }

    }

}
