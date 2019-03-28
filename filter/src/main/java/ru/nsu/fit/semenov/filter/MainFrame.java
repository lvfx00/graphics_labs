package ru.nsu.fit.semenov.filter;

import org.jetbrains.annotations.Nullable;
import ru.nsu.fit.semenov.filter.frame.BaseMainFrame;
import ru.nsu.fit.semenov.filter.util.FileUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static javax.imageio.ImageIO.read;
import static javax.swing.JOptionPane.*;

public final class MainFrame extends BaseMainFrame {
    private BufferedImage image;

    public MainFrame(int width, int height, String title) {
        super(width, height, title);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        JPanel imagesViewPanel = new JPanel();
        imagesViewPanel.setLayout(new FlowLayout());
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

    private void newFileAction() {
        // TODO
    }

    private void openFileAction() {
        final File file = FileUtils.getOpenFileName(this, "txt", "Text files");
        if (file == null) {
        }
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
        }
        // TODO
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
}
