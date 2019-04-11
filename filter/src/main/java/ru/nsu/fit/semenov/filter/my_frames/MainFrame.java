package ru.nsu.fit.semenov.filter.my_frames;

import org.jetbrains.annotations.Nullable;
import ru.nsu.fit.semenov.filter.algorithms.*;
import ru.nsu.fit.semenov.filter.frame.BaseMainFrame;
import ru.nsu.fit.semenov.filter.util.FileUtils;
import ru.nsu.fit.semenov.filter.util.ImageUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.JOptionPane.*;

public final class MainFrame extends BaseMainFrame {
    private static final Color FRAME_BACKGOUND_COLOR = Color.WHITE;
    private static final int ZONE_SIZE = 350;
    private static final int ZONES_GAP = 10;
    private static final Color ZONE_BORDER_COLOR = Color.BLACK;
    private static final int ZONE_BORDER_THICKNESS = 2;

    private BufferedImage plainImage;
    private BufferedImage zoomedImage;
    private BufferedImage modifiedImage;
    private JLabel plainImageLabel = createImageLabel();
    private JLabel zoomedImageLabel = createImageLabel();
    private JLabel modifiedImageLabel = createImageLabel();

    private BufferedImage scaledImage;
    private double scaleRatio;
    private MouseAdapter plainImageMouseAdapter = new SelectionMouseAdapter();

    private boolean isSelectionEnabled = false;
    private JToggleButton selectionButton;
    private JCheckBoxMenuItem selectionMenuItem;

    private boolean isFiltersEnabled = false;
    private List<AbstractButton> filtersButtons = new ArrayList<>();

    public MainFrame(int width, int height, String title) {
        super(width, height, title);
        getContentPane().setBackground(FRAME_BACKGOUND_COLOR);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        JPanel imagesViewPanel = new JPanel();
        imagesViewPanel.setLayout(new FlowLayout(FlowLayout.LEFT, ZONES_GAP, ZONES_GAP));
        imagesViewPanel.add(plainImageLabel);
        imagesViewPanel.add(zoomedImageLabel);
        imagesViewPanel.add(modifiedImageLabel);
        mainPanel.add(imagesViewPanel);

        JPanel chartsPanel = new JPanel();
        chartsPanel.setLayout(new FlowLayout());
        mainPanel.add(chartsPanel);

        JScrollPane scrollPane = new JScrollPane(
                mainPanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        add(scrollPane, BorderLayout.CENTER);

        initMenus();
        cleanup();
    }

    @Override
    protected void onWindowClose(@Nullable WindowEvent e) {
        exitAction();
    }

    private void setPlainImage(BufferedImage image) {
        plainImage = image;
        scaleRatio = ImageUtils.getScaleRatio(
                new Dimension(image.getWidth(), image.getHeight()),
                new Dimension(ZONE_SIZE, ZONE_SIZE)
        );
        BufferedImage afterTransform = new BufferedImage(
                plainImage.getWidth(),
                plainImage.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );
        AffineTransform at = new AffineTransform();
        at.scale(scaleRatio, scaleRatio);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        scaledImage = scaleOp.filter(image, afterTransform);
        plainImageLabel.setIcon(new ImageIcon(scaledImage));

        setSelectionEnabled(true);
    }


    private JLabel createImageLabel() {
        JLabel label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setVerticalAlignment(SwingConstants.TOP);
        Border border = BorderFactory.createLineBorder(ZONE_BORDER_COLOR, ZONE_BORDER_THICKNESS);
        Dimension size = new Dimension(ZONE_SIZE, ZONE_SIZE);
        label.setPreferredSize(size);
        label.setBorder(border);
        return label;
    }

    private void initMenus() {
        initFileMenu();
        initFiltersMenu();
    }

    private void initFileMenu() {
        addSubMenu("File", KeyEvent.getExtendedKeyCodeForChar('f'));

        addMenuItem("File/New",
                "Create a new document",
                KeyEvent.getExtendedKeyCodeForChar('n'),
                "new_file.png",
                this::newFileAction);

        addMenuItem("File/Open",
                "Open an existing document",
                KeyEvent.getExtendedKeyCodeForChar('o'),
                "open_file.png",
                this::openFileAction);

        addMenuItem("File/Save",
                "Save an active document",
                KeyEvent.getExtendedKeyCodeForChar('s'),
                "save.png",
                this::saveFileAction);

        addMenuItem("File/Save As",
                "Save an active document with a new name",
                KeyEvent.getExtendedKeyCodeForChar('a'),
                "save_as.png",
                this::saveAsFileAction);

        addMenuItem("File/Exit",
                "Quit the application; prompts to save document",
                KeyEvent.getExtendedKeyCodeForChar('e'),
                "exit.png",
                this::exitAction);

        addToolBarButton("File/New");
        addToolBarButton("File/Open");
        addToolBarButton("File/Save");

        addToolBarSeparator();
    }

    private void initFiltersMenu() {
        String submenu = "Filters";
        addSubMenu(submenu, KeyEvent.getExtendedKeyCodeForChar('f'));

        selectionMenuItem = addCheckBoxMenuItem(
                "Filters/Select",
                "Select zone of specified size",
                KeyEvent.getExtendedKeyCodeForChar('s'),
                "select.png",
                this::selectAction
        );
        selectionButton = addToolBarToggleButton("Filters/Select");

        String menuPathString = submenu + "/Greyscale";
        filtersButtons.add(
                addMenuItem(
                        menuPathString,
                        "Greyscale",
                        KeyEvent.getExtendedKeyCodeForChar('g'),
                        "greyscale.png",
                        this::greyscaleAction
                )
        );
        filtersButtons.add(addToolBarButton(menuPathString));

        menuPathString = submenu + "/Negative";
        filtersButtons.add(
                addMenuItem(
                        menuPathString,
                        "Negative",
                        KeyEvent.getExtendedKeyCodeForChar('n'),
                        "negative.png",
                        this::negativeAction
                )
        );
        filtersButtons.add(addToolBarButton(menuPathString));

        menuPathString = submenu + "/Ordered Dithering";
        filtersButtons.add(
                addMenuItem(
                        menuPathString,
                        "Ordering Dithering",
                        KeyEvent.getExtendedKeyCodeForChar('o'),
                        "ordered_dithering.png",
                        this::orderedDitheringAction
                )
        );
        filtersButtons.add(addToolBarButton(menuPathString));

        menuPathString = submenu + "/Error Diffusion";
        filtersButtons.add(
                addMenuItem(
                        menuPathString,
                        "Error Diffusion",
                        KeyEvent.getExtendedKeyCodeForChar('e'),
                        "error_diffusion.png",
                        this::errorDifussionAction
                )
        );
        filtersButtons.add(addToolBarButton(menuPathString));

        menuPathString = submenu + "/Differentiating Filter";
        filtersButtons.add(
                addMenuItem(
                        menuPathString,
                        "Differentiating Filter",
                        KeyEvent.getExtendedKeyCodeForChar('d'),
                        "differentiating_filter.png",
                        this::differentiatingFilterAction
                )
        );
        filtersButtons.add(addToolBarButton(menuPathString));

        addToolBarSeparator();
    }

    private void applyAlgorithm(Algorithm algorithm) {
        modifiedImage = algorithm.apply(zoomedImage);
        modifiedImageLabel.setIcon(new ImageIcon(modifiedImage));
    }

    private void negativeAction() {
        applyAlgorithm(new NegativeAlgorithm());
    }

    private void greyscaleAction() {
        applyAlgorithm(new GreyscaleAlgorithm());
    }

    private void orderedDitheringAction() {
        applyAlgorithm(new OrderedDitheringAlgorithm());
    }

    private void errorDifussionAction() {
        startNewFrame(
                new ErrorDiffusionSettingsFrame(
                        this,
                        result -> applyAlgorithm(
                                new ErrorDiffusionAlgorithm(
                                        result.getRedPaletteSize(),
                                        result.getGreenPaletteSize(),
                                        result.getBluePaletteSize()
                                )
                        )
                )
        );
    }

    private void differentiatingFilterAction() {
        startNewFrame(
                new DiffFilterSettingsFrame(
                        this,
                        result -> applyAlgorithm(new DifferentiatingFilterAlgorithm(result.getLimit(), result.getType()))
                )
        );
    }

    private void selectAction() {
        setSelectionSelected(!isSelectionEnabled);
    }

    private void newFileAction() {
        cleanup();
    }

    private void openFileAction() {
        final File file = FileUtils.getOpenFileName(this, "bmp", "Bitmap image files");
        if (file != null) {
            BufferedImage image;
            try {
                image = ImageIO.read(file);
            } catch (IOException e) {
                // TODO show popup about invalid file
                return;
            }
            cleanup();
            setPlainImage(image);
        }
    }

    private void saveFileAction() {
        // TODO
    }

    private void saveAsFileAction() {
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

    private void cleanup() {
        plainImageLabel.setIcon(null);
        zoomedImageLabel.setIcon(null);
        modifiedImageLabel.setIcon(null);
        setSelectionSelected(false);
        setSelectionEnabled(false);
        setFiltersEnabled(false);
    }

    private void setFiltersEnabled(boolean value) {
        isFiltersEnabled = value;
        for (AbstractButton button : filtersButtons) {
            button.setEnabled(value);
        }
    }

    private void setSelectionEnabled(boolean value) {
        selectionMenuItem.setEnabled(value);
        selectionButton.setEnabled(value);
    }

    private void setSelectionSelected(boolean value) {
        isSelectionEnabled = value;
        selectionMenuItem.setSelected(value);
        selectionButton.setSelected(value);
        if (value) {
            plainImageLabel.addMouseListener(plainImageMouseAdapter);
            plainImageLabel.addMouseMotionListener(plainImageMouseAdapter);
        } else {
            plainImageLabel.removeMouseListener(plainImageMouseAdapter);
            plainImageLabel.removeMouseMotionListener(plainImageMouseAdapter);
        }
    }

    private class SelectionMouseAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if (!isFiltersEnabled) {
                setFiltersEnabled(true);
            }

            handleSelectionMouseEvent(e);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            handleSelectionMouseEvent(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            plainImageLabel.setIcon(new ImageIcon(scaledImage));
        }

        private void handleSelectionMouseEvent(MouseEvent e) {
            int imageHeight = plainImage.getHeight();
            int imageWidth = plainImage.getWidth();
            int halfZone = ZONE_SIZE / 2;
            int originClickX = (int) (e.getX() * (1.0 / scaleRatio));
            int originClickY = (int) (e.getY() * (1.0 / scaleRatio));

            if (originClickX < halfZone) {
                originClickX = halfZone;
            }
            if (originClickX > imageWidth - halfZone) {
                originClickX = imageWidth - halfZone;
            }
            if (originClickY < halfZone) {
                originClickY = halfZone;
            }
            if (originClickY > imageHeight - halfZone) {
                originClickY = imageHeight - halfZone;
            }

            int zoomedPartBeginX; // upper-left corner of zoomed part
            int zoomedPartBeginY;
            int zoomedPartWidth; // selection rectangle size
            int zoomedPartHeight;

            if (plainImage.getWidth() < ZONE_SIZE) {
                zoomedPartBeginX = 0;
                zoomedPartWidth = plainImage.getWidth();
            } else {
                zoomedPartBeginX = originClickX - halfZone;
                zoomedPartWidth = ZONE_SIZE;
            }

            if (plainImage.getHeight() < ZONE_SIZE) {
                zoomedPartBeginY = 0;
                zoomedPartHeight = plainImage.getHeight();
            } else {
                zoomedPartBeginY = originClickY - halfZone;
                zoomedPartHeight = ZONE_SIZE;
            }

            zoomedImage = plainImage.getSubimage(
                    zoomedPartBeginX,
                    zoomedPartBeginY,
                    zoomedPartWidth,
                    zoomedPartHeight
            );
            zoomedImageLabel.setIcon(new ImageIcon(zoomedImage));

            BufferedImage scaledWithSelectionImage = ImageUtils.copyBufferedImage(scaledImage);
            Graphics2D graphics2D = scaledWithSelectionImage.createGraphics();
            graphics2D.drawRect(
                    (int) (zoomedPartBeginX * scaleRatio),
                    (int) (zoomedPartBeginY * scaleRatio),
                    (int) (zoomedPartWidth * scaleRatio),
                    (int) (zoomedPartHeight * scaleRatio)
            );
            graphics2D.dispose();
            plainImageLabel.setIcon(new ImageIcon(scaledWithSelectionImage));
        }

    }

}
