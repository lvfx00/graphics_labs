package ru.nsu.fit.g16205.semenov.wireframe.camera;

public class PyramidOfView {

    private final double sw;
    private final double sh;
    private final double zf;
    private final double zb;

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

}
