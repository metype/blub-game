package com.metype.game;

import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class PlayScreen {

    HBox rootPane;
    Level l;
    double mouseX = 0;
    double mouseY = 0;
    boolean mousePressed = false;
    int mouseButton = 0;
    long animationStartTime = System.nanoTime();
    long frameTime = 0;
    boolean cancelMenu = false;
    boolean menu = false;
    Font font;
    double scrollX = 0;
    double scrollY = 0;
    boolean shift = false;
    String levelName = "";
    Player player = new Player(0, 0);
    double tileSize;
    Vector respawnPos;
    byte[] arrows = new byte[4];
    PlayScreen ps;
    Button returnToMenu;
    boolean endAll = false;
    ArrayList<LevelParser> pack = new ArrayList<>();
    int packIndex = 0;
    Image tileSet;


    public PlayScreen() {
        try {
            tileSet = new Image(new FileInputStream(System.getProperty("user.dir") + File.separator + "assets" + File.separator + "gray_terr.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        returnToMenu = new Button("Exit Level");
        returnToMenu.setVisible(false);
        ps = this;
        System.out.println(new Rect(100, 100, 100, 100).contains(new Vector(125, 125)));
        font = Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, 20);
        rootPane = new HBox();
        Canvas mainCanvas = new Canvas();
        GraphicsContext gc = mainCanvas.getGraphicsContext2D();
        mainCanvas.setFocusTraversable(true);
        mainCanvas.setTranslateZ(1);
        returnToMenu.setTranslateZ(0);
        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
            try {
                mainCanvas.setWidth(rootPane.getWidth());
                mainCanvas.setHeight(rootPane.getHeight());
            } catch (Exception ignored) {
            }
        };
        rootPane.heightProperty().addListener(stageSizeListener);
        rootPane.widthProperty().addListener(stageSizeListener);
        rootPane.getChildren().addAll(mainCanvas);
        rootPane.setOnMouseMoved(arg0 -> {
            if (arg0.isPrimaryButtonDown()) {
                mouseButton = 0;
            }
            if (arg0.isSecondaryButtonDown()) {
                mouseButton = 1;
            }
            if (arg0.isMiddleButtonDown()) {
                mouseButton = 2;
            }
            mouseX = arg0.getSceneX();
            mouseY = arg0.getSceneY();
        });
        mainCanvas.setOnKeyReleased(arg0 -> {
            if (arg0.getCode() == KeyCode.UP) {
                arrows[0] = 0;
            }
            if (arg0.getCode() == KeyCode.DOWN) {
                arrows[1] = 0;
            }
            if (arg0.getCode() == KeyCode.RIGHT) {
                arrows[2] = 0;
            }
            if (arg0.getCode() == KeyCode.LEFT) {
                arrows[3] = 0;
            }
        });
        mainCanvas.setOnKeyPressed(arg0 -> {
            if (arg0.getCode() == KeyCode.UP) {
                arrows[0] = 1;
            }
            if (arg0.getCode() == KeyCode.DOWN) {
                arrows[1] = 1;
            }
            if (arg0.getCode() == KeyCode.RIGHT) {
                arrows[2] = 1;
            }
            if (arg0.getCode() == KeyCode.LEFT) {
                arrows[3] = 1;
            }
            if (arg0.getCode() == KeyCode.SHIFT) {
                shift = true;
            }
            if (arg0.getCode() == KeyCode.ESCAPE) {
                if (menu && !cancelMenu) {
                    cancelMenu = true;
                    animationStartTime = System.nanoTime();
                }
                if (!menu) {
                    menu = true;
                    animationStartTime = System.nanoTime();
                    returnToMenu.setVisible(true);
                    new AnimationTimer() {
                        public void handle(long currentNanoTime) {
                            if (endAll) stop();
                            double time = (currentNanoTime - animationStartTime) / 1000000000.0;
                            int speed = 1000;
                            int sizeMult = 3;
                            if (time * speed > mainCanvas.getWidth() / sizeMult && !cancelMenu) {
                                time = mainCanvas.getWidth() / sizeMult / speed;
                            }
                            gc.setFill(Color.color(150 / 255f, 150 / 255f, 150 / 255f, 150 / 255f));
                            gc.setStroke(Color.BLACK);
                            gc.setLineWidth(5);
                            if (!cancelMenu) {
                                gc.fillRect(mainCanvas.getWidth() - (time * speed), 0, mainCanvas.getWidth() / sizeMult, mainCanvas.getHeight());
                                gc.strokeRect(mainCanvas.getWidth() - (time * speed), 0, mainCanvas.getWidth() / sizeMult, mainCanvas.getHeight());
                                gc.setFill(Color.WHITESMOKE);
                                gc.setLineWidth(1);
                                gc.fillRoundRect((mainCanvas.getWidth() - (time * speed)) + ((mainCanvas.getWidth() / sizeMult) / 3) / 2, mainCanvas.getHeight() / 16f, (mainCanvas.getWidth() / sizeMult) * .66f, mainCanvas.getHeight() / 16f, 40, 40);
                                gc.strokeRoundRect((mainCanvas.getWidth() - (time * speed)) + ((mainCanvas.getWidth() / sizeMult) / 3) / 2, mainCanvas.getHeight() / 16f, (mainCanvas.getWidth() / sizeMult) * .66f, mainCanvas.getHeight() / 16f, 40, 40);
                                gc.setFill(Color.BLACK);
                                gc.setTextAlign(TextAlignment.CENTER);
                                gc.fillText("Return To Menu", (mainCanvas.getWidth() - (time * speed)) + ((mainCanvas.getWidth() / sizeMult) * .5), (mainCanvas.getHeight() / 16f) * 1.66);
                                if ((new Rect((mainCanvas.getWidth() - (time * speed)) + ((mainCanvas.getWidth() / sizeMult) / 3) / 2, mainCanvas.getHeight() / 16f, (mainCanvas.getWidth() / sizeMult) * .66f, mainCanvas.getHeight() / 16f).isTouching(new Rect(mouseX, mouseY, 10, 10))) && mousePressed) {
                                    MainScreen ms = new MainScreen();
                                    try {
                                        mainCanvas.getScene().setRoot(ms.getRootPane());
                                    } catch (NullPointerException ignored) {
                                    }
                                    endAll = true;
                                }
                            } else {
                                gc.fillRect((mainCanvas.getWidth() * .66) + (time * speed), 0, mainCanvas.getWidth() / sizeMult, mainCanvas.getHeight());
                                gc.strokeRect(mainCanvas.getWidth() * .66 + (time * speed), 0, mainCanvas.getWidth() / sizeMult, mainCanvas.getHeight());
                                gc.setFill(Color.WHITESMOKE);
                                gc.setLineWidth(1);
                                gc.fillRoundRect((mainCanvas.getWidth() * .66 + (time * speed)) + ((mainCanvas.getWidth() / sizeMult) / 3) / 2, mainCanvas.getHeight() / 16f, (mainCanvas.getWidth() / sizeMult) * .66f, mainCanvas.getHeight() / 16f, 40, 40);
                                gc.strokeRoundRect((mainCanvas.getWidth() * .66 + (time * speed)) + ((mainCanvas.getWidth() / sizeMult) / 3) / 2, mainCanvas.getHeight() / 16f, (mainCanvas.getWidth() / sizeMult) * .66f, mainCanvas.getHeight() / 16f, 40, 40);
                                gc.setFill(Color.BLACK);
                                gc.setTextAlign(TextAlignment.CENTER);
                                gc.fillText("Return To Menu", (mainCanvas.getWidth() * .66 + (time * speed)) + ((mainCanvas.getWidth() / sizeMult) * .5), (mainCanvas.getHeight() / 16f) * 1.66);
                                if ((new Rect((mainCanvas.getWidth() - (time * speed)) + ((mainCanvas.getWidth() / sizeMult) / 3) / 2, mainCanvas.getHeight() / 16f, (mainCanvas.getWidth() / sizeMult) * .66f, mainCanvas.getHeight() / 16f).isTouching(new Rect(mouseX, mouseY, 10, 10))) && mousePressed) {
                                    MainScreen ms = new MainScreen();
                                    try {
                                        mainCanvas.getScene().setRoot(ms.getRootPane());
                                    } catch (NullPointerException ignored) {
                                    }
                                    endAll = true;
                                }
                            }
                            if (time * speed > mainCanvas.getWidth() / sizeMult && cancelMenu) {
                                cancelMenu = false;
                                menu = false;
                                stop();
                            }
                            gc.setLineWidth(1);
                        }
                    }.start();
                }
            }
        });
        rootPane.setOnMouseDragged(arg0 -> {
            if (arg0.isPrimaryButtonDown()) {
                mouseButton = 0;
            }
            if (arg0.isSecondaryButtonDown()) {
                mouseButton = 1;
            }
            if (arg0.isMiddleButtonDown()) {
                mouseButton = 2;
            }
            mouseX = arg0.getSceneX();
            mouseY = arg0.getSceneY();
            mousePressed = true;
        });
        rootPane.setOnMousePressed(arg0 -> {
            if (arg0.isPrimaryButtonDown()) {
                mouseButton = 0;
            }
            if (arg0.isSecondaryButtonDown()) {
                mouseButton = 1;
            }
            if (arg0.isMiddleButtonDown()) {
                mouseButton = 2;
            }
            mouseX = arg0.getSceneX();
            mouseY = arg0.getSceneY();
            mousePressed = true;
        });
        rootPane.setOnMouseReleased(arg0 -> {
            mouseX = arg0.getSceneX();
            mouseY = arg0.getSceneY();
            mousePressed = false;
        });
        rootPane.setOnMouseDragReleased(arg0 -> {
            mouseX = arg0.getSceneX();
            mouseY = arg0.getSceneY();
            mousePressed = false;
        });
        final int[] fps = {0};
        final int[] count = {0};
        final long[] pre = {0};
        l = new Level(5, 2);
        gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, 25));
        frameTime = System.nanoTime();
        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                if (endAll) stop();
                double t = (currentNanoTime - frameTime) / 1000000000.0;
                frameTime = System.nanoTime();
                for (int i = 0; i < l.t[0].length; i++) {
                    for (int j = 0; j < l.t.length; j++) {
                        try {
                            if (l.t[j][i].hitBox.isTouching(new Rect(mainCanvas.getWidth() / 2, mainCanvas.getHeight() / 2, 5, 5))) {
                            }
                        } catch (NullPointerException ignored) {
                        }
                    }
                }
                tileSize = mainCanvas.getHeight() / 11f;
                gc.setFill(Color.GRAY);
                gc.fillRect(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight());
                if (menu) {
                    l.render(scrollX, scrollY, tileSize, gc, mainCanvas.getWidth(), mainCanvas.getHeight(), null);
                    player.render(gc, tileSize / 2, ps);
                } else {
                    Vector test = l.render(scrollX, scrollY, tileSize, gc, mainCanvas.getWidth(), mainCanvas.getHeight(), tileSet);
                    if (!(test.x == 0 && test.y == 0))
                        respawnPos = test;
                    player.pos.mult(tileSize);
                    Vector renderBox = new Vector(((int) player.pos.x / tileSize), ((int) player.pos.y / tileSize));
                    player.pos.div(tileSize);
                    player.render(gc, tileSize / 2, ps);
                    if (player.respawnTimer <= 0) {
                        for (int i = 0; i < 50; i++) {
                            ArrayList<Tile> tiles = new ArrayList<>();
                            for (int j = Math.max(((int) renderBox.x) - 2, 0); j < Math.min(((int) renderBox.x) + 2, l.t.length); j++) {
                                for (int k = Math.max(((int) renderBox.y) - 2, 0); k < Math.min(((int) renderBox.y) + 2, l.t[j].length); k++) {
                                    Tile tile = l.t[j][k];
                                    tile.setHitBox(new Rect((scrollX) + (j * tileSize), (scrollY) + (k * tileSize), tileSize, tileSize));
                                    if (tile.id == 20) {
                                        tile.setHitBox(new Rect((scrollX) + (j * tileSize), (scrollY) + (k * tileSize) + (tileSize - (tileSize / 5)), tileSize, tileSize / 5));
                                    }
                                    if (tile.id == 22 && tile.state) {
                                        tile.setHitBox(new Rect((scrollX) + (j * tileSize), (scrollY) + (k * tileSize), tileSize, tileSize * 3));
                                    }
                                    tiles.add(tile);
                                }
                            }
                            Vector scroll = player.updatePhysics(tiles, 50, l, tileSize, gc, false, arrows, false, t);
                            l.onoffstate = player.onoffstate;
                            scrollX = scroll.x;
                            scrollY = scroll.y;
                        }
                    }
                    if (l.completed) {
                        if (pack.size() > 0) {
                            if (packIndex == pack.size() - 1) {
                                CompletionScreen cs = new CompletionScreen();
                                mainCanvas.getScene().setRoot(cs.getRootPane());
                                stop();
                                return;
                            }
                            l = (Level) pack.get(packIndex++).get("levelData");
                            levelName = pack.get(packIndex).file.getAbsolutePath();
                        } else {
                            CompletionScreen cs = new CompletionScreen();
                            mainCanvas.getScene().setRoot(cs.getRootPane());
                            stop();
                        }
                    }
                }
                count[0]++;
                if (pre[0] + 1000 < System.currentTimeMillis()) {
                    pre[0] = System.currentTimeMillis();
                    fps[0] = count[0];
                    count[0] = 0;
                }
                gc.setStroke(Color.WHITESMOKE);
                gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, 25));
                gc.setFill(Color.WHITE);
                gc.setTextAlign(TextAlignment.LEFT);
                gc.fillText("FPS:" + fps[0], 0, 25);
            }
        }.start();
    }

    final static public double map(double value,
                                   double istart,
                                   double istop,
                                   double ostart,
                                   double ostop) {
        return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
    }

    public void loadPack(File f) {
        ArrayList<File> files = new ArrayList<>();
        File dir = new File(f.getParent() + "/temp");
        try {
            UnzipUtility.unzip(f.getAbsolutePath(), dir.getAbsolutePath());
            Collections.addAll(files, Objects.requireNonNull(dir.listFiles()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert files.isEmpty();
        pack.clear();
        for (File levelFile : files) {
            LevelParser lp = null;
            try {
                lp = new LevelParser(levelFile);
                lp.loadLevel(levelFile);
            } catch (IOException ignored) {
            }
            assert lp != null;
            pack.add(lp);
        }
        l = (Level) pack.get(0).get("levelData");
        levelName = pack.get(0).file.getAbsolutePath();
        dir.delete();
    }

    public void loadLevel(File f) {
        this.levelName = f.getAbsolutePath();
        LevelParser lp = null;
        try {
            lp = new LevelParser(f);
            lp.loadLevel(f);
        } catch (IOException ignored) {
        }
        assert lp != null;
        l = (Level) lp.get("levelData");
        l.calculateLighting(500);
        System.out.println(tileSize);
        respawnPos = l.getSpawnPos();
        player.pos.x = respawnPos.x;
        player.pos.y = respawnPos.y;
        player.pos.mult(tileSize);
        player.pos.x += tileSize / 2;
        player.pos.y += tileSize - (tileSize / 4);
        player.pos.div(tileSize);
    }

    public void loadLevel(String path) {
        loadLevel(new File(path));
    }

    public void reload() {
        loadLevel(levelName);
        player.onoffstate = false;
    }

    public Pane getRootPane() {
        return rootPane;
    }
}