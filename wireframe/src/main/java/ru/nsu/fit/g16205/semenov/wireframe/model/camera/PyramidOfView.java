package ru.nsu.fit.g16205.semenov.wireframe.model.camera;

public class PyramidOfView {

    private double sw;
    private double sh;
    private double zf;
    private double zb;

    public PyramidOfView(double sw, double sh, double zf, double zb) {
        this.sw = sw;
        this.sh = sh;
        this.zf = zf;
        this.zb = zb;
    }

    public double getSw() {
        return sw;
    }

    public double getSh() {
        return sh;
    }

    public double getZf() {
        return zf;
    }

    public double getZb() {
        return zb;
    }

    public void setSw(double sw) {
        this.sw = sw;
    }

    public void setSh(double sh) {
        this.sh = sh;
    }

    public void setZf(double zf) {
        this.zf = zf;
    }

    public void setZb(double zb) {
        this.zb = zb;
    }

}
