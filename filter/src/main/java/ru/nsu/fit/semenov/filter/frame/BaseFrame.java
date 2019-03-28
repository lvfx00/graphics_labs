package ru.nsu.fit.semenov.filter.frame;

import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class BaseFrame extends JFrame {
    public BaseFrame(int width, int height, String title) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        setLocationByPlatform(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onWindowClose(e);
            }
        });
        setSize(width, height);
        setTitle(title);
    }

    protected void onWindowClose(@Nullable WindowEvent e) {
        System.exit(0);
    }
}
