package com.metype.game;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class Tile {
    public int id = 0;
    public String text = "";
    public Color tint = Color.hsb(120, .57, 1, 0.35);
    public int alignment = 1;
    public Vector[] inputs = new Vector[0];
    Rect hitBox;
    float angle = 0;
    long timer = 0;
    int limit = 3;
    int spawned = 0;
    int spawnDelay = 1000;
    Light lightCone = null;
    Color lightIntensity = Color.color(0, 0, 0.01);
    boolean state = false;
    boolean leftShaft = false;
    boolean rightShaft = false;

    public Tile(int id) {
        this.id = id;
        if (id == 3) {
            lightIntensity = Color.rgb(255, 0, 0);
        }
    }

    public void setHitBox(Rect r) {
        this.hitBox = r;
    }

    public boolean isCollidable(boolean onoffstate) {
        int[] colliding = {1, 2, 4, 5, 9, 1, 3, 6, 2, 8, 16, 20, 22, 27};
        if (onoffstate) {
            colliding[5] = 10;
        } else {
            colliding[5] = 11;
        }
        return Index.findIndex(colliding, id) != -1;
    }

    public boolean isSolid(boolean onoffstate) {
        int[] colliding = {1, 2, 4, 5, 9, 1, 3, 16, 20, 22, 27};
        if (onoffstate) {
            colliding[5] = 10;
        } else {
            colliding[5] = 11;
        }
        return Index.findIndex(colliding, id) != -1;
    }

    public boolean isVisiblySolid(boolean onoffstate) {
        int[] colliding = {1, 4, 5, 9, 21, 1};
        if (onoffstate) {
            colliding[5] = 10;
        } else {
            colliding[5] = 11;
        }
        return Index.findIndex(colliding, id) != -1;
    }

    public boolean isPhysicallySolid() {
        int[] colliding = {1, 2, 3, 4, 5, 9, 16, 22};
        return Index.findIndex(colliding, id) != -1;
    }


    public double lightPermablility(boolean onoffstate) {
        if (id == 16) return 1;
        if (isSolid(onoffstate)) return 0;
        switch (id) {
            case (6):
            case (13):
            case (12):
                return .94;
            default:
                return .98;
        }
    }

    public void render(GraphicsContext gc, double tileSize, Boolean... surrounded) {
        this.render(gc, tileSize, 1, null, Color.color(0, 0, 0), surrounded);
    }

    public void render(GraphicsContext gc, double tileSize, double transparency, Boolean... surrounded) {
        this.render(gc, tileSize, transparency, null, Color.color(0, 0, 0), surrounded);
    }

    public void render(GraphicsContext gc, double tileSize, double transparency, Light[] lightCones, Boolean... surrounded) {
        this.render(gc, tileSize, transparency, lightCones, Color.color(0, 0, 0), surrounded);
    }

    public void render(GraphicsContext gc, double tileSize, double transparency, Light[] lightCones, Color brighter, Boolean... surrounded) {
        this.render(gc, null, tileSize, transparency, lightCones, false, false, brighter, null, 0, 0, 0, surrounded);
    }

    public void render(GraphicsContext gc, Image tileSet, double tileSize, double transparency, Light[] lightCones, boolean renderText, boolean onoff, Color brighter, Level l, int type, int x, int y, Boolean... surrounded) {
        if (renderText) {
            if (id == 12) {
                switch (alignment) {
                    case (0):
                        gc.setTextAlign(TextAlignment.LEFT);
                        break;
                    case (1):
                        gc.setTextAlign(TextAlignment.CENTER);
                        break;
                    case (2):
                        gc.setTextAlign(TextAlignment.RIGHT);
                        break;
                    case (3):
                        gc.setTextAlign(TextAlignment.JUSTIFY);
                        break;
                }
                if (text.equalsIgnoreCase("")) {
                    gc.setFill(Color.DARKGRAY);
                    gc.setStroke(Color.BLACK);
                    gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, (int) (gc.getCanvas().getHeight() / 50f)));
                    gc.fillText("ABC...", hitBox.x + (hitBox.w / 2), hitBox.y + (hitBox.h / 2) + (gc.getCanvas().getHeight() / 150f));
                    gc.setFill(Color.color(.5, .5, .5, 0));
                    return;
                }
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, (int) (gc.getCanvas().getHeight() / 50f)));
                gc.fillText(text, hitBox.x + (hitBox.w / 2), hitBox.y + (hitBox.h / 2) + (gc.getCanvas().getHeight() / 150f));
            }
            return;
        }
        int offset = (16 * type * 2);
        gc.setImageSmoothing(false);
        gc.drawImage(tileSet, 656 + offset, 32, 16, 16, hitBox.x, hitBox.y, tileSize, tileSize);
        if (rightShaft)
            gc.drawImage(tileSet, 743, 128, 7, 16, hitBox.x + (tileSize * (7 / 16f)), hitBox.y, tileSize * (7 / 16f), tileSize);
        if (leftShaft)
            gc.drawImage(tileSet, 768, 128, 16, 16, hitBox.x, hitBox.y, tileSize, tileSize);
        if (l != null)
            if (x < l.t[0].length && y > 0 && !this.isPhysicallySolid()) {
                rightShaft = l.t[x][y - 1].rightShaft || l.t[x + 1][y].id == 21;
            } else {
                rightShaft = false;
            }
        if (l != null)
            if (x > 0 && y > 0 && !this.isPhysicallySolid()) {
                leftShaft = l.t[x][y - 1].leftShaft || l.t[x - 1][y].id == 21;
            } else {
                leftShaft = false;
            }
        switch (id) {
            case (0): // Air
            case (12):// Text Tile
                gc.setFill(Color.color(.5, .5, .5, 0));
                break;
            case (1): // Solid Wall
                gc.setFill(Color.color(0, 0, 0, 0));
                if (surrounded.length >= 8) {
                    offset = (16 * type * 8);
                    if (surrounded[1] && surrounded[3] && surrounded[5] && surrounded[7])
                        gc.drawImage(tileSet, 16 + offset, 48, 16, 16, hitBox.x, hitBox.y, tileSize, tileSize);
                    if (!surrounded[1] && !surrounded[3] && !surrounded[5] && !surrounded[7])
                        gc.drawImage(tileSet, 96 + offset, 176, 16, 16, hitBox.x, hitBox.y, tileSize, tileSize);
                    if (!surrounded[1] && surrounded[3] && !surrounded[5] && !surrounded[7])
                        gc.drawImage(tileSet, 32 + offset, 176, 16, 16, hitBox.x, hitBox.y, tileSize, tileSize);
                    if (!surrounded[1] && !surrounded[3] && surrounded[5] && !surrounded[7])
                        gc.drawImage(tileSet, 48 + offset, 208, 16, 16, hitBox.x, hitBox.y, tileSize, tileSize);
                    if (!surrounded[1] && surrounded[3] && surrounded[5] && !surrounded[7])
                        gc.drawImage(tileSet, 80 + offset, 208, 16, 16, hitBox.x, hitBox.y, tileSize, tileSize);
                    if (!surrounded[1] && surrounded[3] && !surrounded[5] && surrounded[7])
                        gc.drawImage(tileSet, 48 + offset, 176, 16, 16, hitBox.x, hitBox.y, tileSize, tileSize);
                    if (!surrounded[1] && !surrounded[3] && !surrounded[5] && surrounded[7])
                        gc.drawImage(tileSet, 64 + offset, 176, 16, 16, hitBox.x, hitBox.y, tileSize, tileSize);
                    if (!surrounded[1] && !surrounded[3] && surrounded[5] && surrounded[7])
                        gc.drawImage(tileSet, 96 + offset, 208, 16, 16, hitBox.x, hitBox.y, tileSize, tileSize);
                    if (!surrounded[1] && surrounded[3] && surrounded[5] && surrounded[7])
                        gc.drawImage(tileSet, 48 + offset, 96, 16, 16, hitBox.x, hitBox.y, tileSize, tileSize);
                    if (surrounded[1] && surrounded[3] && !surrounded[5] && surrounded[7])
                        gc.drawImage(tileSet, 48 + offset, 112, 16, 16, hitBox.x, hitBox.y, tileSize, tileSize);
                    if (surrounded[1] && surrounded[3] && !surrounded[5] && !surrounded[7])
                        gc.drawImage(tileSet, 32 + offset, 112, 16, 16, hitBox.x, hitBox.y, tileSize, tileSize);
                    if (surrounded[1] && !surrounded[3] && !surrounded[5] && surrounded[7])
                        gc.drawImage(tileSet, 112 + offset, 112, 16, 16, hitBox.x, hitBox.y, tileSize, tileSize);
                    if (surrounded[1] && surrounded[3] && surrounded[5] && !surrounded[7])
                        gc.drawImage(tileSet, 64 + offset, 80, 16, 16, hitBox.x, hitBox.y, tileSize, tileSize);
                    if (surrounded[1] && !surrounded[3] && surrounded[5] && surrounded[7])
                        gc.drawImage(tileSet, 80 + offset, 80, 16, 16, hitBox.x, hitBox.y, tileSize, tileSize);
                    if (surrounded[1] && !surrounded[3] && surrounded[5] && !surrounded[7])
                        gc.drawImage(tileSet, 48 + offset, 224, 16, 16, hitBox.x, hitBox.y, tileSize, tileSize);
                    if (surrounded[1] && !surrounded[3] && !surrounded[5] && !surrounded[7])
                        gc.drawImage(tileSet, 48 + offset, 240, 16, 16, hitBox.x, hitBox.y, tileSize, tileSize);
                    if (surrounded[1] && surrounded[7] && !surrounded[0])
                        gc.drawImage(tileSet, 64, 96, 5, 5, hitBox.x, hitBox.y, tileSize * (5 / 16f), tileSize * (5 / 16f));
                    if (surrounded[1] && surrounded[3] && !surrounded[2])
                        gc.drawImage(tileSet, 91, 96, 5, 5, (hitBox.x + tileSize) - (tileSize * (5 / 16f)), hitBox.y, tileSize * (5 / 16f), tileSize * (5 / 16f));
                    if (surrounded[5] && surrounded[3] && !surrounded[4])
                        gc.drawImage(tileSet, 91, 127, 5, 5, (hitBox.x + tileSize) - (tileSize * (5 / 16f)), (hitBox.y + tileSize) - (tileSize * (5 / 16f)), tileSize * (5 / 16f), tileSize * (5 / 16f));
                    if (surrounded[5] && surrounded[7] && !surrounded[6])
                        gc.drawImage(tileSet, 64, 127, 5, 5, hitBox.x, (hitBox.y + tileSize) - (tileSize * (5 / 16f)), tileSize * (5 / 16f), tileSize * (5 / 16f));
                } else {
                    gc.drawImage(tileSet, 96, 176, 16, 16, hitBox.x, hitBox.y, tileSize, tileSize);
                }
                break;
            case (2): // End Tile
                gc.setFill(Color.YELLOW);
                break;
            case (3): // Lava Tile
                gc.setFill(Color.RED);
                break;
            case (4): // Bouncy Tile
                gc.setFill(Color.rgb(27, 222, 34));
                break;
            case (5): // Slippery Tile
                gc.setFill(Color.rgb(27, 103, 222));
                break;
            case (6): // Water
                gc.setFill(Color.color(0.25, 0.55, 0.75, 0.75));
                break;
            case (7): // Start Tile
                gc.setFill(Color.MEDIUMPURPLE);
                gc.fillRoundRect(hitBox.x, hitBox.y, tileSize, tileSize, 20, 90);
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, (int) (tileSize / 3)));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText("Start", hitBox.x + (hitBox.w / 2), hitBox.y + (hitBox.h / 2) + (gc.getCanvas().getHeight() / 150f));
                gc.setFill(Color.color(.5, .5, .5, 0));
                break;
            case (8): // Checkpoint
                gc.setFill(Color.MEDIUMPURPLE);
                gc.fillRoundRect(hitBox.x, hitBox.y, tileSize, tileSize, 20, 90);
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, (int) (tileSize / 3)));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText("Ckpt", hitBox.x + (hitBox.w / 2), hitBox.y + (hitBox.h / 2) + (gc.getCanvas().getHeight() / 150f));
                gc.setFill(Color.color(.5, .5, .5, 0));
                break;
            case (9): // Toggle Block Source
                if (!onoff) {
                    gc.setFill(Color.rgb(26, 120, 211));
                } else {
                    gc.setFill(Color.rgb(117, 2, 160));
                }
                gc.setStroke(Color.BLACK);
                gc.fillRect(hitBox.x, hitBox.y, tileSize, tileSize);
                gc.strokeRect(hitBox.x, hitBox.y, tileSize * .99, tileSize * .99);
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, (int) (tileSize / 2.5)));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.setTextBaseline(VPos.BOTTOM);
                if (!onoff) {
                    gc.fillText("ON", hitBox.x + (hitBox.w / 2), hitBox.y + ((int) (tileSize / 1.25)) + (gc.getCanvas().getHeight() / 150f));
                } else {
                    gc.fillText("OFF", hitBox.x + (hitBox.w / 2), hitBox.y + ((int) (tileSize / 1.25)) + (gc.getCanvas().getHeight() / 150f));
                }
                gc.setTextBaseline(VPos.BASELINE);
                gc.setFill(Color.color(.5, .5, .5, 0));
                break;
            case (10): // Toggle Block Purple
                if (onoff) {
                    lightIntensity = Color.BLACK;
                    gc.setFill(Color.rgb(117, 2, 160));
                    gc.setStroke(Color.BLACK);
                    gc.strokeRect(hitBox.x, hitBox.y, tileSize * .99, tileSize * .99);
                    gc.fillRect(hitBox.x, hitBox.y, tileSize, tileSize);
                    gc.setFill(Color.color(.5, .5, .5, 0));
                    break;
                }
                gc.setStroke(Color.rgb(117, 2, 160));
                gc.strokeRect(hitBox.x, hitBox.y, tileSize * .99, tileSize * .99);
                gc.setFill(Color.color(.5, .5, .5, 0));
                break;
            case (11): // Toggle Block Blue
                if (!onoff) {
                    lightIntensity = Color.BLACK;
                    gc.setFill(Color.rgb(26, 120, 211));
                    gc.setStroke(Color.BLACK);
                    gc.strokeRect(hitBox.x, hitBox.y, tileSize * .99, tileSize * .99);
                    gc.fillRect(hitBox.x, hitBox.y, tileSize, tileSize);
                    gc.setFill(Color.color(.5, .5, .5, 0));
                    break;
                }
                gc.setStroke(Color.rgb(26, 120, 211));
                gc.strokeRect(hitBox.x, hitBox.y, tileSize * .99, tileSize * .99);
                gc.setFill(Color.color(.5, .5, .5, 0));
                break;
            case (13): // Shallow Water
                gc.setFill(Color.BLUE);
                break;
            case (14): // Generator Tile
                gc.setFill(Color.MEDIUMPURPLE);
                gc.fillRoundRect(hitBox.x, hitBox.y, tileSize, tileSize, 20, 90);
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, (int) (tileSize / 3)));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText("Gen", hitBox.x + (hitBox.w / 2), hitBox.y + (hitBox.h / 2) + (gc.getCanvas().getHeight() / 150f));
                gc.setFill(Color.color(.5, .5, .5, 0));
                break;
            case (15):// Trigger Tile
                gc.setFill(Color.MEDIUMPURPLE);
                gc.fillRoundRect(hitBox.x, hitBox.y, tileSize, tileSize, 20, 90);
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, (int) (tileSize / 3)));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText("Trig", hitBox.x + (hitBox.w / 2), hitBox.y + (hitBox.h / 2) + (gc.getCanvas().getHeight() / 150f));
                gc.setFill(Color.color(.5, .5, .5, 0));
                break;
            case (16): //Tinted Glass
                gc.setFill(tint);
                break;
            case (17): // OR Gate
                gc.setFill(Color.BLUEVIOLET);
                gc.fillRect(hitBox.x, hitBox.y, tileSize, tileSize);
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, (int) (tileSize / 3)));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText("OR", hitBox.x + (hitBox.w / 2), hitBox.y + (hitBox.h / 2) + (gc.getCanvas().getHeight() / 150f));
                boolean anyOn = false;
                if (inputs != null)
                    for (int i = 0; i < inputs.length; i++) {
                        Tile t = l.t[(int) inputs[i].x][(int) inputs[i].y];
                        if (t.state) {
                            gc.setFill(Color.RED);
                            anyOn = true;
                        } else
                            gc.setFill(Color.BLACK);

                        gc.fillOval(hitBox.x, hitBox.y + ((i + 1) * (tileSize / (inputs.length + 1))) - (tileSize / 20), tileSize / 10, tileSize / 10);
                    }
                state = anyOn;
                if (anyOn) {
                    gc.setFill(Color.RED);
                } else
                    gc.setFill(Color.BLACK);
                gc.fillOval(hitBox.x + hitBox.w - (tileSize / 10), hitBox.y + (tileSize / 2) - (tileSize / 20), tileSize / 10, tileSize / 10);
                gc.setFill(Color.color(.5, .5, .5, 0));
                break;
            case (18): // AND Gate
                gc.setFill(Color.BLUEVIOLET);
                gc.fillRect(hitBox.x, hitBox.y, tileSize, tileSize);
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, (int) (tileSize / 3)));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText("AND", hitBox.x + (hitBox.w / 2), hitBox.y + (hitBox.h / 2) + (gc.getCanvas().getHeight() / 150f));
                int numberOn = 0;
                if (inputs != null) {
                    for (int i = 0; i < inputs.length; i++) {
                        Tile t = l.t[(int) inputs[i].x][(int) inputs[i].y];
                        if (t.state) {
                            gc.setFill(Color.RED);
                            numberOn++;
                        } else
                            gc.setFill(Color.BLACK);

                        gc.fillOval(hitBox.x, hitBox.y + ((i + 1) * (tileSize / (inputs.length + 1))) - (tileSize / 20), tileSize / 10, tileSize / 10);
                    }
                    state = numberOn == inputs.length;
                }
                if (state) {
                    gc.setFill(Color.RED);
                } else
                    gc.setFill(Color.BLACK);
                gc.fillOval(hitBox.x + hitBox.w - (tileSize / 10), hitBox.y + (tileSize / 2) - (tileSize / 20), tileSize / 10, tileSize / 10);
                gc.setFill(Color.color(.5, .5, .5, 0));
                break;
            case (19): // NOR Gate
                gc.setFill(Color.BLUEVIOLET);
                gc.fillRect(hitBox.x, hitBox.y, tileSize, tileSize);
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, (int) (tileSize / 3)));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText("NOR", hitBox.x + (hitBox.w / 2), hitBox.y + (hitBox.h / 2) + (gc.getCanvas().getHeight() / 150f));
                anyOn = false;
                if (inputs != null)
                    for (int i = 0; i < inputs.length; i++) {
                        Tile t = l.t[(int) inputs[i].x][(int) inputs[i].y];
                        if (t.state) {
                            gc.setFill(Color.RED);
                            anyOn = true;
                        } else
                            gc.setFill(Color.BLACK);

                        gc.fillOval(hitBox.x, hitBox.y + ((i + 1) * (tileSize / (inputs.length + 1))) - (tileSize / 20), tileSize / 10, tileSize / 10);
                    }
                state = !anyOn;
                if (state) {
                    gc.setFill(Color.RED);
                } else
                    gc.setFill(Color.BLACK);
                gc.fillOval(hitBox.x + hitBox.w - (tileSize / 10), hitBox.y + (tileSize / 2) - (tileSize / 20), tileSize / 10, tileSize / 10);
                gc.setFill(Color.color(.5, .5, .5, 0));
                break;
            case (20): // Button
                if (state) {
                    gc.setFill(Color.RED);
                    gc.fillRect(hitBox.x + (tileSize / 10), hitBox.y + ((tileSize / 6) * 4), tileSize - (tileSize / 5), tileSize / 5);
                } else {
                    gc.setFill(Color.RED);
                    gc.fillRect(hitBox.x + (tileSize / 10), hitBox.y + ((tileSize / 8) * 4), tileSize - (tileSize / 5), tileSize / 3);
                    gc.setFill(Color.BLACK);
                }
                gc.fillOval(hitBox.x + hitBox.w - (tileSize / 10), hitBox.y + (tileSize / 2) - (tileSize / 20), tileSize / 10, tileSize / 10);
                gc.setFill(Color.BLACK);
                gc.fillRect(hitBox.x, hitBox.y + ((tileSize / 5) * 4), tileSize, tileSize / 5);
                gc.setFill(Color.color(.5, .5, .5, 0));
                inputs = new Vector[0];
                break;
            case (21): // Light
                if (state)
                    gc.drawImage(tileSet, 832, 128, 16, 16, hitBox.x, hitBox.y, tileSize, tileSize);
                else
                    gc.drawImage(tileSet, 752, 128, 16, 16, hitBox.x, hitBox.y, tileSize, tileSize);
                anyOn = false;
                if (inputs != null)
                    for (int i = 0; i < inputs.length; i++) {
                        Tile t = l.t[(int) inputs[i].x][(int) inputs[i].y];
                        if (t.state) {
                            gc.setFill(Color.RED);
                            anyOn = true;
                        } else
                            gc.setFill(Color.BLACK);

                        gc.fillOval(hitBox.x, hitBox.y + ((i + 1) * (tileSize / (inputs.length + 1))) - (tileSize / 20), tileSize / 10, tileSize / 10);
                    }
                state = anyOn;
                if (state)
                    lightIntensity = Color.LIGHTGOLDENRODYELLOW;
                else
                    lightIntensity = Color.BLACK;
                lightIntensity = Color.hsb(lightIntensity.getHue(), lightIntensity.getSaturation(), lightIntensity.getBrightness() * .5);
                gc.setFill(Color.color(.5, .5, .5, 0));
                break;
            case (22): // Door
                if (state)
                    gc.setFill(Color.BLUE);
                else
                    gc.setFill(Color.SANDYBROWN);
                gc.fillRect(hitBox.x, hitBox.y, tileSize, tileSize * ((state) ? 3 : 1));
                anyOn = false;
                if (inputs != null)
                    for (int i = 0; i < inputs.length; i++) {
                        Tile t = l.t[(int) inputs[i].x][(int) inputs[i].y];
                        if (t.state) {
                            gc.setFill(Color.RED);
                            anyOn = true;
                        } else
                            gc.setFill(Color.BLACK);

                        gc.fillOval(hitBox.x, hitBox.y + ((i + 1) * (tileSize / (inputs.length + 1))) - (tileSize / 20), tileSize / 10, tileSize / 10);
                    }
                state = anyOn;
                gc.setFill(Color.color(.5, .5, .5, 0));
                break;
            case (23): // NAND Gate
                gc.setFill(Color.BLUEVIOLET);
                gc.fillRect(hitBox.x, hitBox.y, tileSize, tileSize);
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, (int) (tileSize / 3)));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText("NAND", hitBox.x + (hitBox.w / 2), hitBox.y + (hitBox.h / 2) + (gc.getCanvas().getHeight() / 150f));
                numberOn = 0;
                if (inputs != null) {
                    for (int i = 0; i < inputs.length; i++) {
                        Tile t = l.t[(int) inputs[i].x][(int) inputs[i].y];
                        if (t.state) {
                            gc.setFill(Color.RED);
                            numberOn++;
                        } else
                            gc.setFill(Color.BLACK);

                        gc.fillOval(hitBox.x, hitBox.y + ((i + 1) * (tileSize / (inputs.length + 1))) - (tileSize / 20), tileSize / 10, tileSize / 10);
                    }
                    state = numberOn != inputs.length;
                }
                if (state) {
                    gc.setFill(Color.RED);
                } else
                    gc.setFill(Color.BLACK);
                gc.fillOval(hitBox.x + hitBox.w - (tileSize / 10), hitBox.y + (tileSize / 2) - (tileSize / 20), tileSize / 10, tileSize / 10);
                gc.setFill(Color.color(.5, .5, .5, 0));
                break;
            case (24): // XOR Gate
                gc.setFill(Color.BLUEVIOLET);
                gc.fillRect(hitBox.x, hitBox.y, tileSize, tileSize);
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, (int) (tileSize / 3)));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText("XOR", hitBox.x + (hitBox.w / 2), hitBox.y + (hitBox.h / 2) + (gc.getCanvas().getHeight() / 150f));
                numberOn = 0;
                if (inputs != null) {
                    if (inputs.length > 2) {
                        Vector[] temp = inputs.clone();
                        inputs = new Vector[2];
                        inputs[0] = temp[0];
                        inputs[1] = temp[1];
                    }
                    for (int i = 0; i < inputs.length; i++) {
                        Tile t = l.t[(int) inputs[i].x][(int) inputs[i].y];
                        if (t.state) {
                            gc.setFill(Color.RED);
                            numberOn++;
                        } else
                            gc.setFill(Color.BLACK);

                        gc.fillOval(hitBox.x, hitBox.y + ((i + 1) * (tileSize / (inputs.length + 1))) - (tileSize / 20), tileSize / 10, tileSize / 10);
                    }
                    state = numberOn != inputs.length && numberOn != 0;
                }
                if (state) {
                    gc.setFill(Color.RED);
                } else
                    gc.setFill(Color.BLACK);
                gc.fillOval(hitBox.x + hitBox.w - (tileSize / 10), hitBox.y + (tileSize / 2) - (tileSize / 20), tileSize / 10, tileSize / 10);
                gc.setFill(Color.color(.5, .5, .5, 0));
                break;
            case (25): // XNOR Gate
                gc.setFill(Color.BLUEVIOLET);
                gc.fillRect(hitBox.x, hitBox.y, tileSize, tileSize);
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, (int) (tileSize / 3)));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText("XNOR", hitBox.x + (hitBox.w / 2), hitBox.y + (hitBox.h / 2) + (gc.getCanvas().getHeight() / 150f));
                numberOn = 0;
                if (inputs != null) {
                    if (inputs.length > 2) {
                        Vector[] temp = inputs.clone();
                        inputs = new Vector[2];
                        inputs[0] = temp[0];
                        inputs[1] = temp[1];
                    }
                    for (int i = 0; i < inputs.length; i++) {
                        Tile t = l.t[(int) inputs[i].x][(int) inputs[i].y];
                        if (t.state) {
                            gc.setFill(Color.RED);
                            numberOn++;
                        } else
                            gc.setFill(Color.BLACK);

                        gc.fillOval(hitBox.x, hitBox.y + ((i + 1) * (tileSize / (inputs.length + 1))) - (tileSize / 20), tileSize / 10, tileSize / 10);
                    }
                    state = !(numberOn != inputs.length && numberOn != 0);
                }
                if (state) {
                    gc.setFill(Color.RED);
                } else
                    gc.setFill(Color.BLACK);
                gc.fillOval(hitBox.x + hitBox.w - (tileSize / 10), hitBox.y + (tileSize / 2) - (tileSize / 20), tileSize / 10, tileSize / 10);
                gc.setFill(Color.color(.5, .5, .5, 0));
                break;
            case (26): // NOT Gate
                gc.setFill(Color.BLUEVIOLET);
                gc.fillRect(hitBox.x, hitBox.y, tileSize, tileSize);
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, (int) (tileSize / 3)));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText("NOT", hitBox.x + (hitBox.w / 2), hitBox.y + (hitBox.h / 2) + (gc.getCanvas().getHeight() / 150f));
                if (inputs != null) {
                    if (inputs.length > 0) {
                        if (inputs.length > 1) {
                            Vector[] temp = inputs.clone();
                            inputs = new Vector[1];
                            inputs[0] = temp[0];
                        }
                        state = !l.t[(int) inputs[0].x][(int) inputs[0].y].state;
                    }
                }
                if (state) {
                    gc.setFill(Color.RED);
                } else
                    gc.setFill(Color.BLACK);
                gc.fillOval(hitBox.x + hitBox.w - (tileSize / 10), hitBox.y + (tileSize / 2) - (tileSize / 20), tileSize / 10, tileSize / 10);
                gc.setFill(Color.color(.5, .5, .5, 0));
                break;
            case (27): // One-Way Up
                gc.setFill(Color.TRANSPARENT);
                gc.drawImage(tileSet, 576, 96, 16, 16, hitBox.x, hitBox.y, tileSize, tileSize);
                break;
            case (28): // One-Way Base
                gc.setFill(Color.TRANSPARENT);
                gc.drawImage(tileSet, 576, 112, 16, 16, hitBox.x, hitBox.y, tileSize, tileSize);
                break;
        }
        Color c = ((Color) gc.getFill());
        c = Color.color(c.getRed(), c.getGreen(), c.getBlue(), transparency * c.getOpacity());
        if (id != 6 && id != 13)
            c = Color.color(Math.min(c.getRed() + lightIntensity.getRed(), 1), Math.min(c.getGreen() + lightIntensity.getGreen(), 1), Math.min(c.getBlue() + lightIntensity.getBlue(), 1), Math.max(lightIntensity.getBrightness(), c.getOpacity()));
        else
            c = c.interpolate(lightIntensity, 0.7);
        if (c.getOpacity() == 0 && lightIntensity.getBrightness() > 0) {
            c = Color.color(c.getRed(), c.getGreen(), c.getBlue(), lightIntensity.getBrightness());
        }
        gc.setFill(c);
        gc.setStroke(Color.WHITE);
        gc.fillRect(hitBox.x, hitBox.y, tileSize, tileSize);
        if (id == 3) {
            lightIntensity = Color.rgb(255, 0, 0);
        }
        if (id == 4) {
            lightIntensity = Color.rgb(27, 222, 34);
        }
    }

    public Tile copy() {
        Tile t = new Tile(id);
        t.lightIntensity = lightIntensity;
        t.hitBox = hitBox;
        t.text = text;
        t.tint = tint;
        t.alignment = alignment;
        t.angle = angle;
        t.spawnDelay = spawnDelay;
        t.limit = limit;
        t.timer = timer;
        t.inputs = inputs;
        t.state = state;
        return t;
    }
}