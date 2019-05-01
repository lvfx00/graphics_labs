package ru.nsu.fit.semenov.isolines;

import ru.nsu.fit.semenov.isolines.BoundedFunctionImpl.DoubleBiFunction;
import ru.nsu.fit.semenov.isolines.frame_utils.BaseMainFrame;
import ru.nsu.fit.semenov.isolines.utils.Coord;
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
    private static final Dimension MAP_INIT_SIZE = new Dimension(500, 500);

    private static final int MAP_LEGEND_WIDTH = 400;
    private static final int MAP_LEGEND_HEIGHT = 30;

    private final Color[] colors = {
            Color.ORANGE,
            Color.YELLOW,
            Color.PINK,
            Color.GRAY,
            Color.CYAN,
            Color.GREEN,
            Color.MAGENTA,
            Color.BLUE
    };

    private static final DoubleBiFunction HARDCODED_FUNCTION = ((x, y) -> Math.sin(x) * Math.cos(y));

    private Dimension mapSize = MAP_INIT_SIZE;
    private double a = -3.0;
    private double b = 3.0;
    private double c = -3.0;
    private double d = 3.0;
    private int k = 10;
    private int m = 10;
    private BoundedFunction boundedFunction =
            new BoundedFunctionImpl(HARDCODED_FUNCTION, a, b, c, d, -1.0, 1.0);

    // --- ДАЛЬШЕ ХУЙНЯ --- //

    private final JLabel isolinesLabel = new JLabel();
    private final List<AbstractButton> isolinesButtonsList = new ArrayList<>(2);
    private boolean isIsolinesShown;

    private final JLabel mapLabel = new JLabel();
    private final List<AbstractButton> mapButtonsList = new ArrayList<>(2);
    private boolean isMapShown;

    private final JLabel gridLabel = new JLabel();
    private final List<AbstractButton> gridButtonsList = new ArrayList<>(2);
    private boolean isGridShown;

    private final JLabel userIsolinesLabel = new JLabel();
    private BufferedImage userIsolinesImage = ImageUtils.createOpaqueImage(mapSize.width, mapSize.height);

    private final JLabel statusLabel = new JLabel("status");

    public MainFrame() {
        super(FRAME_WIDTH, FRAME_HEIGHT, "Isolines");
        getContentPane().setBackground(FRAME_BACKGOUND_COLOR);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setMinimumSize(MAP_MIN_SIZE);
        layeredPane.setPreferredSize(MAP_INIT_SIZE);

        LayeredPaneMouseAdapter layeredPaneMouseAdapter = new LayeredPaneMouseAdapter();
        layeredPane.addMouseListener(layeredPaneMouseAdapter);
        layeredPane.addMouseMotionListener(layeredPaneMouseAdapter);

        layeredPane.add(mapLabel, new Integer(0));
        layeredPane.add(gridLabel, new Integer(1));
        layeredPane.add(isolinesLabel, new Integer(2));
        userIsolinesLabel.setIcon(new ImageIcon(userIsolinesImage));
        layeredPane.add(userIsolinesLabel, new Integer(3));

        layeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                mapSize = new Dimension(e.getComponent().getWidth(), e.getComponent().getHeight());
                redrawImages();
            }
        });

        mainPanel.add(layeredPane);
        mainPanel.add(initMapLegend());

        add(mainPanel, BorderLayout.CENTER);
        add(initStatusPanel(), BorderLayout.SOUTH);

        initMenus();
    }

    // TODO переделать на вызов рисования мапы
    private JPanel initMapLegend() {
        final double step = (boundedFunction.getMaxValue() - boundedFunction.getMinValue()) / colors.length;

        JPanel mapLegendPanel = new JPanel();

        BufferedImage image = ImageUtils.createOpaqueImage(MAP_LEGEND_WIDTH + 20, MAP_LEGEND_HEIGHT + 50);

        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setStroke(new BasicStroke(2));
        graphics2D.setColor(Color.BLACK);
        graphics2D.setFont(new Font("TimesRoman", Font.BOLD, 16));

        final int xLegendOffset = 10;
        final int yLegendOffset = 10;
        final int oneColorWidth = MAP_LEGEND_WIDTH / colors.length;

        graphics2D.drawRect(xLegendOffset, yLegendOffset, MAP_LEGEND_WIDTH, MAP_LEGEND_HEIGHT);

        for (int i = 0; i < colors.length; ++i) {
            graphics2D.setColor(colors[i]);
            graphics2D.fillRect(xLegendOffset + i * oneColorWidth, yLegendOffset, oneColorWidth, MAP_LEGEND_HEIGHT);
            graphics2D.setColor(Color.BLACK);
            graphics2D.drawLine(
                    xLegendOffset + i * oneColorWidth,
                    yLegendOffset,
                    xLegendOffset + i * oneColorWidth,
                    yLegendOffset + MAP_LEGEND_HEIGHT
            );
            if (i > 0) {
                graphics2D.drawString(
                        String.valueOf(step * i),
                        xLegendOffset + i * oneColorWidth - 15,
                        MAP_LEGEND_HEIGHT + yLegendOffset + 15
                );
            }
        }

        graphics2D.dispose();
        JLabel mapLegendLabel = new JLabel();
        mapLegendLabel.setIcon(new ImageIcon(image));
        mapLegendPanel.add(mapLegendLabel);
        return mapLegendPanel;
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
        // TODO расчитывать -1.0 и 1.0
        boundedFunction = new BoundedFunctionImpl(HARDCODED_FUNCTION, a, b, c, d, -1.0, 1.0);
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
        if (isMapShown) {
            mapLabel.setIcon(null);
        } else {
            drawMap();
        }
        isMapShown = !isMapShown;
        for (AbstractButton ab : mapButtonsList) {
            ab.setSelected(isMapShown);
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

    private void isolinesAction() {
        if (isIsolinesShown) {
            isolinesLabel.setIcon(null);
        } else {
            drawIsolines();
        }
        isIsolinesShown = !isIsolinesShown;
        for (AbstractButton ab : isolinesButtonsList) {
            ab.setSelected(isIsolinesShown);
        }
    }

    private void drawIsolines() {
        isolinesLabel.setIcon(new ImageIcon(IsolinesDrawer.drawIsolines(
                ImageUtils.createOpaqueImage(mapSize.width, mapSize.height),
                boundedFunction,
                k,
                m,
                colors.length
        )));
        isolinesLabel.setBounds(0, 0, mapSize.width, mapSize.height);
    }

    private void gridAction() {
        if (isGridShown) {
            gridLabel.setIcon(null);
        } else {
            drawGrid();
        }
        isGridShown = !isGridShown;
        for (AbstractButton ab : gridButtonsList) {
            ab.setSelected(isGridShown);
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
    }

    private void eraseUserIsolinesAction() {
        userIsolinesImage = ImageUtils.createOpaqueImage(mapSize.width, mapSize.height);
        userIsolinesLabel.setIcon(new ImageIcon(userIsolinesImage));
    }

    private class LayeredPaneMouseAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            Coord coordsInD = getMouseCoordsInD(e.getX(), e.getY());
            double level = boundedFunction.apply(coordsInD.getX(), coordsInD.getY());
            System.out.println(level);
            IsolinesDrawer.drawIsolines(userIsolinesImage, boundedFunction, k, m, level);
        }

        @Override
        public void mouseDragged(MouseEvent e) {

        }

        @Override
        public void mouseMoved(MouseEvent e) {
            Coord coordsInD = getMouseCoordsInD(e.getX(), e.getY());
            double level = boundedFunction.apply(coordsInD.getX(), coordsInD.getY());
            statusLabel.setText("X = " + coordsInD.getX() +
                    ",   Y = " + coordsInD.getY() +
                    ",   Value = " + level
            );
        }

        private Coord getMouseCoordsInD(int x, int y) {
            final double pixelWidthInD = (boundedFunction.getMaxX() - boundedFunction.getMinX()) / mapSize.width;
            final double pixelHeightInD = (boundedFunction.getMaxY() - boundedFunction.getMinY()) / mapSize.height;

            final double xInD = x * pixelWidthInD + pixelWidthInD / 2 + boundedFunction.getMinX();
            final double yInD = y * pixelHeightInD + pixelHeightInD / 2 + boundedFunction.getMinY();

            return new Coord(xInD, yInD);
        }
    }

}
