package com.metype.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.paint.Color;

import java.util.ArrayList;

import static com.metype.game.EditorScreen.map;

public class Level {
    Tile[][] t;
    ArrayList<Light> lightCones = new ArrayList<Light>();
    boolean onoffstate = false;
    boolean completed = false;

    public Level(int scrX, int scrY) {
        t = new Tile[scrX*15][scrY*11];
        for (int i=0; i<scrX*15; i++) {
            for (int j=0; j<scrY*11; j++) {
                if (i==0||j==0||i==scrX*15-1||j==scrY*11-1)
                    t[i][j]= new Tile(1);
                else
                    t[i][j]= new Tile(0);
            }
        }
    }

    public int screenX(){
        return t.length/15;
    }

    public int screenY(){
        return t[0].length/11;
    }

    public void calculateLighting(int passes) {
        for(int pass=0;pass<passes;pass++) {
            Color[][] intensities = new Color[this.t.length][this.t[0].length];
            double red = 0;
            double green = 0;
            double blue = 0;
            int count = 0;
            for (int i = 0; i < t.length; i++) {
                for (int j = 0; j < t[i].length; j++) {
                    if (!this.t[i][j].isSolid(onoffstate))
                        for (int var1 = i + 1; var1 > i - 2; var1--) {
                            for (int var2 = j - 1; var2 < j + 2; var2++) {
                                if (/*(var1 != i && var2 != j) || */(var1 == i && var2 == j)) continue;
                                try {
                                    if (this.t[var1][var2].lightIntensity.getBrightness() > 0) {
                                        red += this.t[var1][var2].lightIntensity.getRed();
                                        green += this.t[var1][var2].lightIntensity.getGreen();
                                        blue += this.t[var1][var2].lightIntensity.getBlue();
                                        count++;
                                    }
                                    //highestIntensity = this.t[var1][var2].lightIntensity.interpolate(highestIntensity, .5);
                                } catch (ArrayIndexOutOfBoundsException ignored) {
                                }
                            }
                        }
                    intensities[i][j] = Color.color((red / count) * t[i][j].lightPermablility(onoffstate), (green / count) * t[i][j].lightPermablility(onoffstate), (blue / count) * t[i][j].lightPermablility(onoffstate));
                }
            }
            for (int i = 0; i < this.t.length; i++) {
                for (int j = 0; j < this.t[0].length; j++) {
                    if (intensities[i][j].getBrightness() > .01f / 1000)
                        t[i][j].lightIntensity = intensities[i][j];
                }
            }
        }
    }

    public Vector getSpawnPos(){
        for(int i=0;i<t.length;i++) {
            for(int j=0;j<t[i].length;j++){
                if(t[i][j].id==7) return new Vector(i,j);
            }
        }
        return null;
    }

    public Vector render(double x, double y, double tileSize, GraphicsContext gc, double width, double height, Tile... highlight) {
        int lowx=0,
            lowy=0,
            highx=t.length,
            highy=t[0].length;
        Vector respawnPos = new Vector(0,0);
        Color[][] intensities = new Color[this.t.length][this.t[0].length];
//        if (renderBox!=null) {
////            lowx = max(((int)renderBox.x)-15, 0);
////            lowy = max(((int)renderBox.y)-15, 0);
////            highx = min(((int)renderBox.x)+15, currentLevel.t.length);
////            highy = min(((int)renderBox.y)+15, currentLevel.t[0].length);
//        }
        for (int i=0; i<t.length; i++) {
            for (int j=0; j<t[i].length; j++) {
                t[i][j].setHitBox(new Rect(x+(i*tileSize), y+(j*tileSize), tileSize, tileSize));
                if(t[i][j].id==7) respawnPos = new Vector(i,j);
                if(!(new Rect(0,0,width,height).isTouching(t[i][j].hitBox)) && (t[i][j].id<17 || t[i][j].id>22)) continue;
                Boolean[] b = new Boolean[12];
                if (i == 0 || j == 0) b[0] = true;
                else b[0] = t[i - 1][j - 1].isSolid(onoffstate);

                if (j == 0) b[1] = true;
                else b[1] = t[i][j - 1].isSolid(onoffstate);

                if (i == t.length - 1 || j == 0) b[2] = true;
                else b[2] = t[i + 1][j - 1].isSolid(onoffstate);

                if (i == t.length - 1) b[3] = true;
                else b[3] = t[i + 1][j].isSolid(onoffstate);

                if (i == t.length - 1 || j == t[i].length - 1) b[4] = true;
                else b[4] = t[i + 1][j + 1].isSolid(onoffstate);

                if (j == t[i].length - 1) b[5] = true;
                else b[5] = t[i][j + 1].isSolid(onoffstate);

                if (i == 0 || j == t[i].length - 1) b[6] = true;
                else b[6] = t[i - 1][j + 1].isSolid(onoffstate);

                if (i == 0) b[7] = true;
                else b[7] = t[i - 1][j].isSolid(onoffstate);

                if (i == 0) b[8] = true;
                else b[8] = t[i - 1][j].id == 3;

                if (i == t.length - 1) b[9] = true;
                else b[9] = t[i + 1][j].id == 3;

                if (i == 0) b[10] = true;
                else b[10] = t[i - 1][j].id == 6;

                if (i == t.length - 1) b[11] = true;
                else b[11] = t[i + 1][j].id == 6;
                double red = 0;
                double green = 0;
                double blue = 0;
                int count = 0;
                t[i][j].render(gc, tileSize*1.01f, 1, null, false, onoffstate, Color.color(0,0,0), this, b);
                    for (int var1 = i + 1; var1 > i -2; var1--) {
                        for (int var2 = j - 1; var2 < j + 2; var2++) {
//                            if (/*(var1 != i && var2 != j) || */(var1 == i && var2 == j)) continue;
                            try {
//                                if(this.t[var1][var2].id==16) continue;
                                if(this.t[var1][var2].lightIntensity.getBrightness()>0) {
//                                    if(this.t[var1][var2].id==3){
//                                        System.out.println("("+this.t[var1][var2].lightIntensity.getRed()*255+", "+this.t[var1][var2].lightIntensity.getGreen()*255+", "+this.t[var1][var2].lightIntensity.getBlue()*255+")");
//                                    }
                                    red += this.t[var1][var2].lightIntensity.getRed();
                                    green += this.t[var1][var2].lightIntensity.getGreen();
                                    blue += this.t[var1][var2].lightIntensity.getBlue();
                                    count++;
                                }
                                //highestIntensity = this.t[var1][var2].lightIntensity.interpolate(highestIntensity, .5);
                            } catch (ArrayIndexOutOfBoundsException ignored) {
                            }
                        }
                    }
                intensities[i][j] = Color.color((red/count)*t[i][j].lightPermablility(onoffstate),(green/count)*t[i][j].lightPermablility(onoffstate),(blue/count)*t[i][j].lightPermablility(onoffstate));
            }
        }
        for (int i=0;i<this.t.length;i++){
            for (int j=0;j<this.t[0].length;j++){
                if(intensities[i][j]==null) continue;
                //t[i][j].render(gc, tileSize*1.01f, 1, null, true, onoffstate, Color.color(0,0,0));
                if(t[i][j].id==16)
                    t[i][j].lightIntensity= Color.color(0,0,0);
                if(intensities[i][j].getBrightness()>.01f/10000)
                    if(t[i][j].id==16) {
                        t[i][j].lightIntensity = t[i][j].lightIntensity.interpolate(t[i][j].tint, (map(t[i][j].tint.getOpacity(),0,1,1,0))*intensities[i][j].getBrightness());
                    } else {
                        t[i][j].lightIntensity = intensities[i][j];
                    }
            }
        }
        lightCones.clear();
        if(highlight.length>0)
        for (Tile[] tiles : t) {
            for (Tile value : tiles) {
                for (Tile tile : highlight) {
                    if (value == tile) {
                        gc.setStroke(Color.WHITE);
                        gc.setLineWidth(2);
                        gc.strokeRect(value.hitBox.x, value.hitBox.y, value.hitBox.w, value.hitBox.h);
                        gc.setLineWidth(1);
                    }
                }
            }
        }
        return respawnPos;
    }

    public Level copy(){
        Level l = new Level(0,0);
        l.t=t;
        l.onoffstate=onoffstate;
        return l;
    }
}
