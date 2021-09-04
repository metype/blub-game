package com.metype.game;

public class Rect extends Polygon{
    double x,y,w,h;

    public Rect(double x, double y, double w, double h) {
        super(new Vector(x,y),new Vector(x+w,y),new Vector(x+w,y+h), new Vector(x,y+h));
        this.x=x;
        this.y=y;
        this.w=w;
        this.h=h;
    }
}
