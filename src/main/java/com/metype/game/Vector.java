package com.metype.game;

public class Vector {
    double x = 0;
    double y = 0;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void mult(double size) {
        this.x *= size;
        this.y *= size;
    }

    public void div(double size) {
        this.x /= size;
        this.y /= size;
    }

    public void add(Vector v) {
        this.x += v.x;
        this.y += v.y;
    }

    public void set(Vector v) {
        this.x = v.x;
        this.y = v.y;
    }
}
