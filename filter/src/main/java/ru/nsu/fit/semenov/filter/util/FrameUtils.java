package ru.nsu.fit.semenov.filter.util;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class FrameUtils {

    public static @NotNull JPanel addAllToPanel(@NotNull JPanel panel, Component... components) {
        for (Component c : components) {
            panel.add(c);
        }
        return panel;
    }

}
