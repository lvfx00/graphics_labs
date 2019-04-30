package ru.nsu.fit.semenov.isolines;

import ru.nsu.fit.semenov.isolines.frameutils.BaseMainFrame;
import ru.nsu.fit.semenov.isolines.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public final class MainFrame extends BaseMainFrame {

    private static final Color FRAME_BACKGOUND_COLOR = Color.WHITE;
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;
    private static final int ISOLINES_FRAME_WIDTH = 500;
    private static final int ISOLINES_FRAME_HEIGHT = 500;

    private final Color[] colors = {
            Color.ORANGE,
            Color.GRAY,
            Color.CYAN,
            Color.GREEN
    };
    private final BoundedFunction boundedFunction = new MyBoundedFunction();

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
        layeredPane.setPreferredSize(new Dimension(ISOLINES_FRAME_WIDTH + 20, ISOLINES_FRAME_HEIGHT + 20));
        layeredPane.add(mapLabel, new Integer(0));
        layeredPane.add(gridLabel, new Integer(1));
        layeredPane.add(isolinesLabel, new Integer(2));

        mainPanel.add(layeredPane);

//        JPanel chartsPanel = new JPanel();
//        chartsPanel.setLayout(new FlowLayout());
//        mainPanel.add(chartsPanel);

        JScrollPane scrollPane = new JScrollPane(
                mainPanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        add(scrollPane, BorderLayout.CENTER);

        initMenus();
        cleanup();
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
                    boundedFunction,
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
                    boundedFunction,
                    20,
                    20,
                    -0.5
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
                    20,
                    20
            )));
        }
        isGridShown = !isGridShown;
        for (AbstractButton ab : gridButtonsList) {
            ab.setSelected(isGridShown);
        }
    }

}
