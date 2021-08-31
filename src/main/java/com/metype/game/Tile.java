package com.metype.game;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class Tile {
    public int id = 0;
    Rect hitBox;
    float angle = 0;
    public String text = "";
    long timer = 0;
    int limit=3;
    int spawned=0;
    int spawnDelay=1000;
    Light lightCone = null;
    Color lightIntensity = Color.color(0,0,0.01);
    public Color tint = Color.hsb(120, .57, 1, 0.35);
    public int alignment = 1;
//    PVector exact = new PVector(0, 0);
    boolean state = false;
    public Vector[] inputs = new Vector[0];

    public Tile(int id) {
        this.id=id;
        if(id == 3){
            lightIntensity=Color.rgb(255,0,0);
        }
    }

    public void setHitBox(Rect r) {
        this.hitBox=r;
    }

    public boolean isCollidable(boolean onoffstate) {
        int[] colliding = {1, 2, 4, 5, 9, 1, 3, 6, 2, 8, 16, 20, 21, 22};
        if (onoffstate) {
            colliding[5]=10;
        } else {
            colliding[5]=11;
        }
        return Index.findIndex(colliding, id)!=-1;
    }

    public boolean isSolid(boolean onoffstate) {
        int[] colliding = {1, 2, 4, 5, 9, 1, 3, 16, 20, 21, 22};
        if (onoffstate) {
            colliding[5]=10;
        } else {
            colliding[5]=11;
        }
        return Index.findIndex(colliding, id)!=-1;
    }


    public double lightPermablility(boolean onoffstate){
        if(id==16) return 1;
        if(isSolid(onoffstate)) return 0;
        switch(id){
            case(6):
            case(13):
            case(12):
                return .94;
            default:
                return .98;
        }
    }

    public void render(GraphicsContext gc, double tileSize, Boolean... surrounded) {
        this.render(gc, tileSize, 1, null, Color.color(0,0,0), surrounded);
    }
    public void render(GraphicsContext gc, double tileSize, double transparency, Boolean... surrounded) {
        this.render(gc, tileSize, transparency, null, Color.color(0,0,0), surrounded);
    }
    public void render(GraphicsContext gc, double tileSize, double transparency, Light[] lightCones, Boolean... surrounded) {
        this.render(gc, tileSize, transparency, lightCones, Color.color(0,0,0), surrounded);
    }
    public void render(GraphicsContext gc, double tileSize, double transparency, Light[] lightCones, Color brighter, Boolean... surrounded) {
        this.render(gc, tileSize, transparency, lightCones, false, false, brighter, null, surrounded);
    }
    public void render(GraphicsContext gc, double tileSize, double transparency, Light[] lightCones, boolean renderText, boolean onoff, Color brighter, Level l, Boolean... surrounded) {
//        exact = new PVector(x, y);
//        noStroke();
//        if (!hitBox.isTouching(new Rect(-hitBox.w, -hitBox.w, width+hitBox.w*2, height+hitBox.w*2)))
//            return;
//        push();
        if(renderText){
            if(id==12){
                switch(alignment){
                    case(0):
                        gc.setTextAlign(TextAlignment.LEFT);
                        break;
                    case(1):
                        gc.setTextAlign(TextAlignment.CENTER);
                        break;
                    case(2):
                        gc.setTextAlign(TextAlignment.RIGHT);
                        break;
                    case(3):
                        gc.setTextAlign(TextAlignment.JUSTIFY);
                        break;
                }
                if(text.equalsIgnoreCase("")){
                    gc.setFill(Color.DARKGRAY);
                    gc.setStroke(Color.BLACK);
                    gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, (int)(gc.getCanvas().getHeight()/50f)));
                    gc.fillText("ABC...",hitBox.x+(hitBox.w/2),hitBox.y+(hitBox.h/2)+((gc.getCanvas().getHeight()/150f)));
                    gc.setFill(Color.color(.5,.5,.5,0));
                    return;
                }
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, (int)(gc.getCanvas().getHeight()/50f)));
                gc.fillText(text,hitBox.x+(hitBox.w/2),hitBox.y+(hitBox.h/2)+((gc.getCanvas().getHeight()/150f)));
            }
            return;
        }
        switch(id){
            case(0): // Air
            case(12):// Text Tile
                gc.setFill(Color.color(.5,.5,.5,0));
                break;
            case(1): // Solid Wall
                gc.setFill(Color.BLACK);
                break;
            case(2): // End Tile
                gc.setFill(Color.YELLOW);
                break;
            case(3): // Lava Tile
                gc.setFill(Color.RED);
                break;
            case(4): // Bouncy Tile
                gc.setFill(Color.rgb(27,222,34));
                break;
            case(5): // Slippery Tile
                gc.setFill(Color.rgb(27,103,222));
                break;
            case(6): // Water
                gc.setFill(Color.color(0.25,0.55,0.75,0.75));
                break;
            case(7): // Start Tile
                gc.setFill(Color.MEDIUMPURPLE);
                gc.fillRoundRect(hitBox.x, hitBox.y, tileSize, tileSize, 20, 90);
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, (int)(tileSize/3)));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText("Start",hitBox.x+(hitBox.w/2),hitBox.y+(hitBox.h/2)+((gc.getCanvas().getHeight()/150f)));
                gc.setFill(Color.color(.5,.5,.5,0));
                break;
            case(8): // Checkpoint
                gc.setFill(Color.MEDIUMPURPLE);
                gc.fillRoundRect(hitBox.x, hitBox.y, tileSize, tileSize, 20, 90);
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, (int)(tileSize/3)));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText("Ckpt",hitBox.x+(hitBox.w/2),hitBox.y+(hitBox.h/2)+((gc.getCanvas().getHeight()/150f)));
                gc.setFill(Color.color(.5,.5,.5,0));
                break;
            case(9): // Toggle Block Source
                if(!onoff) {
                    gc.setFill(Color.rgb(26,120,211));
                }else {
                    gc.setFill(Color.rgb(117,2,160));
                }
                gc.setStroke(Color.BLACK);
                gc.fillRect(hitBox.x, hitBox.y, tileSize, tileSize);
                gc.strokeRect(hitBox.x, hitBox.y, tileSize*.99, tileSize*.99);
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, (int)(tileSize/2.5)));
                gc.setTextAlign(TextAlignment.CENTER);
//                System.out.println(gc.getTextBaseline().compareTo(V));
                gc.setTextBaseline(VPos.BOTTOM);
                if(!onoff) {
                    gc.fillText("ON", hitBox.x + (hitBox.w / 2), hitBox.y + ((int)(tileSize/1.25)) + ((gc.getCanvas().getHeight() / 150f)));
                }else {
                    gc.fillText("OFF", hitBox.x + (hitBox.w / 2), hitBox.y + ((int)(tileSize/1.25)) + ((gc.getCanvas().getHeight() / 150f)));
                }
                gc.setTextBaseline(VPos.BASELINE);
                gc.setFill(Color.color(.5,.5,.5,0));
                break;
            case(10): // Toggle Block Purple
                if(onoff){
                    lightIntensity=Color.BLACK;
                    gc.setFill(Color.rgb(117,2,160));
                    gc.setStroke(Color.BLACK);
                    gc.strokeRect(hitBox.x, hitBox.y, tileSize*.99, tileSize*.99);
                    gc.fillRect(hitBox.x, hitBox.y, tileSize, tileSize);
                    gc.setFill(Color.color(.5,.5,.5,0));
                    break;
                }
                gc.setStroke(Color.rgb(117,2,160));
                gc.strokeRect(hitBox.x, hitBox.y, tileSize*.99, tileSize*.99);
                gc.setFill(Color.color(.5,.5,.5,0));
                break;
            case(11): // Toggle Block Blue
                if(!onoff){
                    lightIntensity=Color.BLACK;
                    gc.setFill(Color.rgb(26,120,211));
                    gc.setStroke(Color.BLACK);
                    gc.strokeRect(hitBox.x, hitBox.y, tileSize*.99, tileSize*.99);
                    gc.fillRect(hitBox.x, hitBox.y, tileSize, tileSize);
                    gc.setFill(Color.color(.5,.5,.5,0));
                    break;
                }
                gc.setStroke(Color.rgb(26,120,211));
                gc.strokeRect(hitBox.x, hitBox.y, tileSize*.99, tileSize*.99);
                gc.setFill(Color.color(.5,.5,.5,0));
                break;
            case(13): // Shallow Water
                gc.setFill(Color.BLUE);
                break;
            case(14): // Generator Tile
                gc.setFill(Color.MEDIUMPURPLE);
                gc.fillRoundRect(hitBox.x, hitBox.y, tileSize, tileSize, 20, 90);
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, (int)(tileSize/3)));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText("Gen",hitBox.x+(hitBox.w/2),hitBox.y+(hitBox.h/2)+((gc.getCanvas().getHeight()/150f)));
                gc.setFill(Color.color(.5,.5,.5,0));
                break;
            case(15):// Trigger Tile
                gc.setFill(Color.MEDIUMPURPLE);
                gc.fillRoundRect(hitBox.x, hitBox.y, tileSize, tileSize, 20, 90);
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, (int)(tileSize/3)));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText("Trig",hitBox.x+(hitBox.w/2),hitBox.y+(hitBox.h/2)+((gc.getCanvas().getHeight()/150f)));
                gc.setFill(Color.color(.5,.5,.5,0));
                break;
            case(16): //Tinted Glass
                gc.setFill(tint);
                break;
            case(17): // OR Gate
                gc.setFill(Color.BLUEVIOLET);
                gc.fillRect(hitBox.x, hitBox.y, tileSize, tileSize);
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, (int)(tileSize/3)));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText("OR",hitBox.x+(hitBox.w/2),hitBox.y+(hitBox.h/2)+((gc.getCanvas().getHeight()/150f)));
                boolean anyOn = false;
                if(inputs!=null)
                    for(int i=0;i<inputs.length;i++){
                        Tile t = l.t[(int)inputs[i].x][(int)inputs[i].y];
                        if(t.state) {
                            gc.setFill(Color.RED);
                            anyOn = true;
                        }
                        else
                            gc.setFill(Color.BLACK);

                        gc.fillOval(hitBox.x,hitBox.y+((i+1)*(tileSize/(inputs.length+1)))-(tileSize/20),tileSize/10,tileSize/10);
                    }
                state=anyOn;
                if(anyOn) {
                    gc.setFill(Color.RED);
                }
                else
                    gc.setFill(Color.BLACK);
                gc.fillOval(hitBox.x+hitBox.w-(tileSize/10),hitBox.y+(tileSize/2)-(tileSize/20),tileSize/10,tileSize/10);
                gc.setFill(Color.color(.5,.5,.5,0));
                break;
            case(18): // AND Gate
                gc.setFill(Color.BLUEVIOLET);
                gc.fillRect(hitBox.x, hitBox.y, tileSize, tileSize);
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, (int)(tileSize/3)));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText("AND",hitBox.x+(hitBox.w/2),hitBox.y+(hitBox.h/2)+((gc.getCanvas().getHeight()/150f)));
                int numberOn = 0;
                if(inputs!=null) {
                    for (int i = 0; i < inputs.length; i++) {
                        Tile t = l.t[(int)inputs[i].x][(int)inputs[i].y];
                        if (t.state) {
                            gc.setFill(Color.RED);
                            numberOn++;
                        } else
                            gc.setFill(Color.BLACK);

                        gc.fillOval(hitBox.x, hitBox.y + ((i + 1) * (tileSize / (inputs.length + 1))) - (tileSize / 20), tileSize / 10, tileSize / 10);
                    }
                    state = numberOn == inputs.length;
                }
                if(state) {
                    gc.setFill(Color.RED);
                }
                else
                    gc.setFill(Color.BLACK);
                gc.fillOval(hitBox.x+hitBox.w-(tileSize/10),hitBox.y+(tileSize/2)-(tileSize/20),tileSize/10,tileSize/10);
                gc.setFill(Color.color(.5,.5,.5,0));
                break;
            case(19): // NOR Gate
                gc.setFill(Color.BLUEVIOLET);
                gc.fillRect(hitBox.x, hitBox.y, tileSize, tileSize);
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, (int)(tileSize/3)));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText("NOR",hitBox.x+(hitBox.w/2),hitBox.y+(hitBox.h/2)+((gc.getCanvas().getHeight()/150f)));
                anyOn = false;
                if(inputs!=null)
                    for(int i=0;i<inputs.length;i++){
                        Tile t = l.t[(int)inputs[i].x][(int)inputs[i].y];
                        if(t.state) {
                            gc.setFill(Color.RED);
                            anyOn = true;
                        }
                        else
                            gc.setFill(Color.BLACK);

                        gc.fillOval(hitBox.x,hitBox.y+((i+1)*(tileSize/(inputs.length+1)))-(tileSize/20),tileSize/10,tileSize/10);
                    }
                state=!anyOn;
                if(state) {
                    gc.setFill(Color.RED);
                }
                else
                    gc.setFill(Color.BLACK);
                gc.fillOval(hitBox.x+hitBox.w-(tileSize/10),hitBox.y+(tileSize/2)-(tileSize/20),tileSize/10,tileSize/10);
                gc.setFill(Color.color(.5,.5,.5,0));
                break;
            case(20): // Button
                if(state) {
                    gc.setFill(Color.RED);
                    gc.fillRect(hitBox.x+(tileSize/10), hitBox.y+((tileSize/6)*4), tileSize-(tileSize/5), tileSize/5);
                }
                else {
                    gc.setFill(Color.RED);
                    gc.fillRect(hitBox.x+(tileSize/10), hitBox.y+((tileSize/8)*4), tileSize-(tileSize/5), tileSize/3);
                    gc.setFill(Color.BLACK);
                }
                gc.fillOval(hitBox.x+hitBox.w-(tileSize/10),hitBox.y+(tileSize/2)-(tileSize/20),tileSize/10,tileSize/10);
                gc.setFill(Color.BLACK);
                gc.fillRect(hitBox.x, hitBox.y+((tileSize/5)*4), tileSize, tileSize/5);
                gc.setFill(Color.color(.5,.5,.5,0));
                inputs = new Vector[0];
                break;
            case(21): // Light
                if(state)
                    gc.setFill(Color.LIGHTGOLDENRODYELLOW);
                else
                    gc.setFill(Color.SANDYBROWN);
                gc.fillRect(hitBox.x, hitBox.y, tileSize, tileSize);
                anyOn = false;
                if(inputs!=null)
                    for(int i=0;i<inputs.length;i++){
                        Tile t = l.t[(int)inputs[i].x][(int)inputs[i].y];
                        if(t.state) {
                            gc.setFill(Color.RED);
                            anyOn = true;
                        }
                        else
                            gc.setFill(Color.BLACK);

                        gc.fillOval(hitBox.x,hitBox.y+((i+1)*(tileSize/(inputs.length+1)))-(tileSize/20),tileSize/10,tileSize/10);
                    }
                state=anyOn;
                if(state)
                    lightIntensity=Color.LIGHTGOLDENRODYELLOW;
                else
                    lightIntensity=Color.BLACK;
                gc.setFill(Color.color(.5,.5,.5,0));
                break;
            case(22): // Door
                if(state)
                    gc.setFill(Color.BLUE);
                else
                    gc.setFill(Color.SANDYBROWN);
                gc.fillRect(hitBox.x, hitBox.y, tileSize, tileSize*((state)?3:1));
                anyOn = false;
                if(inputs!=null)
                    for(int i=0;i<inputs.length;i++){
                        Tile t = l.t[(int)inputs[i].x][(int)inputs[i].y];
                        if(t.state) {
                            gc.setFill(Color.RED);
                            anyOn = true;
                        }
                        else
                            gc.setFill(Color.BLACK);

                        gc.fillOval(hitBox.x,hitBox.y+((i+1)*(tileSize/(inputs.length+1)))-(tileSize/20),tileSize/10,tileSize/10);
                    }
                state=anyOn;
                gc.setFill(Color.color(.5,.5,.5,0));
                break;
        }
        Color c = ((Color)gc.getFill());
        c = Color.color(c.getRed(),c.getGreen(),c.getBlue(),transparency*c.getOpacity());
        if(id!=6 && id!=13)
            c = Color.color(Math.min(c.getRed()+lightIntensity.getRed(),1),Math.min(c.getGreen()+lightIntensity.getGreen(),1),Math.min(c.getBlue()+lightIntensity.getBlue(),1),Math.max(lightIntensity.getBrightness(),c.getOpacity()));
        else
            c = c.interpolate(lightIntensity,0.7);
        gc.setFill(c);
        gc.setStroke(Color.WHITE);
        gc.fillRect(hitBox.x, hitBox.y, tileSize, tileSize);
        if(id == 3){
            lightIntensity=Color.rgb(255,0,0);
        }
        if(id == 4){
            lightIntensity=Color.rgb(27,222,34);
        }
//        gc.strokeRect(hitBox.x+hitBox.w/2, hitBox.y+hitBox.w/2, tileSize, tileSize);
//        rotate(angle);
//        translate(-hitBox.w/2, -hitBox.w/2);
//        PVector index = new PVector(15, 7);
//        switch(this.id) {
//            case(0):
//                break;
//            case(1):
//                fill(0);
//      /*
//       0 - tl
//       1 - t
//       2 - tr
//       3 - r
//       4 - br
//       5 - b
//       6 - bl
//       7 - l
//       8 - Lava Left
//       9 - Lava Right
//       10 - Water Left
//       11 - Water Right
//       */
//                if (surrounded.length>0) {
//                    if (surrounded.length>8)
//                        if (surrounded[8]|surrounded[10]) {
//                            if (surrounded[8]) {
//                                if (surrounded[1]) image(tileSet.get(0, 7*32, 16, 32), 0, 0, hitBox.w/2, hitBox.h);
//                                else if (frameCount%12<4) image(tileSet.get(1*32, 6*32, 16, 32), 0, 0, hitBox.w/2, hitBox.h);
//                                else if (frameCount%12<8) image(tileSet.get(2*32, 6*32, 16, 32), 0, 0, hitBox.w/2, hitBox.h);
//                                else  image(tileSet.get(3*32, 6*32, 16, 32), 0, 0, hitBox.w/2, hitBox.h);
//                            }
//                            if (surrounded[10]) {
//                                if (surrounded[1]) image(tileSet.get(0, 6*32, 16, 32), 0, 0, hitBox.w/2, hitBox.h);
//                                else if (frameCount%12<4) image(tileSet.get(1*32, 5*32, 16, 32), 0, 0, hitBox.w/2, hitBox.h);
//                                else if (frameCount%12<8) image(tileSet.get(2*32, 5*32, 16, 32), 0, 0, hitBox.w/2, hitBox.h);
//                                else  image(tileSet.get(3*32, 5*32, 16, 32), 0, 0, hitBox.w/2, hitBox.h);
//                            }
//                        }
//                    if (surrounded.length>8)
//                        if (surrounded[9]|surrounded[11]) {
//                            if (surrounded[9]) {
//                                if (surrounded[1]) image(tileSet.get(16, 7*32, 16, 32), hitBox.w/2, 0, hitBox.w/2, hitBox.h);
//                                else if (frameCount%12<4) image(tileSet.get(1*32 + 16, 6*32, 16, 32), hitBox.w/2, 0, hitBox.w/2, hitBox.h);
//                                else if (frameCount%12<8) image(tileSet.get(2*32 + 16, 6*32, 16, 32), hitBox.w/2, 0, hitBox.w/2, hitBox.h);
//                                else  image(tileSet.get(3*32 + 16, 6*32, 16, 32), hitBox.w/2, 0, hitBox.w/2, hitBox.h);
//                            }
//                            if (surrounded[11]) {
//                                if (surrounded[1]) image(tileSet.get(16, 6*32, 16, 32), hitBox.w/2, 0, hitBox.w/2, hitBox.w-hitBox.h);
//                                else if (frameCount%12<4) image(tileSet.get(1*32 + 16, 5*32, 16, 32), hitBox.w/2, 0, hitBox.w/2, hitBox.h);
//                                else if (frameCount%12<8) image(tileSet.get(2*32 + 16, 5*32, 16, 32), hitBox.w/2, 0, hitBox.w/2, hitBox.h);
//                                else  image(tileSet.get(3*32 + 16, 5*32, 16, 32), hitBox.w/2, 0, hitBox.w/2, hitBox.h);
//                            }
//                        }
//                    if (!surrounded[1]&&!surrounded[7]&&surrounded[3]&&surrounded[5]) index=new PVector(0, 0);
//                    if (!surrounded[1]&&surrounded[7]&&surrounded[3]&&surrounded[5]) index=new PVector(1, 0);
//                    if (!surrounded[1]&&surrounded[7]&&!surrounded[3]&&surrounded[5]) index=new PVector(2, 0);
//                    if (surrounded[1]&&!surrounded[7]&&surrounded[3]&&surrounded[5]) index=new PVector(0, 1);
//                    if (surrounded[1]&&surrounded[7]&&surrounded[3]&&surrounded[5]) index=new PVector(1, 1);
//                    if (surrounded[1]&&surrounded[7]&&!surrounded[3]&&surrounded[5]) index=new PVector(2, 1);
//                    if (surrounded[1]&&!surrounded[7]&&surrounded[3]&&!surrounded[5]) index=new PVector(0, 2);
//                    if (surrounded[1]&&surrounded[7]&&surrounded[3]&&!surrounded[5]) index=new PVector(1, 2);
//                    if (surrounded[1]&&surrounded[7]&&!surrounded[3]&&!surrounded[5]) index=new PVector(2, 2);
//                    if (!surrounded[1]&&!surrounded[7]&&surrounded[3]&&!surrounded[5]) index=new PVector(0, 3);
//                    if (!surrounded[1]&&!surrounded[7]&&!surrounded[3]&&surrounded[5]) index=new PVector(1, 3);
//                    if (!surrounded[1]&&surrounded[7]&&!surrounded[3]&&!surrounded[5]) index=new PVector(2, 3);
//                    if (surrounded[1]&&!surrounded[7]&&!surrounded[3]&&!surrounded[5]) index=new PVector(1, 4);
//                    if (!surrounded[1]&&!surrounded[7]&&!surrounded[3]&&!surrounded[5]) index=new PVector(2, 4);
//                    if (!surrounded[1]&&surrounded[7]&&surrounded[3]&&!surrounded[5]) index=new PVector(0, 4);
//                    if (surrounded[1]&&!surrounded[7]&&!surrounded[3]&&surrounded[5]) index=new PVector(0, 5);
//
//                    PImage texture = tileSet.get((int)index.x*32, (int)index.y*32, 32, 32);
//                    image(texture, 0, 0, hitBox.w, hitBox.h);
//                    textAlign(CENTER, CENTER);
//
//                    if (!surrounded[0] && surrounded[1] && surrounded[7]) image(tileSet.get(4*32, 3*32, 32, 32), 0, 0, hitBox.w, hitBox.h);
//                    if (!surrounded[2] && surrounded[1] && surrounded[3]) image(tileSet.get(3*32, 3*32, 32, 32), 0, 0, hitBox.w, hitBox.h);
//                    if (!surrounded[4] && surrounded[3] && surrounded[5]) image(tileSet.get(3*32, 2*32, 32, 32), 0, 0, hitBox.w, hitBox.h);
//                    if (!surrounded[6] && surrounded[5] && surrounded[7]) image(tileSet.get(4*32, 2*32, 32, 32), 0, 0, hitBox.w, hitBox.h);
//                } else {
//                    rect(0, 0, hitBox.w, hitBox.w);
//                }
//                fill(100);
//                noStroke();
//                int num = 16;
//                break;
//            case(2):
//                fill(255, 255, 0);
//                rect(0, 0, hitBox.w, hitBox.w);
//                break;
//            case(3):
//                if (surrounded[1]) image(tileSet.get(0, 7*32, 32, 32), 0, 0, hitBox.w, hitBox.h);
//                else if (frameCount%12<4) image(tileSet.get(1*32, 6*32, 32, 32), 0, 0, hitBox.w, hitBox.h);
//                else if (frameCount%12<8) image(tileSet.get(2*32, 6*32, 32, 32), 0, 0, hitBox.w, hitBox.h);
//                else  image(tileSet.get(3*32, 6*32, 32, 32), 0, 0, hitBox.w, hitBox.h);
//                break;
//            case(4):
//                fill(#1BDE22);
//                rect(0, 0, hitBox.w, hitBox.w);
//                break;
//            case(5):
//                fill(#1B67DE);
//                rect(0, 0, hitBox.w, hitBox.w);
//                break;
//            case(6):
//                if (surrounded[1]) image(tileSet.get(0, 6*32, 32, 32), 0, 0, hitBox.w, hitBox.h);
//                else if (frameCount%12<4) image(tileSet.get(1*32, 5*32, 32, 32), 0, 0, hitBox.w, hitBox.h);
//                else if (frameCount%12<8) image(tileSet.get(2*32, 5*32, 32, 32), 0, 0, hitBox.w, hitBox.h);
//                else  image(tileSet.get(3*32, 5*32, 32, 32), 0, 0, hitBox.w, hitBox.h);
//                break;
//            case(7):
//                if (GameMode!=Mode.LevelEditor && GameMode!=Mode.EditorInventory && GameMode!=Mode.EditTile && GameMode!=Mode.LevelEditorPaused) {
//                    pop();
//                    return;
//                }
//                fill(#946E9D);
//                rect(0, 0, hitBox.w, hitBox.w);
//                fill(0);
//                textAlign(CENTER, CENTER);
//                textSize(hitBox.w/2);
//                text("Strt", hitBox.w/2, hitBox.w/2);
//                break;
//            case(8):
//                image(tileSet.get(96, 0, 32, 32), 0, -hitBox.h, hitBox.w, hitBox.h);
//                if (p.checkpoint && (int)respawnPos.x==x && (int)respawnPos.y==y) image(tileSet.get(128, 32, 32, 32), 0, 0, hitBox.w, hitBox.h);
//                else image(tileSet.get(96, 32, 32, 32), 0, 0, hitBox.w, hitBox.h);
//                if (surrounded[5] && (!surrounded[4] || !surrounded[6])) image(tileSet.get(128, 0, 32, 32), 0, hitBox.h, hitBox.w, hitBox.h);
//                break;
//            case(9):
//                stroke(0);
//                strokeWeight(3);
//                if (onoffstate) {
//                    fill(#7502A0);
//                    rect(0, 0, hitBox.w, hitBox.w);
//                    fill(0);
//                    textAlign(CENTER, CENTER);
//                    textSize(hitBox.w/2);
//                    text("ON", hitBox.w/2, hitBox.w/2);
//                } else {
//                    fill(#1A78D3);
//                    rect(0, 0, hitBox.w, hitBox.w);
//                    fill(0);
//                    textAlign(CENTER, CENTER);
//                    textSize(hitBox.w/2);
//                    text("OFF", hitBox.w/2, hitBox.w/2);
//                }
//                break;
//            case(10):
//                stroke(0);
//                strokeWeight(3);
//                if (onoffstate) {
//                    fill(#7502A0);
//                    rect(0, 0, hitBox.w, hitBox.w);
//                } else {
//                    stroke(#7502A0);
//                    fill(0, 0);
//                    rect(0, 0, hitBox.w, hitBox.w);
//                }
//                break;
//            case(11):
//                stroke(0);
//                strokeWeight(3);
//                if (!onoffstate) {
//                    fill(#1A78D3);
//                    rect(0, 0, hitBox.w, hitBox.w);
//                } else {
//                    stroke(#1A78D3);
//                    fill(0, 0);
//                    rect(0, 0, hitBox.w, hitBox.w);
//                }
//                break;
//            case(12):
//                textAlign(LEFT, CENTER);
//                if (text.equalsIgnoreCase("")) {
//                    fill(100);
//                    textSize(hitBox.w/3);
//                    text("ABC...", 0, hitBox.w/2);
//                    break;
//                }
//                fill(0);
//                textSize(tileSize/3);
//                text(this.text, 0, hitBox.w/2);
//                break;
//            case(13):
//                fill(#0A67FF);
//                if (surrounded.length>0) {
//                    if (!surrounded[1]) {
//                        beginShape();
//                        vertex(hitBox.w, hitBox.w);
//                        vertex(0, hitBox.w);
//                        for (int i=0; i<31; i+=1) {
//                            float off=0;
//                            vertex(
//                                    map(i, 0, 30, 0, hitBox.w), ((off+ sin((hitBox.x+map(i, 0, 30, 0, hitBox.w))/5+frameCount/5f)+1)*5)+((hitBox.w/3)*2));
//                        }
//                        endShape();
//                        break;
//                    }
//                }
//                rect(0, 0, hitBox.w, hitBox.w);
//                break;
//            case(14):
//                if (limit==0) spawned=-1;
//                if (timer+spawnDelay<System.currentTimeMillis() && GameMode == Mode.MainGame && spawned < limit) {
//                    Enemy e = new Enemy(0, 0);
//                    e.spawn(x*tileSize, y*tileSize);
//                    enemies.add(e);
//                    timer=System.currentTimeMillis();
//                    spawned++;
//                }
//                if (GameMode!=Mode.LevelEditor && GameMode!=Mode.EditorInventory && GameMode!=Mode.EditTile && GameMode!=Mode.LevelEditorPaused) {
//                    pop();
//                    return;
//                }
//                fill(#946E9D);
//                rect(0, 0, hitBox.w, hitBox.w);
//                fill(0);
//                textAlign(CENTER, CENTER);
//                textSize(hitBox.w/2);
//                text("Gen", hitBox.w/2, hitBox.w/2);
//                break;
//            case(15):
//                if (limit==0) spawned=-1;
//                if (timer+spawnDelay<System.currentTimeMillis() && GameMode == Mode.MainGame && spawned < limit) {
//                    Enemy e = new Enemy(0, 0);
//                    e.spawn(x*tileSize, y*tileSize);
//                    enemies.add(e);
//                    timer=System.currentTimeMillis();
//                    spawned++;
//                }
//                if (GameMode!=Mode.LevelEditor && GameMode!=Mode.EditorInventory && GameMode!=Mode.EditTile && GameMode!=Mode.LevelEditorPaused) {
//                    pop();
//                    return;
//                }
//                fill(#946E9D);
//                rect(0, 0, hitBox.w, hitBox.w);
//                fill(0);
//                textAlign(CENTER, CENTER);
//                textSize(hitBox.w/2);
//                text("Trig", hitBox.w/2, hitBox.w/2);
//                break;
//        }
//        //fill(0);
//        //textAlign(CENTER, CENTER);
//        //textSize(hitBox.w/2);
//        //text(this.id, hitBox.w/2, hitBox.w/2);
//        pop();
    }

    public Tile copy(){
        Tile t = new Tile(id);
        t.lightIntensity=lightIntensity;
        t.hitBox=hitBox;
        t.text=text;
        t.tint=tint;
        t.alignment=alignment;
        t.angle=angle;
        t.spawnDelay=spawnDelay;
        t.limit=limit;
        t.timer=timer;
        return t;
    }
}