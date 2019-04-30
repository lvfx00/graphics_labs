package ru.nsu.fit.semenov.isolines;

import ru.nsu.fit.semenov.isolines.frameutils.BaseMainFrame;
import ru.nsu.fit.semenov.isolines.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public final class MainFrame extends BaseMainFrame {

    private static final Color FRAME_BACKGOUND_COLOR = Color.WHITE;
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;
    private static final int ISOLINES_FRAME_WIDTH = 500;
    private static final int ISOLINES_FRAME_HEIGHT = 500;

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
    private final BoundedFunction function = new MyBoundedFunction();
    private static final int GRID_WIDTH = 20;
    private static final int GRID_HEIGHT = 20;

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


    public MainFrame() {
        super(FRAME_WIDTH, FRAME_HEIGHT, "Isolines");
        getContentPane().setBackground(FRAME_BACKGOUND_COLOR);

        mapLabel.setBounds(0, 0, ISOLINES_FRAME_WIDTH, ISOLINES_FRAME_HEIGHT);
        gridLabel.setBounds(0, 0, ISOLINES_FRAME_WIDTH, ISOLINES_FRAME_HEIGHT);
        isolinesLabel.setBounds(0, 0, ISOLINES_FRAME_WIDTH, ISOLINES_FRAME_HEIGHT);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(ISOLINES_FRAME_WIDTH, ISOLINES_FRAME_HEIGHT));
        layeredPane.add(mapLabel, new Integer(0));
        layeredPane.add(gridLabel, new Integer(1));
        layeredPane.add(isolinesLabel, new Integer(2));

        mainPanel.add(layeredPane);
        mainPanel.add(initMapLegend());

        JScrollPane scrollPane = new JScrollPane(
                mainPanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        add(scrollPane, BorderLayout.CENTER);

        initMenus();
        cleanup();
    }

    private JPanel initMapLegend() {
        final double step = (function.getMaxValue() - function.getMinValue()) / colors.length;

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

    private void initMenus() {
        initActionMenu();
    }

    private void cleanup() {

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

        addToolBarSeparator();
    }

    private void mapAction() {
        if (isMapShown) {
            mapLabel.setIcon(null);
        } else {
            mapLabel.setIcon(new ImageIcon(IsolinesDrawer.drawMap(
                    ImageUtils.createOpaqueImage(ISOLINES_FRAME_WIDTH, ISOLINES_FRAME_HEIGHT),
                    function,
                    colors
            )));
        }
        isMapShown = !isMapShown;
        for (AbstractButton ab : mapButtonsList) {
            ab.setSelected(isMapShown);
        }
    }

    private void isolinesAction() {
        if (isIsolinesShown) {
            isolinesLabel.setIcon(null);
        } else {
            isolinesLabel.setIcon(new ImageIcon(IsolinesDrawer.drawIsolines(
                    ImageUtils.createOpaqueImage(ISOLINES_FRAME_WIDTH, ISOLINES_FRAME_HEIGHT),
                    function,
                    GRID_WIDTH,
                    GRID_HEIGHT,
                    colors.length
            )));
        }
        isIsolinesShown = !isIsolinesShown;
        for (AbstractButton ab : isolinesButtonsList) {
            ab.setSelected(isIsolinesShown);
        }
    }

    private void gridAction() {
        if (isGridShown) {
            gridLabel.setIcon(null);
        } else {
            gridLabel.setIcon(new ImageIcon(IsolinesDrawer.drawGrid(
                    ImageUtils.createOpaqueImage(ISOLINES_FRAME_WIDTH, ISOLINES_FRAME_HEIGHT),
                    GRID_WIDTH,
                    GRID_HEIGHT
            )));
        }
        isGridShown = !isGridShown;
        for (AbstractButton ab : gridButtonsList) {
            ab.setSelected(isGridShown);
        }
    }

}
