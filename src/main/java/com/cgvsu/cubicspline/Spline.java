package com.cgvsu.cubicspline;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class Spline {
    public List<Point2D> points;

    public Spline(List<Point2D> points) {
        this.points = points;
    }

    public double calc(double param) {
        int size = points.size();
        List<Double> h = calculateH(size - 1);
        double[] u = calculateU(size, h);
        double[] v = calculateV(size, h);
        double[][] tempLeftMatrix = calculateLeftMatrix(size, u, v);
        double[] rightMatrix = calculateRightMatrix(size, u, v, h);
        double[] m = calculateUVM(size, tempLeftMatrix, rightMatrix);

        if (Double.isNaN(param)) {
            return param;
        }
        if (param <= points.get(0).getX()) {
            return points.get(0).getY();
        }
        if (param >= points.get(size - 1).getX()) {
            return points.get(size - 1).getY();
        }

        int i = 0;
        for (; param >= points.get(i + 1).getX(); ++i) {
            if (param == points.get(i).getX()) {
                return points.get(i).getY();
            }
        }

        double t = (param - points.get(i).getX()) / h.get(i);
        return points.get(i).getY() * (t-1)*(t-1) * (2*t+1) + points.get(i+1).getY() * t*t * (3-2*t)
                + m[i] * h.get(i) * t * (1-t)*(1-t) - m[i+1] * h.get(i) * t*t * (1-t);
    }

    private List<Double> calculateH(int size) {
        List<Double> h = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            h.add(i, points.get(i+1).getX() - points.get(i).getX());
        }
        return h;
    }
    private double[] calculateU(int size, List<Double> h) {
        double[] u = new double[size];
        for (int i = 0; i < size; i++) {
            if (i != 0 && i != size - 1) {
                u[i] = h.get(i - 1) / (h.get(i - 1) + h.get(i));
            }
        }
        return u;
    }

    private double[] calculateV(int size, List<Double> h) {
        double[] v = new double[size];
        for (int i = 0; i < size; i++) {
            if (i != 0 && i != size - 1) {
                v[i] = h.get(i) / (h.get(i - 1) + h.get(i));
            }
        }
        return v;
    }

    private double[][] calculateLeftMatrix(int size, double[] u, double[] v) {
        double[][] leftMatrix = new double[size][size];
        leftMatrix[0][1] = 0;
        leftMatrix[1][0] = 0;
        for (int i = 0; i < size; i++) {
            leftMatrix[i][i] = 2;
            if (i != 0 && i != size - 1) {
                leftMatrix[i][i + 1] = u[i];
                leftMatrix[i + 1][i] = v[i];
            }
        }
        return leftMatrix;
    }

    private double[] calculateRightMatrix(int size, double[] u, double[] v, List<Double> h) {
        double[] rightMatrix = new double[size];
        rightMatrix[0] = 0;
        for (int i = 0; i < size - 2; i++) {
            rightMatrix[i+1] = 3*(v[i+1]*((points.get(i+1).getY() - points.get(i).getY())/h.get(i)) +
                    u[i+1]*((points.get(i+2).getY() - points.get(i+1).getY())/h.get(i+1)));
        }
        rightMatrix[size-1] = 0;
        return rightMatrix;
    }

    private double[] calculateUVM(int size, double[][] matrix, double[] d) {
        double[] m = new double[size];
        //коэффициенты прогонки
        double[] u = new double[size];
        double[] v = new double[size];

        //прямой ход прогонки
        for (int i = 0; i < size; i++) {
            if (i == 0) {
                v[i] = 0;
                u[i] = 0;
            } else if (i == size - 1) {
                v[i] = 0;
                u[i] = (d[i] - matrix[i][i-1]*u[i-1]) / (matrix[i][i-1]*v[i-1] + matrix[i][i]);
            } else {
                v[i] = -matrix[i][i+1] / (matrix[i][i-1]*v[i-1] + matrix[i][i]);
                u[i] = (d[i] - matrix[i][i-1]*u[i-1]) / (matrix[i][i-1]*v[i-1] + matrix[i][i]);
            }
        }

        //обратный ход прогонки
        m[size - 1] = u[size - 1];
        for (int i = size - 1; i > 0; i--) {
            m[i-1] = v[i-1]*m[i] + u[i-1];
        }
        return m;
    }

}
