package ru.nsu.fit.semenov.isolines.my_frames;

import org.jetbrains.annotations.Nullable;
import ru.nsu.fit.semenov.isolines.frame_utils.BaseMainFrame;
import ru.nsu.fit.semenov.isolines.model.BoundedFunction;
import ru.nsu.fit.semenov.isolines.model.BoundedFunctionImpl;
import ru.nsu.fit.semenov.isolines.model.BoundedFunctionImpl.DoubleBiFunction;
import ru.nsu.fit.semenov.isolines.model.IsolinesDrawer;
import ru.nsu.fit.semenov.isolines.utils.CoordsTransformer;
import ru.nsu.fit.semenov.isolines.utils.DoubleCoord;
import ru.nsu.fit.semenov.isolines.utils.ImageUtils;
import ru.nsu.fit.semenov.isolines.utils.Rectangle;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class MainFrame extends BaseMainFrame {

    private static final Color FRAME_BACKGOUND_COLOR = Color.WHITE;
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;
    private static final Dimension MAP_MIN_SIZE = new Dimension(100, 100);
    private static final Dimension INIT_MAP_SIZE = new Dimension(500, 500);
    private static final Dimension MIN_FRAME_SIZE = new Dimension(500, 500);

    private static final int MAP_LEGEND_HEIGHT = 80;

    private static final Color additionalColor = Color.BLUE;

    private static final DoubleBiFunction HARDCODED_FUNCTION = ((x, y) -> Math.sin(y) * Math.cos(x));

    private final Color[] colors = {
            Color.RED,
            new Color(255, 100, 0),
            Color.ORANGE,
            Color.YELLOW,
            Color.GREEN,
            Color.CYAN,
            new Color(0, 135, 255),
            new Color(0, 50, 255)
    };

    private final Color[] interpolationColors = new Color[colors.length + 1];

    private Dimension mapSize = INIT_MAP_SIZE;

    private BoundedFunction boundedFunction;
    private double a = -5.0;
    private double b = 5.0;
    private double c = -5.0;
    private double d = 5.0;
    private int k = 20;
    private int m = 20;

    private final LayeredPaneMouseAdapter layeredPaneMouseAdapter = new LayeredPaneMouseAdapter();
    private final List<ImageLayerData> imageLayerDataList = new ArrayList<>();

    private final List<AbstractButton> interpolationButtons = new ArrayList<>(2);
    private boolean isInterpolationSelected = false;

    private final List<Double> userIsolinesLevels = new ArrayList<>();
    private final JLabel userIsolinesLabel = new JLabel();

    private final JLabel statusLabel = new JLabel("status");
    private JLabel mapLegendLabel = new JLabel();

    public MainFrame() {
        super(FRAME_WIDTH, FRAME_HEIGHT, "Isolines");
        setMinimumSize(MIN_FRAME_SIZE);
        initMenus();

        getContentPane().setBackground(FRAME_BACKGOUND_COLOR);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setMinimumSize(MAP_MIN_SIZE);
        layeredPane.setPreferredSize(INIT_MAP_SIZE);

        LayeredPaneMouseAdapter layeredPaneMouseAdapter = new LayeredPaneMouseAdapter();
        layeredPane.addMouseListener(layeredPaneMouseAdapter);
        layeredPane.addMouseMotionListener(layeredPaneMouseAdapter);

        for (ImageLayerDataIndex index : ImageLayerDataIndex.values()) {
            layeredPane.add(imageLayerDataList.get(index.ordinal()).getLabel(), new Integer(index.ordinal()));
        }
        layeredPane.add(userIsolinesLabel, new Integer(ImageLayerDataIndex.ISOLINES.ordinal()));

        layeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                mapSize = new Dimension(e.getComponent().getWidth(), e.getComponent().getHeight());
                resizeImages();
                drawMapLegend();
            }
        });

        mainPanel.add(layeredPane);

        JPanel mapLegendPanel = new JPanel();
        mapLegendPanel.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Map Legend")
        );
        mapLegendPanel.add(mapLegendLabel);
        mapLegendLabel.setHorizontalAlignment(SwingConstants.LEFT);
        mainPanel.add(mapLegendPanel);

        add(mainPanel, BorderLayout.CENTER);
        add(initStatusPanel(), BorderLayout.SOUTH);

        System.arraycopy(colors, 0, interpolationColors, 0, colors.length);
        interpolationColors[colors.length] = additionalColor;

        init();
    }

    private void drawMapLegend() {
        int mapLegendWidth = mapSize.width - 40;
        if (!imageLayerDataList.get(ImageLayerDataIndex.MAP.ordinal()).isShown()) {
            mapLegendLabel.setIcon(new ImageIcon(ImageUtils.createOpaqueImage(mapLegendWidth, MAP_LEGEND_HEIGHT * 3 / 4)));
            return;
        }

        BufferedImage legendImage = ImageUtils.createOpaqueImage(mapLegendWidth, MAP_LEGEND_HEIGHT / 2);
        BoundedFunction legendFuncliton = new BoundedFunctionImpl(
                (x, y) -> x,
                new Rectangle(0, 0, 1, 1)
        );

        if (isInterpolationSelected) {
            IsolinesDrawer.drawInterpolatedMap(legendImage, legendFuncliton, interpolationColors);
        } else {
            IsolinesDrawer.drawMap(legendImage, legendFuncliton, colors);
        }

        BufferedImage concatImage = ImageUtils.concatImagesVertically(
                legendImage,
                ImageUtils.createOpaqueImage(mapLegendWidth, MAP_LEGEND_HEIGHT / 4)
        );

        Graphics2D graphics2D = concatImage.createGraphics();
        graphics2D.setFont(new Font("TimesRoman", Font.BOLD, 15));
        graphics2D.setStroke(new BasicStroke(2));
        graphics2D.setColor(Color.BLACK);

        for (double level : calculateLevels(boundedFunction, colors.length - 1)) {
            int width = (int) (mapLegendWidth * (level - boundedFunction.getMinValue()) /
                    (boundedFunction.getMaxValue() - boundedFunction.getMinValue()));
            graphics2D.drawLine(width, 0, width, legendImage.getHeight() - 1);
            graphics2D.drawString(
                    String.format("%.2f", level),
                    width - 15,
                    MAP_LEGEND_HEIGHT / 2 + 20
            );
        }
        graphics2D.dispose();
        mapLegendLabel.setIcon(new ImageIcon(concatImage));
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
        initAboutMenu();
    }

    private void init() {
        boundedFunction = new BoundedFunctionImpl(HARDCODED_FUNCTION, new Rectangle(a, c, b - a, d - c));
        resizeImages();
        drawMapLegend();
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
                () -> {
                    if (isInterpolationSelected) {
                        return IsolinesDrawer.drawInterpolatedMap(
                                ImageUtils.createOpaqueImage(mapSize.width, mapSize.height),
                                boundedFunction,
                                interpolationColors
                        );
                    } else {
                        return IsolinesDrawer.drawMap(
                                ImageUtils.createOpaqueImage(mapSize.width, mapSize.height),
                                boundedFunction,
                                colors
                        );
                    }
                },
                this::drawMapLegend,
                submenu,
                "Draw map",
                "Map",
                'm',
                "map.png"
        );

        String menuPathString = submenu + "/Interpolation";
        interpolationButtons.add(
                addCheckBoxMenuItem(
                        menuPathString,
                        "enable interpolation map mode",
                        KeyEvent.getExtendedKeyCodeForChar('i'),
                        "interpolation.png",
                        this::interpolationAction
                )
        );
        interpolationButtons.add(addToolBarToggleButton(menuPathString));

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

        menuPathString = submenu + "/Erase";
        addMenuItem(
                menuPathString,
                "Erase user isolines",
                KeyEvent.getExtendedKeyCodeForChar('e'),
                "erase.png",
                this::eraseUserIsolinesAction
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

    private void resizeImages() {
        for (ImageLayerDataIndex index : ImageLayerDataIndex.values()) {
            ImageLayerData data = imageLayerDataList.get(index.ordinal());
            if (data.isShown()) {
                showImageLayerAction(data);
            }
        }
        if (!userIsolinesLevels.isEmpty()) {
            drawUserIsolines();
        }
    }

    private static List<Double> calculateLevels(BoundedFunction function, int levelsNum) {
        final double[] levels = new double[levelsNum];
        final double step = (function.getMaxValue() - function.getMinValue()) / (levelsNum + 1);
        int count = 0;
        for (double level = function.getMinValue() + step; level < function.getMaxValue(); level += step) {
            levels[count++] = level;
        }
        return Arrays.stream(levels).boxed().collect(Collectors.toList());
    }

    private void interpolationAction() {
        isInterpolationSelected = !isInterpolationSelected;
        for (AbstractButton button : interpolationButtons) {
            button.setSelected(isInterpolationSelected);
        }
        ImageLayerData mapLayerData = imageLayerDataList.get(ImageLayerDataIndex.MAP.ordinal());
        if (mapLayerData.isShown()) {
            showImageLayerAction(mapLayerData);
            drawMapLegend();
        }
    }

    private void drawUserIsolines() {
        userIsolinesLabel.setIcon(new ImageIcon(
                IsolinesDrawer.drawIsolines(
                        ImageUtils.createOpaqueImage(mapSize.width, mapSize.height),
                        boundedFunction,
                        k,
                        m,
                        userIsolinesLevels
                )
        ));
        userIsolinesLabel.setBounds(0, 0, mapSize.width, mapSize.height);
    }

    private void eraseUserIsolinesAction() {
        userIsolinesLevels.clear();
        userIsolinesLabel.setIcon(null);
    }

    private class LayeredPaneMouseAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            addUserIsolineByCoords(e.getX(), e.getY());
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            userIsolinesLevels.remove(userIsolinesLevels.size() - 1);
            addUserIsolineByCoords(e.getX(), e.getY());
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            DoubleCoord coordsInD = getCoordsPixel(e.getX(), e.getY());
            double level = boundedFunction.apply(coordsInD.getX(), coordsInD.getY());
            statusLabel.setText("X = " + coordsInD.getX() +
                    ",   Y = " + coordsInD.getY() +
                    ",   F(X, Y) = " + level
            );
        }

        private void addUserIsolineByCoords(int x, int y) {
            DoubleCoord coordsInD = getCoordsPixel(x, y);
            userIsolinesLevels.add(boundedFunction.apply(coordsInD.getX(), coordsInD.getY()));
            drawUserIsolines();
        }

        private DoubleCoord getCoordsPixel(int x, int y) {
            CoordsTransformer transformer = new CoordsTransformer(
                    mapSize.width,
                    mapSize.height,
                    boundedFunction.getDomain()
            );
            return transformer.getCoordsByPixel(x, y);
        }
    }

}
