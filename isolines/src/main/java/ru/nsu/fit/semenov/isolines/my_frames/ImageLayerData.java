package ru.nsu.fit.semenov.isolines.my_frames;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ImageLayerData {

    private final JLabel label = new JLabel();
    private final List<AbstractButton> buttonsList = new ArrayList<>();
    private boolean isShown = false;
    private final Supplier<BufferedImage> imageSupplier;

    public ImageLayerData(Supplier<BufferedImage> imageSupplier) {
        this.imageSupplier = imageSupplier;
    }

    public @NotNull JLabel getLabel() {
        return label;
    }

    public @NotNull List<AbstractButton> getButtonsList() {
        return buttonsList;
    }

    public boolean isShown() {
        return isShown;
    }

    public void setShown(boolean shown) {
        isShown = shown;
    }

    public void addButton(@NotNull AbstractButton button) {
        buttonsList.add(button);
    }

    public BufferedImage createImage() {
        return imageSupplier.get();
    }

}
