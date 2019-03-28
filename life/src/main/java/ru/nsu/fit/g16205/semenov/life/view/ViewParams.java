package ru.nsu.fit.g16205.semenov.life.view;

import java.awt.*;

public class ViewParams {
    private final int hexLength;
    private final int borderWidth;

    private final int fieldOffset;

    private final int aliveCellColor;
    private final int deadCellColor;
    private final int backgroundColor;
    private final int borderColor;

    public ViewParams(int hexLength,
                      int borderWidth,
                      int fieldOffset,
                      int aliveCellColor,
                      int deadCellColor,
                      int backgroundColor,
                      int borderColor) {
        this.hexLength = hexLength;
        this.borderWidth = borderWidth;
        this.fieldOffset = fieldOffset;
        this.aliveCellColor = aliveCellColor;
        this.deadCellColor = deadCellColor;
        this.backgroundColor = backgroundColor;
        this.borderColor = borderColor;
    }

    public int getHexLength() {
        return hexLength;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public int getFieldOffset() {
        return fieldOffset;
    }

    public int getAliveCellColor() {
        return aliveCellColor;
    }

    public int getDeadCellColor() {
        return deadCellColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getBorderColor() {
        return borderColor;
    }


    public static class Builder {
        private int hexLength = 30;
        private int borderWidth = 2;

        private int fieldOffset = 30;

        private int aliveCellColor = Color.GREEN.getRGB();
        private int deadCellColor = Color.RED.getRGB();
        private int backgroundColor = Color.DARK_GRAY.getRGB();
        private int borderColor = Color.YELLOW.getRGB();

        public ViewParams build() {
            return new ViewParams(
                    hexLength,
                    borderWidth,
                    fieldOffset,
                    aliveCellColor,
                    deadCellColor,
                    backgroundColor,
                    borderColor);
        }

        public Builder setHexLength(int hexLength) {
            this.hexLength = hexLength;
            return this;
        }

        public Builder setBorderWidth(int borderWidth) {
            this.borderWidth = borderWidth;
            return this;
        }

        public Builder setFieldOffset(int fieldOffset) {
            this.fieldOffset = fieldOffset;
            return this;
        }

        public Builder setAliveCellColor(int aliveCellColor) {
            this.aliveCellColor = aliveCellColor;
            return this;
        }

        public Builder setDeadCellColor(int deadCellColor) {
            this.deadCellColor = deadCellColor;
            return this;
        }

        public Builder setBackgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder setBorderColor(int borderColor) {
            this.borderColor = borderColor;
            return this;
        }
    }
}
