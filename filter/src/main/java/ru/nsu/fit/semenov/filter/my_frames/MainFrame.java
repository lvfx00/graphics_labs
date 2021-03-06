package ru.nsu.fit.semenov.filter.my_frames;

import org.jetbrains.annotations.Nullable;
import ru.nsu.fit.semenov.filter.algorithms.*;
import ru.nsu.fit.semenov.filter.filters.*;
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

import static java.lang.Math.PI;
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

    private List<AbstractButton> thirdFrameButtons = new ArrayList<>();

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
        initActionMenu();
        initFiltersMenu();
        initAboutMenu();
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

        thirdFrameButtons.add(
                addMenuItem("File/Save As",
                "Save an active document with a new name",
                KeyEvent.getExtendedKeyCodeForChar('a'),
                "save_as.png",
                        this::saveAsFileAction)
        );

        addMenuItem("File/Exit",
                "Quit the application; prompts to save document",
                KeyEvent.getExtendedKeyCodeForChar('e'),
                "exit.png",
                this::exitAction);

        addToolBarButton("File/New");
        addToolBarButton("File/Open");
        thirdFrameButtons.add(addToolBarButton("File/Save As"));

        addToolBarSeparator();
    }

    private void initActionMenu() {
        addSubMenu("Action", KeyEvent.getExtendedKeyCodeForChar('a'));

        selectionMenuItem = addCheckBoxMenuItem(
                "Action/Select",
                "Select zone of specified size",
                KeyEvent.getExtendedKeyCodeForChar('s'),
                "select.png",
                this::selectAction
        );
        selectionButton = addToolBarToggleButton("Action/Select");

        thirdFrameButtons.add(
                addMenuItem("Action/Copy to second",
                        "Copy image in third frame to second",
                        KeyEvent.getExtendedKeyCodeForChar('c'),
                        "copy_to_second.png",
                        this::copyToSecondAction)
        );
        thirdFrameButtons.add(addToolBarButton("Action/Copy to second"));

        addToolBarSeparator();
    }

    private void initFiltersMenu() {
        String submenu = "Filters";
        addSubMenu(submenu, KeyEvent.getExtendedKeyCodeForChar('f'));

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

        menuPathString = submenu + "/Image Doubling";
        filtersButtons.add(
                addMenuItem(
                        menuPathString,
                        "Image Doubling",
                        KeyEvent.getExtendedKeyCodeForChar('i'),
                        "image_doubling.png",
                        this::imageDoublingAction
                )
        );
        filtersButtons.add(addToolBarButton(menuPathString));

        menuPathString = submenu + "/Blur";
        filtersButtons.add(
                addMenuItem(
                        menuPathString,
                        "Blur",
                        KeyEvent.getExtendedKeyCodeForChar('b'),
                        "blur.png",
                        this::blurAction
                )
        );
        filtersButtons.add(addToolBarButton(menuPathString));

        menuPathString = submenu + "/Sharpness";
        filtersButtons.add(
                addMenuItem(
                        menuPathString,
                        "Sharpness",
                        KeyEvent.getExtendedKeyCodeForChar('s'),
                        "sharpness.png",
                        this::sharpnessAction
                )
        );
        filtersButtons.add(addToolBarButton(menuPathString));

        menuPathString = submenu + "/Stamping";
        filtersButtons.add(
                addMenuItem(
                        menuPathString,
                        "Stamping",
                        KeyEvent.getExtendedKeyCodeForChar('p'),
                        "stamping.png",
                        this::stampingAction
                )
        );
        filtersButtons.add(addToolBarButton(menuPathString));

        menuPathString = submenu + "/Watercolor";
        filtersButtons.add(
                addMenuItem(
                        menuPathString,
                        "Watercolor",
                        KeyEvent.getExtendedKeyCodeForChar('w'),
                        "watercolor.png",
                        this::watercolorAction
                )
        );
        filtersButtons.add(addToolBarButton(menuPathString));

        menuPathString = submenu + "/Image Rotation";
        filtersButtons.add(
                addMenuItem(
                        menuPathString,
                        "Image Rotation",
                        KeyEvent.getExtendedKeyCodeForChar('r'),
                        "image_rotation.png",
                        this::imageRotationAction
                )
        );
        filtersButtons.add(addToolBarButton(menuPathString));

        menuPathString = submenu + "/Gamma Correction";
        filtersButtons.add(
                addMenuItem(
                        menuPathString,
                        "Gamma Correction",
                        KeyEvent.getExtendedKeyCodeForChar('m'),
                        "gamma_correction.png",
                        this::gammaCorrectionAction
                )
        );
        filtersButtons.add(addToolBarButton(menuPathString));

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

    private void applyAlgorithm(Algorithm algorithm) {
        modifiedImage = algorithm.apply(zoomedImage);
        modifiedImageLabel.setIcon(new ImageIcon(modifiedImage));
    }

    private void applyAlgorithms(List<Algorithm> algorithms) {
        modifiedImage = algorithms.remove(0).apply(zoomedImage);
        for (Algorithm algorithm : algorithms) {
            modifiedImage = algorithm.apply(modifiedImage);
        }
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
                        result -> {
                            List<Algorithm> algorithms = new ArrayList<>();
                            algorithms.add(new GreyscaleAlgorithm());
                            switch (result.getType()) {
                                case ROBERTS_OPERATOR:
                                    algorithms.add(new FilterAlgorithm(new RobertsOperator(result.getLimit())));
                                    break;
                                case SOBEL_OPERATOR:
                                    algorithms.add(new FilterAlgorithm(new SobelOperator(result.getLimit())));
                                    break;
                                case BORDER_SELECTION:
                                    algorithms.add(new FilterAlgorithm(new BorderSelectionFilter(result.getLimit())));
                                    break;
                            }
                            applyAlgorithms(algorithms);
                        }
                )
        );
    }

    private void imageDoublingAction() {
        applyAlgorithm(new BilinearInterpolationAlgorithm(2.0));
    }

    private void blurAction() {
        applyAlgorithm(new FilterAlgorithm(new BlurFilter()));
    }

    private void sharpnessAction() {
        applyAlgorithm(new FilterAlgorithm(new SharpnessFilter()));
    }

    private void stampingAction() {
        applyAlgorithm(new FilterAlgorithm(new StampingFilter()));
    }

    private void watercolorAction() {
        List<Algorithm> algorithms = new ArrayList<>();
        algorithms.add(new FilterAlgorithm(new WatercolorFilter()));
        algorithms.add(new FilterAlgorithm(new SharpnessFilter()));
        applyAlgorithms(algorithms);
    }

    private void imageRotationAction() {
        startNewFrame(
                new RotationSettingsFrame(
                        this,
                        result -> applyAlgorithm(
                                new RotationAlgorithm((double) result.getRotationAngle() * PI / 180)
                        )
                )
        );
    }

    private void gammaCorrectionAction() {
        startNewFrame(
                new GammaSettingsFrame(
                        this,
                        result -> applyAlgorithm(new GammaCorrectionAlgorithm(result.getGamma()))
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
                showMessageDialog(null, "I/O error happened :(");
                System.err.println(e.getMessage());
                return;
            }
            cleanup();
            setPlainImage(image);
        }
    }

    private void saveAsFileAction() {
        final File file = FileUtils.getSaveFileName(this, "bmp", "Bitmap image files");
        if (file != null && modifiedImage != null) {
            try {
                ImageIO.write(modifiedImage, "png", file);
            } catch (IOException e) {
                showMessageDialog(null, "I/O error happened :(");
                System.err.println(e.getMessage());
            }
        }
    }

    private void exitAction() {
        int result = JOptionPane.showConfirmDialog(null, "Do you want to save current document?");
        switch (result) {
            case CANCEL_OPTION:
                break;
            case OK_OPTION:
                saveAsFileAction();
                System.exit(0);
                break;
            case NO_OPTION:
                System.exit(0);
                break;
            default:
                throw new AssertionError("Invalid option specified");
        }
    }

    private void copyToSecondAction() {
        zoomedImage = modifiedImage;
        zoomedImageLabel.setIcon(new ImageIcon(zoomedImage));
    }

    private void aboutAction() {
        startNewFrame(new AboutFrame(this));
    }

    private void cleanup() {
        plainImageLabel.setIcon(null);
        zoomedImageLabel.setIcon(null);
        modifiedImageLabel.setIcon(null);
        setSelectionSelected(false);
        setSelectionEnabled(false);
        setThirdFrameButtonsEnabled(false);
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

    private void setThirdFrameButtonsEnabled(boolean value) {
        for (AbstractButton button : thirdFrameButtons) {
            button.setEnabled(value);
        }
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
            for (AbstractButton button : filtersButtons) {
                button.addActionListener(e1 -> setThirdFrameButtonsEnabled(true));
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
