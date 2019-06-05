package ru.nsu.fit.g16205.semenov.raytracing.model.tracing_primitives;

public class RaytracingFigure {

    public static final RaytracingFigure VOID = new RaytracingFigure(null, null);

    private final RaytracingPrimitive primitive;
    private final OpticalProperties opticalProperties;

    public RaytracingFigure(RaytracingPrimitive primitive, OpticalProperties opticalProperties) {
        this.primitive = primitive;
        this.opticalProperties = opticalProperties;
    }

    public RaytracingPrimitive getPrimitive() {
        return primitive;
    }

    public OpticalProperties getOpticalProperties() {
        return opticalProperties;
    }

}
