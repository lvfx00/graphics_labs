package ru.nsu.fit.g16205.semenov.wireframe;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.nsu.fit.g16205.semenov.wireframe.model.figure.BezierCurve;
import ru.nsu.fit.g16205.semenov.wireframe.model.figure.FigureData;
import ru.nsu.fit.g16205.semenov.wireframe.model.figure.FigureParameters;
import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.DoubleBox;
import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.DoublePoint;
import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.DoublePoint3D;
import ru.nsu.fit.g16205.semenov.wireframe.utils.DoubleBoxTransformer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.*;
import static ru.nsu.fit.g16205.semenov.wireframe.model.figure.BezierCurve.Adapter2D.matrixToPoint;

public class SurfaceCreator {

    private SurfaceCreator() {
    }

    public static List<Pair<DoublePoint3D, DoublePoint3D>> createSurface(FigureData figureData) {
        final BezierCurve curve = figureData.getCurve();
        final FigureParameters parameters = figureData.getParameters();
        final double a = parameters.getA();
        final double b = parameters.getB();
        final double c = parameters.getC();
        final double d = parameters.getD();
        final int n = parameters.getN();
        final int m = parameters.getM();
        final int k = parameters.getK();
        final int numSegments = (n * (m + 1) + (n + 1) * m) * k * 2;
        final List<Pair<DoublePoint3D, DoublePoint3D>> surfaceSegments = new ArrayList<>(numSegments);

        for (int i = 0; i <= m; ++i) {
            @Nullable DoublePoint3D previousPoint = null;
            final double iterV = ((double) i) / m;
            final double v = c * (1 - iterV) + d * iterV;
            for (int j = 0; j <= n * k; ++j) {
                final double iterU = ((double) j) / (n * k);
                final double u = a * (1 - iterU) + b * iterU;
                final DoublePoint3D surfacePoint = getSurfacePoint(curve, u, v);
                if (previousPoint != null) {
                    surfaceSegments.add(Pair.of(previousPoint, surfacePoint));
                }
                previousPoint = surfacePoint;
            }
        }
        for (int j = 0; j <= n; ++j) {
            @Nullable DoublePoint3D previousPoint = null;
            final double iterU = ((double) j) / n;
            final double u = a * (1 - iterU) + b * iterU;
            for (int i = 0; i <= m * k; ++i) {
                final double iterV = ((double) i) / (m * k);
                final double v = c * (1 - iterV) + d * iterV;
                final DoublePoint3D surfacePoint = getSurfacePoint(curve, u, v);
                if (previousPoint != null) {
                    surfaceSegments.add(Pair.of(previousPoint, surfacePoint));
                }
                previousPoint = surfacePoint;
            }
        }
        return surfaceSegments;
    }

    public static @NotNull List<Pair<DoublePoint3D, DoublePoint3D>> normalizeSurface(
            @NotNull List<Pair<DoublePoint3D, DoublePoint3D>> surface
    ) {
        checkArgument(surface.size() > 0, "Empty list :/");
//        surface.forEach(System.out::println); // TODO убрать
        final Supplier<Stream<DoublePoint3D>> streamSupplier = () -> surface
                .stream()
                .flatMap(pair -> Stream.of(pair.getLeft(), pair.getRight()));

        final double minX = streamSupplier.get().min(Comparator.comparingDouble(DoublePoint3D::getX)).get().getX();
        final double maxX = streamSupplier.get().max(Comparator.comparingDouble(DoublePoint3D::getX)).get().getX();
        final double minY = streamSupplier.get().min(Comparator.comparingDouble(DoublePoint3D::getY)).get().getY();
        final double maxY = streamSupplier.get().max(Comparator.comparingDouble(DoublePoint3D::getY)).get().getY();
        final double minZ = streamSupplier.get().min(Comparator.comparingDouble(DoublePoint3D::getZ)).get().getZ();
        final double maxZ = streamSupplier.get().max(Comparator.comparingDouble(DoublePoint3D::getZ)).get().getZ();

        final double xSize = maxX - minX;
        final double ySize = maxY - minY;
        final double zSize = maxZ - minZ;
        final double maxSize = Math.max(Math.max(xSize, ySize), zSize);

        final DoubleBoxTransformer transformer = new DoubleBoxTransformer(
                new DoubleBox(
                        minX - (maxSize - xSize) / 2,
                        maxX + (maxSize - xSize) / 2,
                        minY - (maxSize - ySize) / 2,
                        maxY + (maxSize - ySize) / 2,
                        minZ - (maxSize - zSize) / 2,
                        maxZ + (maxSize - zSize) / 2
                ),
                new DoubleBox(-1, 1, -1, 1, -1, 1)
        );
        //        normalizedSurface.forEach(System.out::println); // TODO убрать
        return surface
                .stream()
                .map(pair -> Pair.of(transformer.toBox2(pair.getLeft()), transformer.toBox2(pair.getRight())))
                .collect(Collectors.toList());
    }

    private static DoublePoint3D getSurfacePoint(@NotNull BezierCurve curve, double u, double v) {
        final DoublePoint curvePoint = matrixToPoint(curve.getPoint(u * curve.getTotalLength()));
        return new DoublePoint3D(
                curvePoint.getY() * Math.cos(v),
                curvePoint.getY() * Math.sin(v),
                curvePoint.getX()
        );
    }

}
