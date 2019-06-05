package ru.nsu.fit.g16205.semenov.raytracing.model.primitives;

public class IntLine {

    private final IntPoint p1;
    private final IntPoint p2;

    public IntLine(IntPoint p1, IntPoint p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public IntPoint getP1() {
        return p1;
    }

    public IntPoint getP2() {
        return p2;
    }

}
