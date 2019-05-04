package ru.nsu.fit.semenov.isolines.utils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

public class FileUtils {
    /**
     * Prompts user for file name to save and returns it
     *
     * @param parent      - parent frame for file selection dialog
     * @param extension   - preferred file extension (example: "txt")
     * @param description - description of specified file type (example: "Text files")
     * @return File specified by user or null if user canceled operation
     */
    public static File getSaveFileName(JFrame parent, String extension, String description) {
        JFileChooser fileChooser = new JFileChooser();
        FileFilter filter = new ExtensionFileFilter(extension, description);
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setCurrentDirectory(new File("."));
        if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            if (!f.getName().contains("."))
                f = new File(f.getParent(), f.getName() + "." + extension);
            return f;
        }
        return null;
    }

    /**
     * Prompts user for file name to open and returns it
     *
     * @param parent      - parent frame for file selection dialog
     * @param extension   - preferred file extension (example: "txt")
     * @param description - description of specified file type (example: "Text files")
     * @return File specified by user or null if user canceled operation
     */
    public static File getOpenFileName(JFrame parent, String extension, String description) {
        JFileChooser fileChooser = new JFileChooser();
        FileFilter filter = new ExtensionFileFilter(extension, description);
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setCurrentDirectory(new File("."));
        if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            if (!f.getName().contains("."))
                f = new File(f.getParent(), f.getName() + "." + extension);
            return f;
        }
        return null;
    }

    private static class ExtensionFileFilter extends FileFilter {
        private String extension, description;

        /**
         * Constructs filter
         *
         * @param extension   - extension (without point), for example, "txt"
         * @param description - file type description, for example, "Text files"
         */
        public ExtensionFileFilter(String extension, String description) {
            this.extension = extension;
            this.description = description;
        }

        @Override
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().toLowerCase().endsWith("." + extension.toLowerCase());
        }

        @Override
        public String getDescription() {
            return description + " (*." + extension + ")";
        }
    }

}
