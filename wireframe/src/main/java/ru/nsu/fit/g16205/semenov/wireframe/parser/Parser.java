package ru.nsu.fit.g16205.semenov.wireframe.parser;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.g16205.semenov.wireframe.model.camera.CameraParameters;
import ru.nsu.fit.g16205.semenov.wireframe.model.camera.CameraPosition;
import ru.nsu.fit.g16205.semenov.wireframe.model.camera.PyramidOfView;
import ru.nsu.fit.g16205.semenov.wireframe.model.figure.BezierCurve;
import ru.nsu.fit.g16205.semenov.wireframe.model.figure.FigureData;
import ru.nsu.fit.g16205.semenov.wireframe.model.figure.FigureParameters;
import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.DoublePoint;
import ru.nsu.fit.g16205.semenov.wireframe.model.primitives.DoublePoint3D;

import java.awt.*;
import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static ru.nsu.fit.g16205.semenov.wireframe.model.figure.BezierCurve.Adapter2D.matrixToPointsList;
import static ru.nsu.fit.g16205.semenov.wireframe.model.figure.BezierCurve.Adapter2D.pointsToMatrix;

public class Parser {

    private Parser() {
    }

    public static Config parseFile(@NotNull File file) throws IOException, ParseException {
        try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(file)))) {
            final double sw = readNextDouble(scanner);
            final double sh = readNextDouble(scanner);
            final double zf = readNextDouble(scanner);
            final double zb = readNextDouble(scanner);
            final DoublePoint3D pCam = readNextDoublePoint3D(scanner);
            final DoublePoint3D pView = readNextDoublePoint3D(scanner);
            final DoublePoint3D upVect = readNextDoublePoint3D(scanner);
// TODO            final Color backgroundColor = readNextColor(scanner);
            final CameraParameters cameraParameters = new CameraParameters(
                    new PyramidOfView(sw, sh, zf, zb),
                    new CameraPosition(pCam, pView, upVect)
            );
            final int figuresNum = readNextInt(scanner);
            final List<FigureData> figureDataList = new ArrayList<>();
            for (int i = 0; i < figuresNum; ++i) {
                final int pointsNum = readNextInt(scanner);
                final List<DoublePoint> achorPoints = new ArrayList<>();
                for (int j = 0; j < pointsNum; ++j) {
                    achorPoints.add(readNextDoublePoint(scanner));
                }
                scanner.nextLine();
                final String name = scanner.nextLine();
                final double a = readNextDouble(scanner);
                final double b = readNextDouble(scanner);
                final double c = readNextDouble(scanner);
                final double d = readNextDouble(scanner);
                final int n = readNextInt(scanner);
                final int m = readNextInt(scanner);
                final int k = readNextInt(scanner);
                final DoublePoint3D cPoint = readNextDoublePoint3D(scanner);
                final double thetaX = readNextDouble(scanner);
                final double thetaY = readNextDouble(scanner);
                final double thetaZ = readNextDouble(scanner);
                final Color color = readNextColor(scanner);
                figureDataList.add(new FigureData(
                        new BezierCurve(pointsToMatrix(achorPoints)),
                        new FigureParameters(a, b, c, d, n, m, k, cPoint, thetaX, thetaY, thetaZ, color, name)
                ));
            }
            return new Config(figureDataList, cameraParameters);
        }
    }

    public static void saveToFile(Config config, File file) throws IOException {
        final CameraPosition cameraPosition = config.getCameraParameters().getCameraPosition();
        final PyramidOfView pyramidOfView = config.getCameraParameters().getPyramidOfView();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            writeDouble(pyramidOfView.getSw(), bw);
            writeDouble(pyramidOfView.getSh(), bw);
            writeDouble(pyramidOfView.getZf(), bw);
            writeDouble(pyramidOfView.getZb(), bw);
            writeDoublePoint3D(cameraPosition.getCameraPoint(), bw);
            writeDoublePoint3D(cameraPosition.getViewPoint(), bw);
            writeDoublePoint3D(cameraPosition.getUpVector(), bw);
// TODO            final Color backgroundColor = readNextColor(scanner);
            writeInt(config.getFigureDataList().size(), bw);
            for (FigureData fd : config.getFigureDataList()) {
                final List<DoublePoint> points = matrixToPointsList(fd.getCurve().getAnchorPoints());
                writeInt(points.size(), bw);
                for (DoublePoint p : points) {
                    writeDoublePoint(p, bw);
                }
                final FigureParameters fp = fd.getParameters();
                bw.write(fp.getName());
                bw.newLine();
                writeDouble(fp.getA(), bw);
                writeDouble(fp.getB(), bw);
                writeDouble(fp.getC(), bw);
                writeDouble(fp.getD(), bw);
                writeInt(fp.getN(), bw);
                writeInt(fp.getM(), bw);
                writeInt(fp.getK(), bw);
                writeDoublePoint3D(fp.getPointC(), bw);
                writeDouble(fp.getThetaX(), bw);
                writeDouble(fp.getThetaY(), bw);
                writeDouble(fp.getThetaZ(), bw);
                writeColor(fp.getColor(), bw);
            }
        }
    }

    private static void writeDouble(double d, @NotNull BufferedWriter bw) throws IOException {
        bw.write(Double.toString(d));
        bw.newLine();
    }

    private static void writeInt(int i, @NotNull BufferedWriter bw) throws IOException {
        bw.write(Integer.toString(i));
        bw.newLine();
    }

    private static void writeDoublePoint(@NotNull DoublePoint p, @NotNull BufferedWriter bw) throws IOException {
        bw.write(p.getX() + " " + p.getY());
        bw.newLine();
    }

    private static void writeDoublePoint3D(@NotNull DoublePoint3D p, @NotNull BufferedWriter bw) throws IOException {
        bw.write(p.getX() + " " + p.getY() + " " + p.getZ());
        bw.newLine();
    }

    private static void writeColor(Color c, @NotNull BufferedWriter bw) throws IOException {
        bw.write(c.getRed() + " " + c.getGreen() + " " + c.getBlue());
        bw.newLine();
    }

    private static @NotNull DoublePoint3D readNextDoublePoint3D(@NotNull Scanner scanner) throws ParseException {
        double[] components = new double[3];
        for (int j = 0; j < 3; ++j) {
            components[j] = readNextDouble(scanner);
        }
        return new DoublePoint3D(components[0], components[1], components[2]);
    }

    private static @NotNull DoublePoint readNextDoublePoint(@NotNull Scanner scanner) throws ParseException {
        double[] components = new double[2];
        for (int j = 0; j < 2; ++j) {
            components[j] = readNextDouble(scanner);
        }
        return new DoublePoint(components[0], components[1]);
    }

    private static double readNextDouble(@NotNull Scanner scanner) throws ParseException {
        if (scanner.hasNextDouble()) {
            return scanner.nextDouble();
        } else {
            throw new ParseException("", 0);
        }
    }

    private static int readNextInt(@NotNull Scanner scanner) throws ParseException {
        if (scanner.hasNextInt()) {
            return scanner.nextInt();
        } else {
            throw new ParseException("", 0);
        }
    }

    private static @NotNull Color readNextColor(@NotNull Scanner scanner) throws ParseException {
        int[] colorComponents = new int[3];
        for (int j = 0; j < 3; ++j) {
            colorComponents[j] = readNextInt(scanner);
        }
        return new Color(colorComponents[0], colorComponents[1], colorComponents[2]);
    }

}
