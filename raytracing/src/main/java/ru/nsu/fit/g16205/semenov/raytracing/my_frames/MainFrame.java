package ru.nsu.fit.g16205.semenov.raytracing.my_frames;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.nsu.fit.g16205.semenov.raytracing.InitialRaysCreator;
import ru.nsu.fit.g16205.semenov.raytracing.LightComposer;
import ru.nsu.fit.g16205.semenov.raytracing.camera.CameraTransformer;
import ru.nsu.fit.g16205.semenov.raytracing.model.camera.CameraParameters;
import ru.nsu.fit.g16205.semenov.raytracing.model.camera.CameraPosition;
import ru.nsu.fit.g16205.semenov.raytracing.model.camera.PyramidOfView;
import ru.nsu.fit.g16205.semenov.raytracing.frame_utils.BaseMainFrame;
import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.DoubleLine;
import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.DoublePoint3D;
import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.SphericalPoint;
import ru.nsu.fit.g16205.semenov.raytracing.model.tracing_primitives.*;
import ru.nsu.fit.g16205.semenov.raytracing.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.awt.event.KeyEvent.getExtendedKeyCodeForChar;
import static java.lang.Math.PI;
import static javax.swing.JOptionPane.*;
import static ru.nsu.fit.g16205.semenov.raytracing.Drawer.*;
import static ru.nsu.fit.g16205.semenov.raytracing.Reflector.getRaytracing;

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
    private static final int RAYTRACING_DEPTH = 2;

    private Dimension imageSize;
    private final JLabel viewPortLabel = new JLabel();
    private CameraParameters cameraParameters = INIT_CAMERA_PARAMETERS;
    private final CameraParametersPanel cameraParametersPanel = new CameraParametersPanel(cameraParameters);
    private CameraTransformer cameraTransformer = new CameraTransformer(cameraParameters);

    private final List<RaytracingFigure> figures = new ArrayList<>();
    private final List<LightSource> lightSources = new ArrayList<>();
    private final Color ambientLight = Color.WHITE;
    private final List<Pair<List<DoubleLine>, Color>> additionalLines = new ArrayList<>();

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
        initFigures();
    }

    private void initFigures() {
        // рвсполагать плоскости по правилу правой руки!
        // upper
        figures.add(
                new RaytracingFigure(
                        new Triangle3D(
                                new DoublePoint3D(0, 0, 2),
                                new DoublePoint3D(2, 0, 0),
                                new DoublePoint3D(0, 2, 0)
                        ),
                        new OpticalProperties(0.5, 0.5, 0.5, 1, 1, 1, 30)
                )
        );
        // upper left
        figures.add(
                new RaytracingFigure(
                        new Triangle3D(
                                new DoublePoint3D(0, 2, 0),
                                new DoublePoint3D(2, 0, 0),
                                new DoublePoint3D(5, 0, 0)
                        ),
                        new OpticalProperties(0.9, 0.5, 0.5, 0.7, 0.7, 0.7, 15)
                )
        );
        // lower left
        figures.add(
                new RaytracingFigure(
                        new Triangle3D(
                                new DoublePoint3D(0, 2, 0),
                                new DoublePoint3D(5, 0, 0),
                                new DoublePoint3D(5, 3, 0)

                        ),
                        new OpticalProperties(0.9, 0.5, 0.5, 0.7, 0.7, 0.7, 15)
                )
        );
        // upper right
        figures.add(
                new RaytracingFigure(
                        new Triangle3D(
                                new DoublePoint3D(0, 2, 0),
                                new DoublePoint3D(-1, 0, 5),
                                new DoublePoint3D(0, 0, 2)
                        ),
                        new OpticalProperties(0.4, 0.4, 0.4, 1, 1, 1, 30)
                )
        );
        // lower right
        figures.add(
                new RaytracingFigure(
                        new Triangle3D(
                                new DoublePoint3D(0, 2, 0),
                                new DoublePoint3D(0, 3, 5),
                                new DoublePoint3D(-1, 0, 5)

                        ),
                        new OpticalProperties(0.4, 0.4, 0.4, 1, 1, 1, 30)
                )
        );
        // bottom
        figures.add(
                new RaytracingFigure(
                        new Triangle3D(
                                new DoublePoint3D(0, 2, 0),
                                new DoublePoint3D(5, 3, 0),
                                new DoublePoint3D(0, 3, 5)
                        ),
                        new OpticalProperties(0.7, 0.7, 1, 0.9, 0.9, 0.9, 10)
                )
        );
        // middle face to bottom
        figures.add(
                new RaytracingFigure(
                        new Triangle3D(
                                new DoublePoint3D(0, 3, 0),
                                new DoublePoint3D(1, 2, 4),
                                new DoublePoint3D(2.5, 2, 2.5)
                        ),
                        new OpticalProperties(0.6, 0.4, 0.4, 0.9, 0.7, 1, 10)
                )
        );
        // middle face to up
        figures.add(
                new RaytracingFigure(
                        new Triangle3D(
                                new DoublePoint3D(0, 3, 0),
                                new DoublePoint3D(4, 2, 1),
                                new DoublePoint3D(2.5, 2, 2.5)
                        ),
                        new OpticalProperties(0.5, 0.5, 0.5, 0.9, 0.9, 0.9, 10)
                )
        );
//        figures.add(new RaytracingFigure(new Triangle3D(
//                new DoublePoint3D(0, -5, 0),
//                new DoublePoint3D(5, -3, 0),
//                new DoublePoint3D(0, -3, 5)
//        ), opticalProperties));

        lightSources.add(new LightSource(new DoublePoint3D(1, 1, 1), Color.RED));
        lightSources.add(new LightSource(new DoublePoint3D(4, 1, 0.5), Color.GREEN));
        lightSources.add(new LightSource(new DoublePoint3D(0.5, 2.8, 4), Color.BLUE));

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
        additionalLines.forEach(p -> drawLines(image, cameraTransformer, p.getLeft(), p.getRight()));
        figures.forEach(f -> drawLines(image, cameraTransformer, f.getPrimitive().getFigureLines(), Color.BLACK));
        viewPortLabel.setIcon(new ImageIcon(image));
        viewPortLabel.setSize(imageSize);
    }

    private void initMenus() {
        initFileMenu();
        initRenderMenu();
        initAboutMenu();
    }

    private void initFileMenu() {
        addSubMenu("File", getExtendedKeyCodeForChar('f'));

        addMenuItem("File/New",
                "Open a new document",
                getExtendedKeyCodeForChar('n'),
                "new_file.png",
                this::newFileAction);

        addToolBarButton("File/New");

        addMenuItem("File/Open",
                "Open an existing document",
                getExtendedKeyCodeForChar('o'),
                "open_file.png",
                this::openFileAction);

        addToolBarButton("File/Open");

        addMenuItem("File/Save As",
                "Save current document",
                getExtendedKeyCodeForChar('s'),
                "save_as.png",
                this::saveFileAction);

        addToolBarButton("File/Save As");

        addMenuItem("File/Exit",
                "Quit the application; prompts to save document",
                getExtendedKeyCodeForChar('e'),
                "exit.png",
                this::exitAction);

        addToolBarSeparator();
    }

    private void initRenderMenu() {
        addSubMenu("Render", getExtendedKeyCodeForChar('r'));

        addMenuItem("Render/Render",
                "Render image",
                getExtendedKeyCodeForChar('r'),
                "render.png",
                this::renderAction);

        addToolBarButton("Render/Render");

        addMenuItem("Render/Debug",
                "Debug",
                getExtendedKeyCodeForChar('d'),
                "debug.png",
                this::debugAction);

        addToolBarButton("Render/Debug");

        addToolBarSeparator();
    }

    private void initAboutMenu() {
        addSubMenu("About", getExtendedKeyCodeForChar('a'));

        addMenuItem("About/About",
                "About author",
                getExtendedKeyCodeForChar('a'),
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

    private void renderAction() {
        final InitialRaysCreator raysCreator = new InitialRaysCreator(cameraTransformer, cameraParameters, imageSize);
        // TODO or <= ???
        final float[][][] light = new float[imageSize.height][imageSize.width][3];
        for (int x = 0; x < imageSize.width; ++x) {
            for (int y = 0; y < imageSize.height; ++y) {
                final Ray initialRay = raysCreator.createRayFromViewPortPixel(x, y);
                final List<Reflection> reflections = getRaytracing(initialRay, figures, lightSources, RAYTRACING_DEPTH);
                light[y][x] = LightComposer.composeLight(reflections, ambientLight);
            }
        }
        final Color[][] finalColors = LightComposer.normalizeLight(light, imageSize);

        final BufferedImage image = ImageUtils.createImage(imageSize, Color.WHITE);
        for (int x = 0; x < imageSize.width; ++x) {
            for (int y = 0; y < imageSize.height; ++y) {
                image.setRGB(x, y, finalColors[y][x].getRGB());
            }
        }
        viewPortLabel.setIcon(new ImageIcon(image));
        viewPortLabel.setSize(imageSize);
    }

    private void debugAction() {
        additionalLines.clear();
        final InitialRaysCreator raysCreator = new InitialRaysCreator(cameraTransformer, cameraParameters, imageSize);
        final Ray initialRay = raysCreator.createRayFromViewPortPixel(imageSize.width / 2, imageSize.height / 2);

        final List<DoubleLine> reflectedRays = getRaytracing(initialRay, figures, lightSources, 5)
                .stream()
                .map(Reflection::getReflectedRay)
                .map(Ray::getDirectionLine)
                .collect(Collectors.toList());
        additionalLines.add(Pair.of(reflectedRays, Color.BLUE));

        final List<DoubleLine> lightSourcesLines = lightSources.stream()
                .map(light -> Stream.of(
                        new DoubleLine(light.getPosition(), light.getPosition().plus(new DoublePoint3D(0, 0, 0.2))),
                        new DoubleLine(light.getPosition(), light.getPosition().plus(new DoublePoint3D(0, 0.2, 0))),
                        new DoubleLine(light.getPosition(), light.getPosition().plus(new DoublePoint3D(0.2, 0, 0)))
                ))
                .flatMap(Function.identity())
                .collect(Collectors.toList());
        additionalLines.add(Pair.of(lightSourcesLines, Color.GREEN));

        redrawAll();
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
