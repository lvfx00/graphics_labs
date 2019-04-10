package ru.nsu.fit.semenov.filter.frame;

import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class BaseFrame extends JFrame {
    private final BaseFrame intentionFrame;

    public BaseFrame(int width, int height, String title, @Nullable BaseFrame intentionFrame) {
        this.intentionFrame = intentionFrame;
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        setLocationByPlatform(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (intentionFrame != null) {
                    intentionFrame.onFocusChanged(true);
                }
                onWindowClose(e);
            }
        });
        setSize(width, height);
        setTitle(title);
    }

    public BaseFrame(int width, int height, String title) {
        this(width, height, title, null);
    }

    protected void onWindowClose(@Nullable WindowEvent e) {
        dispose();
    }

    public void onFocusChanged(boolean focused) {
        if (focused) {
            setEnabled(true);
            setVisible(true);
        } else {
            setEnabled(false);
        }
    }

    public void startNewFrame(BaseFrame frame) {
        onFocusChanged(false);
        frame.onFocusChanged(true);
    }

}
