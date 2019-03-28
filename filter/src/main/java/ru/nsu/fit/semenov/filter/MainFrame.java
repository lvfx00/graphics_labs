package ru.nsu.fit.semenov.filter;

import org.jetbrains.annotations.Nullable;
import ru.nsu.fit.semenov.filter.frame.BaseMainFrame;
import ru.nsu.fit.semenov.filter.util.FileUtils;

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
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

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
    private BufferedImage scaledWithSelectionImage;
    private double scaleRatio;
    private MouseAdapter plainImageMouseAdapter;

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
    }

    private void setPlainImage(BufferedImage image) {
        plainImage = image;
        scaleRatio = getScaleRatio(
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

        // TODO move to select button
        selectAction();
    }

    private void handleMouseEvent(MouseEvent e) {
        int imageHeight = plainImage.getHeight();
        int imageWidth = plainImage.getWidth();
        int halfZone = ZONE_SIZE / 2;
        int originX = (int) (e.getX() * (1.0 / scaleRatio));
        int originY = (int) (e.getY() * (1.0 / scaleRatio));

        if (originX < halfZone) {
            originX = halfZone;
        }
        if (originX > imageWidth - halfZone) {
            originX = imageWidth - halfZone;
        }
        if (originY < halfZone) {
            originY = halfZone;
        }
        if (originY > imageHeight - halfZone) {
            originY = imageHeight - halfZone;
        }

        zoomedImage = plainImage.getSubimage(originX - halfZone, originY - halfZone, ZONE_SIZE, ZONE_SIZE);
        zoomedImageLabel.setIcon(new ImageIcon(zoomedImage));

        int scaledX = (int) ((double) originX * scaleRatio);
        int scaledY = (int) ((double) originY * scaleRatio);

        scaledWithSelectionImage = copyBufferedImage(scaledImage);
        Graphics2D graphics2D = scaledWithSelectionImage.createGraphics();
        graphics2D.drawRect(
                scaledX - (int) (halfZone * scaleRatio),
                scaledY - (int) (halfZone * scaleRatio),
                (int) (ZONE_SIZE * scaleRatio),
                (int) (ZONE_SIZE * scaleRatio)
        );
        graphics2D.dispose();
        plainImageLabel.setIcon(new ImageIcon(scaledWithSelectionImage));
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

    private void selectAction() {
        plainImageMouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMouseEvent(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseEvent(e);
            }
        };
        plainImageLabel.addMouseListener(plainImageMouseAdapter);
        plainImageLabel.addMouseMotionListener(plainImageMouseAdapter);
    }

    private void newFileAction() {
        // TODO
    }

    private void openFileAction() {
        final File file = FileUtils.getOpenFileName(this, "bmp", "Bitmap image files");
        if (file != null) {
            try {
                BufferedImage image = ImageIO.read(file);
                setPlainImage(image);
            } catch (IOException e) {
                // TODO show popup about invalid file
            }
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

    @Override
    protected void onWindowClose(@Nullable WindowEvent e) {
        exitAction();
    }

    private static double getScaleRatio(Dimension imgSize, Dimension boundary) {
        int originalWidth = imgSize.width;
        int originalHeight = imgSize.height;
        int boundWidth = boundary.width;
        int boundHeight = boundary.height;
        int newWidth = originalWidth;
        int newHeight = originalHeight;

        if (originalWidth > boundWidth) {
            newWidth = boundWidth;
            newHeight = (newWidth * originalHeight) / originalWidth;
        }
        if (newHeight > boundHeight) {
            newHeight = boundHeight;
            newWidth = (newHeight * originalWidth) / originalHeight;
        }
        return ((double) newWidth) / originalWidth;
    }

    private static BufferedImage copyBufferedImage(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
