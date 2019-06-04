package ru.nsu.fit.g16205.semenov.raytracing.my_frames;

import ru.nsu.fit.g16205.semenov.raytracing.frame_utils.BaseFrame;

import javax.swing.*;
import java.awt.*;

public class AboutFrame extends BaseFrame {
    private static final int WIDTH = 300;
    private static final int HEIGHT = 300;

    public AboutFrame(BaseFrame intentionFrame) {
        super(WIDTH, HEIGHT, "About", intentionFrame);
        setResizable(false);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> onWindowClose(null));

        ImageIcon imageIcon = new ImageIcon(ClassLoader.getSystemClassLoader().getResource("me.png"), "");
        Image img = imageIcon.getImage();
        Image scaledImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        imageIcon.setImage(scaledImg);

        JTextArea aboutTextArea = new JTextArea("Aвтор: Семенов Сергей \ngithub.com/lvfx00\n НГУ, ФИТ, группа 16205\n Задача raytracing\n Версия 1.0\n Copyright(C) 2019");
        aboutTextArea.setEditable(false);
        aboutTextArea.setFont(new Font(Font.DIALOG, Font.BOLD, 15));

        add(new JLabel(imageIcon));
        add(aboutTextArea);
        add(okButton);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
    }

}
