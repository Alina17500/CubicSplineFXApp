package com.cgvsu.cubicspline;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class Spline2D {
    Spline x, y;
    List<Double> params;
    List<Point2D> pointsX = new ArrayList<>();
    List<Point2D> pointsY = new ArrayList<>();

    public Spline2D(List<Point2D> points) {
        params = calculateParams(points);

        for (int i = 0; i < params.size(); i++) {
            pointsX.add(i, new Point2D(params.get(i), points.get(i).getX()));
            pointsY.add(i, new Point2D(params.get(i), points.get(i).getY()));
        }

        x = new Spline(pointsX);
        y = new Spline(pointsY);
    }

    public List<Double> getParams(int num) {
        params = linspace(params.get(0), params.get(params.size() - 1), num);
        return params;
    }

    //Возвращает равномерно распределенные числа через указанный интервал
    public static List<Double> linspace(double start, double stop, int n) {
        List<Double> result = new ArrayList<>();

        double step = (stop-start) / (n - 1);

        for(int i = 0; i < n - 1; i++) {
            result.add(start + (i * step));
        }
        result.add(stop);

        return result;
    }

    public Point2D points(double param) {
        return new Point2D(x.calc(param), y.calc(param));
    }

    private List<Double> calculateParams(List<Point2D> points) {
        List<Double> params = new ArrayList<>();
        params.add(0.0);

        double value = 0.0;
        for (int i = 0; i < points.size() - 1; i++) {
            value = value + Math.hypot(points.get(i + 1).getX() - points.get(i).getX(), points.get(i + 1).getY() - points.get(i).getY());
            params.add(value);
        }

        return params;
    }
}
