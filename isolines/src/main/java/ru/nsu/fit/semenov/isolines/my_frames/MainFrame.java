package ru.nsu.fit.semenov.isolines.my_frames;

import org.jetbrains.annotations.Nullable;
import ru.nsu.fit.semenov.isolines.frame_utils.BaseMainFrame;
import ru.nsu.fit.semenov.isolines.model.BoundedFunction;
import ru.nsu.fit.semenov.isolines.model.BoundedFunctionImpl;
import ru.nsu.fit.semenov.isolines.model.BoundedFunctionImpl.DoubleBiFunction;
import ru.nsu.fit.semenov.isolines.model.IsolinesDrawer;
import ru.nsu.fit.semenov.isolines.utils.ImageUtils;
import ru.nsu.fit.semenov.isolines.utils.Rectangle;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

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
            new Color(255, 100, 0),
            Color.ORANGE,
            Color.YELLOW,
            Color.LIGHT_GRAY,
            Color.CYAN,
            new Color(0, 135, 255),
            new Color(0, 50, 255)
    };

    private static final Color additionalColor = Color.BLUE;
    private final Color[] interpolationColors = new Color[colors.length + 1];

    private static final DoubleBiFunction HARDCODED_FUNCTION = ((x, y) -> Math.sin(y) * Math.cos(x));

    private Dimension mapSize = INIT_MAP_SIZE;

    private BoundedFunction boundedFunction;
    private double a = -5.0;
    private double b = 5.0;
    private double c = -5.0;
    private double d = 5.0;
    private int k = 20;
    private int m = 20;

    private final List<ImageLayerData> imageLayerDataList = new ArrayList<>();

//    private final JLabel userIsolinesLabel = new JLabel();
//    private BufferedImage userIsolinesImage;

    private final JLabel statusLabel = new JLabel("status");
    private JLabel mapLegendLabel = new JLabel();

    public MainFrame() {
        super(FRAME_WIDTH, FRAME_HEIGHT, "Isolines");
        initMenus();

        getContentPane().setBackground(FRAME_BACKGOUND_COLOR);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setMinimumSize(MAP_MIN_SIZE);
        layeredPane.setPreferredSize(INIT_MAP_SIZE);

//        LayeredPaneMouseAdapter layeredPaneMouseAdapter = new LayeredPaneMouseAdapter();
//        layeredPane.addMouseListener(layeredPaneMouseAdapter);
//        layeredPane.addMouseMotionListener(layeredPaneMouseAdapter);

        for (ImageLayerDataIndex index : ImageLayerDataIndex.values()) {
            layeredPane.add(imageLayerDataList.get(index.ordinal()).getLabel(), new Integer(index.ordinal()));
        }

//        userIsolinesImage = ImageUtils.createOpaqueImage(mapSize.width, mapSize.height);
//        userIsolinesLabel.setIcon(new ImageIcon(userIsolinesImage));
//        layeredPane.add(userIsolinesLabel, new Integer(4));

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

        System.arraycopy(colors, 0, interpolationColors, 0, colors.length);
        interpolationColors[colors.length] = additionalColor;

        init();
    }

    private void drawMapLegend() {
        final boolean isInterpolatedMapShown = imageLayerDataList.get(ImageLayerDataIndex.INTERPOLATED_MAP.ordinal()).isShown();
        final boolean isMapShown = imageLayerDataList.get(ImageLayerDataIndex.MAP.ordinal()).isShown();
        if (!isInterpolatedMapShown && !isMapShown) {
            mapLegendLabel.setIcon(new ImageIcon(ImageUtils.createOpaqueImage(MAP_LEGEND_WIDTH, MAP_LEGEND_HEIGHT)));
            return;
        }

        double minValue = boundedFunction.getMinValue();
        double maxValue = boundedFunction.getMaxValue();
        BoundedFunction legendFuncliton = new BoundedFunctionImpl(
                (x, y) -> minValue + x * (maxValue - minValue), // -> x ???
                new Rectangle(0, 0, 1, 1)
        );
        BufferedImage image = ImageUtils.createOpaqueImage(MAP_LEGEND_WIDTH, MAP_LEGEND_HEIGHT);

        if (isInterpolatedMapShown) {
            IsolinesDrawer.drawInterpolatedMap(image, legendFuncliton, interpolationColors);
        } else {
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
        boundedFunction = new BoundedFunctionImpl(HARDCODED_FUNCTION, new Rectangle(a, c, b - a, d - c));
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
        addImageLayer(
                ImageLayerDataIndex.MAP,
                () -> IsolinesDrawer.drawMap(ImageUtils.createOpaqueImage(mapSize.width, mapSize.height), boundedFunction, colors),
                () -> {
                    drawMapLegend();
                    ImageLayerData data = imageLayerDataList.get(ImageLayerDataIndex.INTERPOLATED_MAP.ordinal());
                    if (data.isShown()) commonImageLayerAction(data);
                },
                submenu,
                "Draw map",
                "Map",
                'm',
                "map.png"
        );
        addImageLayer(
                ImageLayerDataIndex.INTERPOLATED_MAP,
                () -> IsolinesDrawer.drawInterpolatedMap(
                        ImageUtils.createOpaqueImage(mapSize.width, mapSize.height),
                        boundedFunction,
                        interpolationColors
                ),
                () -> {
                    drawMapLegend();
                    ImageLayerData data = imageLayerDataList.get(ImageLayerDataIndex.MAP.ordinal());
                    if (data.isShown()) commonImageLayerAction(data);
                },
                submenu,
                "Interpolation",
                "Draw interpolated map",
                'p',
                "interpolation.png"
        );
        addImageLayer(
                ImageLayerDataIndex.ISOLINES,
                () -> IsolinesDrawer.drawIsolines(
                        ImageUtils.createOpaqueImage(mapSize.width, mapSize.height),
                        boundedFunction,
                        k,
                        m,
                        calculateLevels(boundedFunction, colors.length - 1)
                ),
                null,
                submenu,
                "Isolines",
                "Draw isolines",
                'i',
                "isolines.png"
        );
        addImageLayer(
                ImageLayerDataIndex.GRID,
                () -> IsolinesDrawer.drawGrid(ImageUtils.createOpaqueImage(mapSize.width, mapSize.height), k, m),
                null,
                submenu,
                "Grid",
                "Draw grid",
                'g',
                "grid.png"
        );
        addImageLayer(
                ImageLayerDataIndex.CONTROL_POINTS,
                () -> IsolinesDrawer.drawControlPoints(
                        ImageUtils.createOpaqueImage(mapSize.width, mapSize.height),
                        boundedFunction,
                        k,
                        m,
                        calculateLevels(boundedFunction, colors.length - 1)
                ),
                null,
                submenu,
                "Control Points",
                "Draw control points",
                'c',
                "dots.png"
        );
//        String menuPathString = submenu + "/Erase";
//        addCheckBoxMenuItem(
//                menuPathString,
//                "Erase user isolines",
//                KeyEvent.getExtendedKeyCodeForChar('e'),
//                "erase.png",
//                this::eraseUserIsolinesAction
//        );
//        addToolBarToggleButton(menuPathString);
        addToolBarSeparator();
    }

    private void addImageLayer(
            ImageLayerDataIndex dataIndex,
            Supplier<BufferedImage> imageSupplier,
            @Nullable Runnable additionalAction,
            String submenu,
            String menuItem,
            String tooltip,
            char hotKey,
            String iconPath
    ) {
        ImageLayerData data = new ImageLayerData(imageSupplier);
        String menuPathString = submenu + "/" + menuItem;
        data.addButton(
                addCheckBoxMenuItem(
                        menuPathString,
                        tooltip,
                        KeyEvent.getExtendedKeyCodeForChar(hotKey),
                        iconPath,
                        () -> {
                            commonImageLayerAction(data);
                            if (additionalAction != null) {
                                additionalAction.run();
                            }
                        }
                )
        );
        data.addButton(addToolBarToggleButton(menuPathString));
        imageLayerDataList.add(dataIndex.ordinal(), data);
    }

    private void commonImageLayerAction(ImageLayerData data) {
        data.setShown(!data.isShown());
        for (AbstractButton button : data.getButtonsList()) {
            button.setSelected(data.isShown());
        }
        if (data.isShown()) {
            showImageLayerAction(data);
        } else {
            data.getLabel().setIcon(null);
        }
    }

    private void showImageLayerAction(ImageLayerData data) {
        data.getLabel().setIcon(new ImageIcon(data.createImage()));
        data.getLabel().setBounds(0, 0, mapSize.width, mapSize.height);
    }

    private void redrawImages() {
        for (ImageLayerDataIndex index : ImageLayerDataIndex.values()) {
            ImageLayerData data = imageLayerDataList.get(index.ordinal());
            if (data.isShown()) {
                showImageLayerAction(data);
            }
        }
    }

//    private void eraseUserIsolinesAction() {
//        userIsolinesImage = ImageUtils.createOpaqueImage(mapSize.width, mapSize.height);
//        userIsolinesLabel.setIcon(new ImageIcon(userIsolinesImage));
//    }

    private double[] calculateLevels(BoundedFunction function, int levelsNum) {
        final double[] levels = new double[levelsNum];
        final double step = (function.getMaxValue() - function.getMinValue()) / (levelsNum + 1);
        int count = 0;
        for (double level = function.getMinValue() + step; level < function.getMaxValue(); level += step) {
            levels[count++] = level;
        }
        return levels;
    }

//    private class LayeredPaneMouseAdapter extends MouseAdapter {
//        @Override
//        public void mousePressed(MouseEvent e) {
//            DoubleCoord coordsInD = getMouseCoordsInD(e.getX(), e.getY());
//            double level = boundedFunction.apply(coordsInD.getX(), coordsInD.getY());
//            IsolinesDrawer.drawIsolines(userIsolinesImage, boundedFunction, k, m, level);
//            userIsolinesLabel.setIcon(new ImageIcon(userIsolinesImage));
//
//        }
//
//        @Override
//        public void mouseDragged(MouseEvent e) {
//
//        }
//
//        @Override
//        public void mouseMoved(MouseEvent e) {
//            DoubleCoord coordsInD = getMouseCoordsInD(e.getX(), e.getY());
//            double level = boundedFunction.apply(coordsInD.getX(), coordsInD.getY());
//            statusLabel.setText("X = " + coordsInD.getX() +
//                    ",   Y = " + coordsInD.getY() +
//                    ",   Value = " + level
//            );
//        }
//
//        private DoubleCoord getMouseCoordsInD(int x, int y) {
//            final double pixelWidthInD = (boundedFunction.getMaxX() - boundedFunction.getMinX()) / mapSize.width;
//            final double pixelHeightInD = (boundedFunction.getMaxY() - boundedFunction.getMinY()) / mapSize.height;
//
//            final double xInD = x * pixelWidthInD + pixelWidthInD / 2 + boundedFunction.getMinX();
//            final double yInD = y * pixelHeightInD + pixelHeightInD / 2 + boundedFunction.getMinY();
//
//            return new DoubleCoord(xInD, yInD);
//        }
//    }

}
