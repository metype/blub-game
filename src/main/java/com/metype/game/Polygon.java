package com.metype.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Polygon {
    static int INF = 10000;
    Vector[] points;

    public Polygon(Vector... points) {
        this.points = points;
    }

    // Given three colinear points p, q, r,
    // the function checks if point q lies
    // on line segment 'pr'
    static boolean onSegment(Point p, Point q, Point r) {
        if (q.x <= Math.max(p.x, r.x) &&
                q.x >= Math.min(p.x, r.x) &&
                q.y <= Math.max(p.y, r.y) &&
                q.y >= Math.min(p.y, r.y)) {
            return true;
        }
        return false;
    }

    // To find orientation of ordered triplet (p, q, r).
    // The function returns following values
    // 0 --> p, q and r are colinear
    // 1 --> Clockwise
    // 2 --> Counterclockwise
    static int orientation(Point p, Point q, Point r) {
        double val = (q.y - p.y) * (r.x - q.x)
                - (q.x - p.x) * (r.y - q.y);

        if (val == 0) {
            return 0; // colinear
        }
        return (val > 0) ? 1 : 2; // clock or counterclock wise
    }

    // The function that returns true if
    // line segment 'p1q1' and 'p2q2' intersect.
    static boolean doIntersect(Vector p1, Vector q1, Vector p2, Vector q2) {
        return doIntersect(new Point(p1.x, p1.y), new Point(q1.x, q1.y), new Point(p2.x, p2.y), new Point(q2.x, q2.y));
    }

    static boolean doIntersect(Point p1, Point q1,
                               Point p2, Point q2) {
        // Find the four orientations needed for
        // general and special cases
        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);

        // General case
        if (o1 != o2 && o3 != o4) {
            return true;
        }

        // Special Cases
        // p1, q1 and p2 are colinear and
        // p2 lies on segment p1q1
        if (o1 == 0 && onSegment(p1, p2, q1)) {
            return true;
        }

        // p1, q1 and p2 are colinear and
        // q2 lies on segment p1q1
        if (o2 == 0 && onSegment(p1, q2, q1)) {
            return true;
        }

        // p2, q2 and p1 are colinear and
        // p1 lies on segment p2q2
        if (o3 == 0 && onSegment(p2, p1, q2)) {
            return true;
        }

        // p2, q2 and q1 are colinear and
        // q1 lies on segment p2q2
        if (o4 == 0 && onSegment(p2, q1, q2)) {
            return true;
        }

        // Doesn't fall in any of the above cases
        return false;
    }

    ;

    public void render(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        for (int i = 0; i < points.length; i++) {
            if (i < points.length - 1)
                gc.strokeLine(points[i].x, points[i].y, points[i + 1].x, points[i + 1].y);
            else
                gc.strokeLine(points[i].x, points[i].y, points[0].x, points[0].y);
        }
    }

    public void addPoint(Vector v) {
        Vector[] newArr = new Vector[points.length + 1];
        for (int i = 0; i < points.length; i++) {
            newArr[i] = points[i];
        }
        newArr[points.length] = v;
        points = newArr;
    }

    public boolean isTouching(Polygon p) {
        boolean touching = false;
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < p.points.length; j++) {
                if (i < points.length - 1) {
                    if (j < p.points.length - 1) {
                        if (doIntersect(points[i], points[i + 1], p.points[j], p.points[j + 1])) touching = true;
                    } else {
                        if (doIntersect(points[i], points[i + 1], p.points[j], p.points[0])) touching = true;
                    }
                } else {
                    if (j < p.points.length - 1) {
                        if (doIntersect(points[i], points[0], p.points[j], p.points[j + 1])) touching = true;
                    } else {
                        if (doIntersect(points[i], points[0], p.points[j], p.points[0])) touching = true;
                    }
                }
            }
        }
        for (Vector point : p.points) {
            if (contains(point)) touching = true;
        }
        return touching;
    }

    public boolean contains(Vector test) {
        int i;
        int j;
        boolean result = false;
        for (i = 0, j = points.length - 1; i < points.length; j = i++) {
            if ((points[i].y > test.y) != (points[j].y > test.y) &&
                    (test.x < (points[j].x - points[i].x) * (test.y - points[i].y) / (points[j].y - points[i].y) + points[i].x)) {
                result = !result;
            }
        }
        return result;
    }

    static class Point {
        double x;
        double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}
