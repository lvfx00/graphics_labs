package ru.nsu.fit.g16205.semenov.raytracing.model.tracing_primitives;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.raytracing.model.primitives.DoublePoint3D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.*;
import static ru.nsu.fit.g16205.semenov.raytracing.utils.VectorUtils.dotProduct;

public class Reflection {

    private final DoublePoint3D reflectionPoint;
    private final DoublePoint3D incomingDirection;
    private final DoublePoint3D reflectedDirection;
    private final Ray reflectedRay;
    private final RaytracingFigure figure;
    private final List<IncomingLight> incomingLights = new ArrayList<>();

    public Reflection(
            @NotNull DoublePoint3D reflectionPoint,
            @NotNull DoublePoint3D incomingDirection,
            @NotNull DoublePoint3D reflectedDirection,
            @NotNull RaytracingFigure figure
    ) {
        checkArgument(incomingDirection.isNormalizedWithError(1E-10), "Incoming ray must be normalized");
        checkArgument(reflectedDirection.isNormalizedWithError(1E-10), "Incoming ray must be normalized");
        this.reflectionPoint = reflectionPoint;
        this.incomingDirection = incomingDirection;
        reflectedRay = new Ray(reflectionPoint, reflectedDirection);
        this.figure = figure;
        this.reflectedDirection = reflectedDirection;
    }

    public @NotNull RaytracingFigure getFigure() {
        return figure;
    }

    public @NotNull DoublePoint3D getReflectionPoint() {
        return reflectionPoint;
    }

    public @NotNull DoublePoint3D getIncomingDirection() {
        return incomingDirection;
    }

    public @NotNull DoublePoint3D getReflectedDirection() {
        return reflectedDirection;
    }

    public @NotNull Ray getReflectedRay() {
        return reflectedRay;
    }

    public void addIncomingLight(@NotNull IncomingLight incomingLight) {
        incomingLights.add(incomingLight);
    }

    public @NotNull List<IncomingLight> getIncomingLights() {
        return Collections.unmodifiableList(incomingLights);
    }

    // TODO validate !!!!
    public float[] getReflectionFromLightSources() {
        final float[] totalLight = new float[3];
        final DoublePoint3D V = incomingDirection.scale(-1);
        for (IncomingLight incomingLight : incomingLights) {
            final DoublePoint3D L = incomingLight.getIncomingDirection().scale(-1);
            final DoublePoint3D LplusV = L.plus(V);
            final DoublePoint3D H = LplusV.scale(1. / LplusV.getNorm());
            final DoublePoint3D N = figure.getPrimitive().getNormalVector();
            final double NtoH = dotProduct(H, N);
            // TODO is valid formula ???
            // TODO replace pow to cycle???
            final double specularCoef = pow(NtoH, figure.getOpticalProperties().getPower());
            final double diffuseCoef = dotProduct(L, N);

            final float[] lightSourceComponents = incomingLight.getLightSource().getColor().getComponents(null);
            final double[] kDiffuse = figure.getOpticalProperties().getkDiffuse();
            final double[] kSpecular = figure.getOpticalProperties().getkSpecular();
            for (int i = 0; i < 3; ++i) {
                totalLight[i] += lightSourceComponents[i] * (specularCoef * kSpecular[i] + diffuseCoef * kDiffuse[i]) /
                        (1 + incomingLight.getDistance());
            }
        }
        return totalLight;
    }

}
