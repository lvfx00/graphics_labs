package ru.nsu.fit.semenov.isolines;

import ru.nsu.fit.semenov.isolines.BoundedFunctionImpl.DoubleBiFunction;
import ru.nsu.fit.semenov.isolines.frame_utils.BaseMainFrame;
import ru.nsu.fit.semenov.isolines.utils.DoubleCoord;
import ru.nsu.fit.semenov.isolines.utils.ImageUtils;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public final class MainFrame extends BaseMainFrame {

    private static final Color FRAME_BACKGOUND_COLOR = Color.WHITE;
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;
    private static final Dimension MAP_MIN_SIZE = new Dimension(100, 100);
    private static final Dimension INIT_MAP_SIZE = new Dimension(500, 500);

    private static final int MAP_LEGEND_WIDTH = 300;
    private static final int MAP_LEGEND_HEIGHT = 50;

    private final Color[] colors = {
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.PINK,
            Color.LIGHT_GRAY,
            Color.CYAN,
            new Color(0, 135, 255),
            new Color(0, 50, 255)
    };

    private final Color additionalColor = Color.BLUE;
    private final Color[] interpolationColors = new Color[colors.length + 1];

    private static final DoubleBiFunction HARDCODED_FUNCTION = ((x, y) -> Math.sin(y) * Math.cos(x));
    private static final double minValue = -1.0;
    private static final double maxValue = 1.0;

    private Dimension mapSize = INIT_MAP_SIZE;
    private double a = -5.0;
    private double b = 5.0;
    private double c = -5.0;
    private double d = 5.0;
    private int k = 20;
    private int m = 20;
    private BoundedFunction boundedFunction =
            new BoundedFunctionImpl(HARDCODED_FUNCTION, a, b, c, d, -1.0, 1.0);

    private final JLabel isolinesLabel = new JLabel();
    private final List<AbstractButton> isolinesButtonsList = new ArrayList<>(2);
    private boolean isIsolinesShown;

    private final JLabel mapLabel = new JLabel();
    private final List<AbstractButton> mapButtonsList = new ArrayList<>(2);
    private boolean isMapShown;

    private final JLabel interpolatedMapLabel = new JLabel();
    private final List<AbstractButton> interpolationButtonsList = new ArrayList<>(2);
    private boolean isInterpolatedMapShown;

    private final JLabel gridLabel = new JLabel();
    private final List<AbstractButton> gridButtonsList = new ArrayList<>(2);
    private boolean isGridShown;

    private final JLabel controlPointsLabel = new JLabel();
    private final List<AbstractButton> controlPointsButtonsList = new ArrayList<>(2);
    private boolean isControlPointsShown;

    private final JLabel userIsolinesLabel = new JLabel();
    private BufferedImage userIsolinesImage;

    private final JLabel statusLabel = new JLabel("status");
    private JLabel mapLegendLabel = new JLabel();

    public MainFrame() {
        super(FRAME_WIDTH, FRAME_HEIGHT, "Isolines");
        getContentPane().setBackground(FRAME_BACKGOUND_COLOR);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setMinimumSize(MAP_MIN_SIZE);
        layeredPane.setPreferredSize(INIT_MAP_SIZE);

        LayeredPaneMouseAdapter layeredPaneMouseAdapter = new LayeredPaneMouseAdapter();
        layeredPane.addMouseListener(layeredPaneMouseAdapter);
        layeredPane.addMouseMotionListener(layeredPaneMouseAdapter);

        layeredPane.add(mapLabel, new Integer(0));
        layeredPane.add(interpolatedMapLabel, new Integer(0));
        layeredPane.add(gridLabel, new Integer(1));
        layeredPane.add(isolinesLabel, new Integer(2));
        layeredPane.add(controlPointsLabel, new Integer(3));

        userIsolinesImage = ImageUtils.createOpaqueImage(mapSize.width, mapSize.height);
        userIsolinesLabel.setIcon(new ImageIcon(userIsolinesImage));
        layeredPane.add(userIsolinesLabel, new Integer(4));

        layeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                mapSize = new Dimension(e.getComponent().getWidth(), e.getComponent().getHeight());
                redrawImages();
            }
        });

        mainPanel.add(layeredPane);

        JPanel mapLegendPanel = new JPanel();
        mapLegendPanel.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Map Legend")
        );
        mapLegendPanel.add(mapLegendLabel);
        mapLegendLabel.setHorizontalAlignment(SwingConstants.LEFT);
        drawMapLegend();
        mainPanel.add(mapLegendPanel);

        add(mainPanel, BorderLayout.CENTER);
        add(initStatusPanel(), BorderLayout.SOUTH);

        initMenus();

        System.arraycopy(colors, 0, interpolationColors, 0, colors.length);
        interpolationColors[colors.length] = additionalColor;
    }

    private void drawMapLegend() {
        if (!isInterpolatedMapShown && !isMapShown) {
            mapLegendLabel.setIcon(new ImageIcon(ImageUtils.createOpaqueImage(MAP_LEGEND_WIDTH, MAP_LEGEND_HEIGHT)));
            return;
        }

        double len = 1;
        BoundedFunction legendFuncliton = new BoundedFunctionImpl(
                (x, y) -> minValue + x * (maxValue - minValue) / len,
                0, len,
                0, len,
                minValue, maxValue
        );
        BufferedImage image = ImageUtils.createOpaqueImage(MAP_LEGEND_WIDTH, MAP_LEGEND_HEIGHT);

        if (isInterpolatedMapShown) {
            IsolinesDrawer.drawInterpolatedMap(image, legendFuncliton, interpolationColors);
        } else if (isMapShown) {
            IsolinesDrawer.drawMap(image, legendFuncliton, colors);
        }

//        graphics2D.setFont(new Font("TimesRoman", Font.BOLD, 16));
//                graphics2D.drawString(
//                        String.valueOf(step * i),
//                        xLegendOffset + i * oneColorWidth - 15,
//                        MAP_LEGEND_HEIGHT + yLegendOffset + 15
//                );
        mapLegendLabel.setIcon(new ImageIcon(image));

    }

    private JPanel initStatusPanel() {
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusPanel.setPreferredSize(new Dimension(getWidth(), 16));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(statusLabel);
        return statusPanel;
    }

    private void initMenus() {
        initActionMenu();
        initEditMenu();
    }

    private void init() {
        boundedFunction = new BoundedFunctionImpl(HARDCODED_FUNCTION, a, b, c, d, minValue, maxValue);
        redrawImages();
    }

    private void initEditMenu() {
        String submenu = "Edit";
        addSubMenu(submenu, KeyEvent.getExtendedKeyCodeForChar('e'));

        String menuPathString = submenu + "/Preferences";
        addMenuItem(
                menuPathString,
                "Preferences",
                KeyEvent.getExtendedKeyCodeForChar('p'),
                "preferences.png",
                this::preferencesAction
        );
        addToolBarButton(menuPathString);

        addToolBarSeparator();
    }

    private void preferencesAction() {
        startNewFrame(new PreferencesFrame(
                this,
                preferencesValues -> {
                    a = preferencesValues.getA();
                    b = preferencesValues.getB();
                    c = preferencesValues.getC();
                    d = preferencesValues.getD();
                    k = preferencesValues.getK();
                    m = preferencesValues.getM();
                    init();
                },
                new PreferencesFrame.PreferencesValues(a, b, c, d, k, m)
        ));
    }

    private void initActionMenu() {
        String submenu = "Action";
        addSubMenu(submenu, KeyEvent.getExtendedKeyCodeForChar('a'));

        String menuPathString = submenu + "/Map";
        mapButtonsList.add(
                addCheckBoxMenuItem(
                        menuPathString,
                        "Map",
                        KeyEvent.getExtendedKeyCodeForChar('m'),
                        "map.png",
                        this::mapAction
                )
        );
        mapButtonsList.add(addToolBarToggleButton(menuPathString));

        menuPathString = submenu + "/Interpolation";
        interpolationButtonsList.add(
                addCheckBoxMenuItem(
                        menuPathString,
                        "Interpolated map",
                        KeyEvent.getExtendedKeyCodeForChar('p'),
                        "interpolation.png",
                        this::interpolationAction
                )
        );
        interpolationButtonsList.add(addToolBarToggleButton(menuPathString));

        menuPathString = submenu + "/Isolines";
        isolinesButtonsList.add(
                addCheckBoxMenuItem(
                        menuPathString,
                        "Isolines",
                        KeyEvent.getExtendedKeyCodeForChar('i'),
                        "isolines.png",
                        this::isolinesAction
                )
        );
        isolinesButtonsList.add(addToolBarToggleButton(menuPathString));

        menuPathString = submenu + "/Grid";
        gridButtonsList.add(
                addCheckBoxMenuItem(
                        menuPathString,
                        "Grid",
                        KeyEvent.getExtendedKeyCodeForChar('g'),
                        "grid.png",
                        this::gridAction
                )
        );
        gridButtonsList.add(addToolBarToggleButton(menuPathString));

        menuPathString = submenu + "/Control Points";
        controlPointsButtonsList.add(
                addCheckBoxMenuItem(
                        menuPathString,
                        "View isoline control points",
                        KeyEvent.getExtendedKeyCodeForChar('c'),
                        "dots.png",
                        this::controlPointsAction
                )
        );
        controlPointsButtonsList.add(addToolBarToggleButton(menuPathString));

        menuPathString = submenu + "/Erase";
        addCheckBoxMenuItem(
                menuPathString,
                "Erase user isolines",
                KeyEvent.getExtendedKeyCodeForChar('e'),
                "erase.png",
                this::eraseUserIsolinesAction
        );
        addToolBarToggleButton(menuPathString);

        addToolBarSeparator();
    }

    private void mapAction() {
        isMapShown = !isMapShown;
        for (AbstractButton ab : mapButtonsList) {
            ab.setSelected(isMapShown);
        }
        drawMapLegend();
        if (!isMapShown) {
            mapLabel.setIcon(null);
        } else {
            if (isInterpolatedMapShown) {
                interpolationAction();
            }
            drawMap();
        }
    }

    private void drawMap() {
        mapLabel.setIcon(new ImageIcon(IsolinesDrawer.drawMap(
                ImageUtils.createOpaqueImage(mapSize.width, mapSize.height),
                boundedFunction,
                colors
        )));
        mapLabel.setBounds(0, 0, mapSize.width, mapSize.height);
    }

    private void interpolationAction() {
        isInterpolatedMapShown = !isInterpolatedMapShown;
        for (AbstractButton ab : interpolationButtonsList) {
            ab.setSelected(isInterpolatedMapShown);
        }
        drawMapLegend();
        if (!isInterpolatedMapShown) {
            interpolatedMapLabel.setIcon(null);
        } else {
            if (isMapShown) {
                mapAction();
            }
            drawInterpolatedMap();
        }
    }

    private void drawInterpolatedMap() {
        interpolatedMapLabel.setIcon(new ImageIcon(IsolinesDrawer.drawInterpolatedMap(
                ImageUtils.createOpaqueImage(mapSize.width, mapSize.height),
                boundedFunction,
                interpolationColors
        )));
        interpolatedMapLabel.setBounds(0, 0, mapSize.width, mapSize.height);
    }

    private void isolinesAction() {
        isIsolinesShown = !isIsolinesShown;
        for (AbstractButton ab : isolinesButtonsList) {
            ab.setSelected(isIsolinesShown);
        }
        if (!isIsolinesShown) {
            isolinesLabel.setIcon(null);
        } else {
            drawIsolines();
        }
    }

    private void drawIsolines() {
        isolinesLabel.setIcon(new ImageIcon(IsolinesDrawer.drawIsolines(
                ImageUtils.createOpaqueImage(mapSize.width, mapSize.height),
                boundedFunction,
                k,
                m,
                calculateLevels(boundedFunction, colors.length - 1)
        )));
        isolinesLabel.setBounds(0, 0, mapSize.width, mapSize.height);
    }

    private void gridAction() {
        isGridShown = !isGridShown;
        for (AbstractButton ab : gridButtonsList) {
            ab.setSelected(isGridShown);
        }
        if (!isGridShown) {
            gridLabel.setIcon(null);
        } else {
            drawGrid();
        }
    }

    private void drawGrid() {
        gridLabel.setIcon(new ImageIcon(IsolinesDrawer.drawGrid(
                ImageUtils.createOpaqueImage(mapSize.width, mapSize.height),
                k,
                m
        )));
        gridLabel.setBounds(0, 0, mapSize.width, mapSize.height);
    }

    private void controlPointsAction() {
        isControlPointsShown = !isControlPointsShown;
        for (AbstractButton ab : controlPointsButtonsList) {
            ab.setSelected(isControlPointsShown);
        }
        if (!isControlPointsShown) {
            controlPointsLabel.setIcon(null);
        } else {
            drawControlPoints();
        }
    }

    private void drawControlPoints() {
        controlPointsLabel.setIcon(new ImageIcon(IsolinesDrawer.drawControlPoints(
                ImageUtils.createOpaqueImage(mapSize.width, mapSize.height),
                boundedFunction,
                k,
                m,
                calculateLevels(boundedFunction, colors.length - 1)
        )));
        controlPointsLabel.setBounds(0, 0, mapSize.width, mapSize.height);
    }

    private void redrawImages() {
        if (isGridShown) {
            drawGrid();
        }
        if (isIsolinesShown) {
            drawIsolines();
        }
        if (isMapShown) {
            drawMap();
        }
        if (isControlPointsShown) {
            drawControlPoints();
        }
        if (isInterpolatedMapShown) {
            drawInterpolatedMap();
        }
    }

    private void eraseUserIsolinesAction() {
        userIsolinesImage = ImageUtils.createOpaqueImage(mapSize.width, mapSize.height);
        userIsolinesLabel.setIcon(new ImageIcon(userIsolinesImage));
    }

    private double[] calculateLevels(BoundedFunction function, int levelsNum) {
        final double[] levels = new double[levelsNum];
        final double step = (function.getMaxValue() - function.getMinValue()) / (levelsNum + 1);
        int count = 0;
        for (double level = function.getMinValue() + step; level < function.getMaxValue(); level += step) {
            levels[count++] = level;
        }
        return levels;
    }

    private class LayeredPaneMouseAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            DoubleCoord coordsInD = getMouseCoordsInD(e.getX(), e.getY());
            double level = boundedFunction.apply(coordsInD.getX(), coordsInD.getY());
            IsolinesDrawer.drawIsolines(userIsolinesImage, boundedFunction, k, m, level);
            userIsolinesLabel.setIcon(new ImageIcon(userIsolinesImage));

        }

        @Override
        public void mouseDragged(MouseEvent e) {

        }

        @Override
        public void mouseMoved(MouseEvent e) {
            DoubleCoord coordsInD = getMouseCoordsInD(e.getX(), e.getY());
            double level = boundedFunction.apply(coordsInD.getX(), coordsInD.getY());
            statusLabel.setText("X = " + coordsInD.getX() +
                    ",   Y = " + coordsInD.getY() +
                    ",   Value = " + level
            );
        }

        private DoubleCoord getMouseCoordsInD(int x, int y) {
            final double pixelWidthInD = (boundedFunction.getMaxX() - boundedFunction.getMinX()) / mapSize.width;
            final double pixelHeightInD = (boundedFunction.getMaxY() - boundedFunction.getMinY()) / mapSize.height;

            final double xInD = x * pixelWidthInD + pixelWidthInD / 2 + boundedFunction.getMinX();
            final double yInD = y * pixelHeightInD + pixelHeightInD / 2 + boundedFunction.getMinY();

            return new DoubleCoord(xInD, yInD);
        }
    }

}
