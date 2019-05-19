package ru.nsu.fit.g16205.semenov.wireframe;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.wireframe.camera.CameraPosition;
import ru.nsu.fit.g16205.semenov.wireframe.camera.PyramidOfView;
import ru.nsu.fit.g16205.semenov.wireframe.frame_utils.BaseMainFrame;
import ru.nsu.fit.g16205.semenov.wireframe.figure.FigureData;
import ru.nsu.fit.g16205.semenov.wireframe.figure.FigureEditFrame;
import ru.nsu.fit.g16205.semenov.wireframe.model.geometric.DoublePoint3D;
import ru.nsu.fit.g16205.semenov.wireframe.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends BaseMainFrame {

    private static final Dimension MIN_FRAME_SIZE = new Dimension(800, 600);
    private static final Dimension INIT_FRAME_SIZE = new Dimension(800, 600);

    private @NotNull List<FigureData> figures = new ArrayList<>();


    private @NotNull PyramidOfView pyramidOfView = new PyramidOfView(5, 5, 2, 10);
    private @NotNull CameraPosition cameraPosition = new CameraPosition(
            new DoublePoint3D(-10, 0, 0),
            new DoublePoint3D(0, 0, 0),
            new DoublePoint3D(0, 1, 0)
    );
    private final JLabel viewPortLabel = new JLabel();
    private BufferedImage viewPortImage;

    public MainFrame() {
        super(INIT_FRAME_SIZE.width, INIT_FRAME_SIZE.height, "Wireframe");
        initMenus();

        setMinimumSize(MIN_FRAME_SIZE);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));

        viewPortImage = ImageUtils.createImage(new Dimension(300, 300), Color.CYAN);
        viewPortLabel.setIcon(new ImageIcon(viewPortImage));
        viewPortLabel.setSize(new Dimension(300, 300));

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.add(viewPortLabel, 0);
//        layeredPane.addComponentListener(new LayeredPaneComponentListener());
//        MouseAdapter layeredPaneMouseAdapter = new LayeredPaneMouseAdapter();
//        layeredPane.addMouseListener(layeredPaneMouseAdapter);
//        layeredPane.addMouseMotionListener(layeredPaneMouseAdapter);
//        return layeredPane;
//        addComponentListener(new ComponentAdapter() {
//            @Override
//            public void componentResized(ComponentEvent e) {
//                layeredPane.setPreferredSize(new Dimension(e.getComponent().getWidth(), e.getComponent().getHeight()));
//            }
//        });
        mainPanel.add(layeredPane);
        add(mainPanel);
    }

    private void initMenus() {
        initFileMenu();
        initEditMenu();
    }

    private void initFileMenu() {
        addSubMenu("File", KeyEvent.getExtendedKeyCodeForChar('f'));

        addMenuItem("File/Open",
                "Open an existing document",
                KeyEvent.getExtendedKeyCodeForChar('o'),
                "open_file.png",
                this::openFileAction);

        addMenuItem("File/Exit",
                "Quit the application; prompts to save document",
                KeyEvent.getExtendedKeyCodeForChar('e'),
                "exit.png",
                this::exitAction);

        addToolBarButton("File/Open");

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

        addToolBarSeparator();
    }

    private void openFileAction() {
    }

    private void exitAction() {
    }

    private void addFigureAction() {
        startNewFrame(new FigureEditFrame(this, null, figureData -> {
            figures.add(figureData);
            drawFigure(figureData);
        }));
    }

    private void editFigureAction() {
        // TODO
    }

    private void drawFigure(@NotNull FigureData figureData) {
        ProjectionCreator.drawProjection(viewPortImage, figureData, cameraPosition, pyramidOfView);
    }

}
