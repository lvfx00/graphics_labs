package ru.nsu.fit.semenov.isolines;

import ru.nsu.fit.semenov.isolines.frameutils.BaseMainFrame;

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
    private static final int ISOLINES_FRAME_WIDTH = 1200;
    private static final int ISOLINES_FRAME_HEIGHT = 800;

    private JLabel isolinesLabel = new JLabel();
    private BufferedImage isolinesImage =
            new BufferedImage(ISOLINES_FRAME_WIDTH, ISOLINES_FRAME_HEIGHT, BufferedImage.TYPE_INT_ARGB);

    private final IsolinesDrawer isolinesDrawer;


    public MainFrame() {
        super(FRAME_WIDTH, FRAME_HEIGHT, "Isolines");
        getContentPane().setBackground(FRAME_BACKGOUND_COLOR);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        JPanel isolinesPanel = new JPanel();
        isolinesPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        isolinesPanel.add(isolinesLabel);
        mainPanel.add(isolinesPanel);

        JPanel chartsPanel = new JPanel();
        chartsPanel.setLayout(new FlowLayout());
        mainPanel.add(chartsPanel);

        JScrollPane scrollPane = new JScrollPane(
                mainPanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        add(scrollPane, BorderLayout.CENTER);

        isolinesLabel.setIcon(new ImageIcon());

        final List<Color> colors = new ArrayList<>();
        colors.add(Color.ORANGE);
        colors.add(Color.GRAY);
        colors.add(Color.CYAN);
        colors.add(Color.GREEN);

        isolinesDrawer = new IsolinesDrawer(
                colors,
                new MyFunction(),
                -3,
                3,
                -3,
                3
        );

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
        addMenuItem(
                menuPathString,
                "Map",
                KeyEvent.getExtendedKeyCodeForChar('m'),
                "map.png",
                this::mapAction
        );
        addToolBarButton(menuPathString);

        menuPathString = submenu + "/Isolines";
        addMenuItem(
                menuPathString,
                "Isolines",
                KeyEvent.getExtendedKeyCodeForChar('i'),
                "isolines.png",
                this::isolinesAction
        );
        addToolBarButton(menuPathString);

        menuPathString = submenu + "/Grid";
        addMenuItem(
                menuPathString,
                "Grid",
                KeyEvent.getExtendedKeyCodeForChar('g'),
                "grid.png",
                this::gridAction
        );
        addToolBarButton(menuPathString);

        addToolBarSeparator();
    }

    private void mapAction() {
        isolinesImage = isolinesDrawer.drawMap(isolinesImage);
        isolinesLabel.setIcon(new ImageIcon(isolinesImage));
    }

    private void isolinesAction() {
        isolinesImage = isolinesDrawer.drawIsolines(isolinesImage, 20, 20, -0.5);
        isolinesLabel.setIcon(new ImageIcon(isolinesImage));
    }

    private void gridAction() {
        isolinesImage = isolinesDrawer.drawGrid(isolinesImage, 20, 20);
        isolinesLabel.setIcon(new ImageIcon(isolinesImage));
    }

}
