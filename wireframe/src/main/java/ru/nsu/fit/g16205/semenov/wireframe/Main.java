package ru.nsu.fit.g16205.semenov.wireframe;

import ru.nsu.fit.g16205.semenov.wireframe.generatrix.GeneratrixFrame;
import ru.nsu.fit.g16205.semenov.wireframe.model.DoubleRectangle;

public class Main {
    public static void main(String[] args) {
        GeneratrixFrame generatrixFrame = new GeneratrixFrame(
          new DoubleRectangle(0, 0, 1, Math.PI * 2)
        );
        generatrixFrame.setVisible(true);
    }
}
