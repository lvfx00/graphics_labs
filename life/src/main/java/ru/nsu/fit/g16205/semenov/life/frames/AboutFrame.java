package ru.nsu.fit.g16205.semenov.life.frames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AboutFrame extends JFrame {
    private static final int WIDTH = 300;
    private static final int HEIGHT = 300;

    private final MyFrame myFrame;

    AboutFrame(MyFrame myFrame) {
        this.myFrame = myFrame;

        setSize(WIDTH, HEIGHT);
        setResizable(false);

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                backToMainFrame();
            }
        });

        setLocationByPlatform(true);
        setTitle("About");


        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> backToMainFrame());

        ImageIcon imageIcon = new ImageIcon(ClassLoader.getSystemClassLoader().getResource("me.png"), "");
        Image img = imageIcon.getImage();
        Image scaledImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        imageIcon.setImage(scaledImg);

        JTextArea aboutTextArea = new JTextArea("Aвтор: Семенов Сергей \ngithub.com/lvfx00\n НГУ, ФИТ, группа 16205\n Задача life\n Версия 1.0\n Copyright(C) 2019");
        aboutTextArea.setEditable(false);
        aboutTextArea.setFont(new Font(Font.DIALOG, Font.BOLD, 15));

        add(new JLabel(imageIcon));
        add(aboutTextArea);
        add(okButton);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
    }

    private void backToMainFrame() {
        dispose();
        myFrame.setEnabled(true);
        myFrame.setVisible(true);
    }
}
