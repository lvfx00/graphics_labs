package ru.nsu.fit.g16205.semenov.raytracing.model.primitives;

public class DoubleLine {

    private final DoublePoint3D p1;
    private final DoublePoint3D p2;

    public DoubleLine(DoublePoint3D p1, DoublePoint3D p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public DoublePoint3D getP1() {
        return p1;
    }

    public DoublePoint3D getP2() {
        return p2;
    }

}
