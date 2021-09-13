package com.metype.game;

import javafx.scene.paint.Color;

public class Light {
    Polygon shape;
    Color color;
    Vector center;

    public Light(Polygon shape, Vector center) {
        this.color = Color.rgb(50, 50, 50);
        this.shape = shape;
        this.center = center;
    }

    public Light(Polygon shape, Color color, Vector center) {
        this.color = color;
        this.shape = shape;
        this.center = center;
    }
}
