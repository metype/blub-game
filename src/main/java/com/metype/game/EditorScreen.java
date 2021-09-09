package com.metype.game;

import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditorScreen {

    VBox rootPane;
    Level l;
    double mouseX = 0;
    double mouseY = 0;
    boolean mousePressed = false;
    boolean editing = false;
    int mouseButton = 0;
    Tile editTile = new Tile(0);
    long animationStartTime = System.nanoTime();
    boolean cancelEditing = false;
    boolean inventory = false;
    boolean cancelInventory = false;
    Font font;
    int selectedTile = 1;
    double scrollX = 0;
    double scrollY = 0;
    double xa = 0;
    double ya = 0;
    boolean shift = false;
    double thumbnailWidth = 0;
    double thumbnailHeight = 0;
    Vector offset = new Vector(0,0);
    File levelName = null;
    boolean menu = false;
    boolean cancelMenu = false;
    boolean endAll=false;
    int tool = 0;
    Vector selectionStart = new Vector(0,0);
    Vector selectionSize = new Vector(0,0);
    boolean selecting = false;
    boolean selection = false;
    boolean ctrl = false;
    Tile[][] copy;
    Rect selectRect = null;
    ArrayList<Action> actions = new ArrayList<Action>();
    int actionIndex = -1;
    Button returnToMenu = new Button("Rect To Menu");
    MenuBar mb = new MenuBar();
    Tile tileOneForWire;
    Image tileSet;


    public EditorScreen() {
        try {
            tileSet = new Image(new FileInputStream(System.getProperty("user.dir") + "/assets/gray_terr.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(new Rect(100,100,100,100).contains(new Vector(125,125)));
        font = Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, 20);
        rootPane = new VBox();
        Canvas mainCanvas = new Canvas();
        GraphicsContext gc = mainCanvas.getGraphicsContext2D();
        mainCanvas.setFocusTraversable(true);
        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
            Canvas c;
            try {
                mainCanvas.setWidth(rootPane.getWidth());
                mainCanvas.setHeight(rootPane.getHeight());
            }catch(Exception ignored){};
        };
        rootPane.heightProperty().addListener(stageSizeListener);
        rootPane.widthProperty().addListener(stageSizeListener);
        mb.setUseSystemMenuBar(true);
        Menu file = new Menu("File");
        MenuItem newItem = new MenuItem("New");
        file.getItems().add(newItem);
        newItem.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent actionEvent) {
                final double[] newWidth = {1};
                final double[] newHeight = {1};
                Stage popupwindow=new Stage();
                popupwindow.initModality(Modality.APPLICATION_MODAL);
                popupwindow.setTitle("Create A New Level");
                Label horizontalLabel= new Label("Change Width of the level (in screens, a screen is 15 tiles wide)");
                Slider horizontalSlider = new Slider();
                horizontalSlider.setMajorTickUnit(1);
                horizontalSlider.setMinorTickCount(0);
                horizontalSlider.setBlockIncrement(1);
                horizontalSlider.setSnapToTicks(true);
                horizontalSlider.showTickMarksProperty().set(true);
                horizontalSlider.showTickLabelsProperty().set(true);
                horizontalSlider.setMax(15);
                horizontalSlider.setMin(1);
                horizontalSlider.valueProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                        newWidth[0] = t1.doubleValue();
                    }
                });
                Label verticalLabel= new Label("Change Height of the level (in screens, a screen is 11 tiles tall)");
                Slider verticalSlider = new Slider();
                verticalSlider.setMajorTickUnit(1);
                verticalSlider.setMinorTickCount(0);
                verticalSlider.setBlockIncrement(1);
                verticalSlider.setSnapToTicks(true);
                verticalSlider.showTickMarksProperty().set(true);
                verticalSlider.showTickLabelsProperty().set(true);
                verticalSlider.setMax(15);
                verticalSlider.setMin(1);
                verticalSlider.valueProperty().addListener(new ChangeListener<>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                        newHeight[0] = t1.doubleValue();
                    }
                });
                Button okay = new Button("Create New Level");
                Button cancel = new Button("Cancel");
                okay.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        l = new Level((int)newWidth[0],(int)newHeight[0]);
                        scrollX=0;
                        scrollY=0;
                        popupwindow.close();
                    }
                });
                cancel.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        popupwindow.close();
                    }
                });
                VBox layout= new VBox(5);
                HBox cheating = new HBox(2);
                cheating.getChildren().addAll(cancel, okay);
                layout.getChildren().addAll(horizontalLabel, horizontalSlider, verticalLabel, verticalSlider, cheating);
                layout.setAlignment(Pos.CENTER);
                cheating.setAlignment(Pos.CENTER);
                Scene scene1= new Scene(layout, 300, 250);
                popupwindow.setHeight(230);
                popupwindow.setWidth(412);
                popupwindow.resizableProperty().set(false);
                popupwindow.setScene(scene1);
                popupwindow.showAndWait();
            }
        });
        MenuItem openItem = new MenuItem("Open");
        file.getItems().add(openItem);
        openItem.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent actionEvent) {
                FileChooser fileChooser = new FileChooser();
                File levelDirectory = new File(System.getProperty("user.dir") + "/levels/");
                fileChooser.setInitialDirectory(levelDirectory);
                fileChooser.setTitle("Open Level File");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Level Files", "*.lev"));
                File selectedFile = fileChooser.showOpenDialog(rootPane.getScene().getWindow());
                if (selectedFile != null) {
                    loadLevel(selectedFile);
                }
            }
        });
        MenuItem saveItem = new MenuItem("Save");
        file.getItems().add(saveItem);
        saveItem.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent actionEvent) {
                FileChooser fileChooser = new FileChooser();
                File levelDirectory = new File(System.getProperty("user.dir") + "/levels/");
                fileChooser.setInitialDirectory(levelDirectory);
                fileChooser.setTitle("Save Level File");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Level Files", "*.lev"));
                File selectedFile = fileChooser.showSaveDialog(rootPane.getScene().getWindow());
                if (selectedFile != null) {
                    if(selectedFile.exists()) {
                        selectedFile.delete();
                    }
                    try {
                        selectedFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String[] data = saveLevel(mainCanvas);
                    BufferedWriter outputWriter = null;
                    try {
                        outputWriter = new BufferedWriter(new FileWriter(selectedFile));
                        for (String datum : data) {
                            outputWriter.write(datum);
                            outputWriter.newLine();
                        }
                        outputWriter.flush();
                        outputWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        file.getItems().add(new MenuItem("Save As"));
        MenuItem exitItem = new MenuItem("Exit");
        file.getItems().add(exitItem);
        exitItem.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent actionEvent) {
                MainScreen main = new MainScreen();
                mainCanvas.getScene().setRoot(main.getRootPane());
            }
        });
        Menu edit = new Menu("Edit");
        MenuItem cutItem = new MenuItem("Cut");
        edit.getItems().add(cutItem);
        cutItem.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent actionEvent) {
                copy = new Tile[(int)(selectRect.w)][(int)(selectRect.h)];
                for(double i=selectRect.x;i<selectRect.x+selectRect.w;i++){
                    for(double j=selectRect.y;j<selectRect.y+selectRect.h;j++){
                        copy[(int)(i-selectRect.x)][(int)(j-selectRect.y)] = new Tile(l.t[(int)i][(int)j].id);
                    }
                }
                Action a = new Action(new Vector(selectRect.x, selectRect.y));
                Tile[][] old = new Tile[(int)selectRect.w][(int)selectRect.h];
                Tile[][] change = new Tile[(int)selectRect.w][(int)selectRect.h];
                for(double i=selectRect.x;i<selectRect.x+selectRect.w;i++){
                    for(double j=selectRect.y;j<selectRect.y+selectRect.h;j++){
                        old[(int)i-(int)selectRect.x][(int)j-(int)selectRect.y] = l.t[(int)i][(int)j];
                        change[(int)i-(int)selectRect.x][(int)j-(int)selectRect.y] = new Tile(0);
                        l.t[(int)i][(int)j] = new Tile(0);
                    }
                }
                a.setOld(old);
                a.setNew(change);
                ArrayList<Action> toRemove = new ArrayList<Action>();
                for (int i = actionIndex+1; i < actions.size(); i++) toRemove.add(actions.get(i));
                actions.removeAll(toRemove);
                actions.add(a);
                actionIndex=actions.size()-1;
            }
        });
        MenuItem copyItem = new MenuItem("Copy");
        edit.getItems().add(copyItem);
        copyItem.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent actionEvent) {
                copy = new Tile[(int)(selectRect.w)][(int)(selectRect.h)];
                for(double i=selectRect.x;i<selectRect.x+selectRect.w;i++){
                    for(double j=selectRect.y;j<selectRect.y+selectRect.h;j++){
                        copy[(int)(i-selectRect.x)][(int)(j-selectRect.y)] = new Tile(l.t[(int)i][(int)j].id);
                    }
                }
            }
        });
        MenuItem pasteItem = new MenuItem("Paste");
        edit.getItems().add(pasteItem);
        pasteItem.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent actionEvent) {
                Action a = new Action(new Vector((int)selectionStart.x, (int)selectionStart.y));
                Tile[][] old = new Tile[copy.length][copy[0].length];
                a.setNew(copy);
                for (int i = (int)selectionStart.x; i < (int)selectionStart.x + copy.length; i++) {
                    for (int j = (int)selectionStart.y; j < (int)selectionStart.y + copy[i - (int)selectionStart.x].length; j++) {
                        old[i - (int)selectionStart.x][j - (int)selectionStart.y] = l.t[i][j].copy();
                        l.t[i][j] = new Tile(copy[i - (int)selectionStart.x][j - (int)selectionStart.y].id);
                    }
                }
                a.setOld(old);
                ArrayList<Action> toRemove = new ArrayList<Action>();
                for (int i = actionIndex+1; i < actions.size(); i++) toRemove.add(actions.get(i));
                actions.removeAll(toRemove);
                actions.add(a);
                actionIndex=actions.size()-1;
            }
        });
        MenuItem undoItem = new MenuItem("Undo");
        edit.getItems().add(undoItem);
        undoItem.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent actionEvent) {
                l = actions.get(actionIndex).undo(l);
                actionIndex--;
                actionIndex = Math.max(-1,actionIndex);
            }
        });
        MenuItem redoItem = new MenuItem("Redo");
        edit.getItems().add(redoItem);
        redoItem.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent actionEvent) {
                actionIndex++;
                l = actions.get(actionIndex).redo(l);
                actionIndex = Math.min(actions.size()-1,actionIndex);
            }
        });
        Menu tools = new Menu("Tools");
        MenuItem placeToolItem = new MenuItem("Place Tool");
        tools.getItems().add(placeToolItem);
        placeToolItem.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent actionEvent) {
                tool = 0;
            }
        });
        MenuItem selectionToolItem = new MenuItem("Selection Tool");
        tools.getItems().add(selectionToolItem);
        selectionToolItem.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent actionEvent) {
                tool = 1;
            }
        });
        MenuItem wireToolItem = new MenuItem("Wiring Tool");
        tools.getItems().add(wireToolItem);
        wireToolItem.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent actionEvent) {
                tool = 2;
            }
        });
        mb.getMenus().addAll(file, edit, tools);
        mb.setFocusTraversable(false);
        HBox.setHgrow(mb, Priority.ALWAYS);
        rootPane.getChildren().add(mb);
        rootPane.getChildren().add(mainCanvas);
        returnToMenu.setTranslateZ(-100);
        returnToMenu.setVisible(false);
        rootPane.setOnMouseMoved(arg0 -> {
            if(arg0.isPrimaryButtonDown()){
                mouseButton=0;
            }
            if(arg0.isSecondaryButtonDown()){
                mouseButton=1;
            }
            if(arg0.isMiddleButtonDown()){
                mouseButton=2;
            }
            mouseX=arg0.getX();
            mouseY=arg0.getY()-mb.getHeight();
        });
        mainCanvas.setOnKeyReleased(arg0 -> {
            if(arg0.getCode() == KeyCode.LEFT) {
                xa=0;
            }
            if(arg0.getCode() == KeyCode.RIGHT) {
                xa=0;
            }
            if(arg0.getCode() == KeyCode.UP) {
                ya=0;
            }
            if(arg0.getCode() == KeyCode.DOWN) {
                ya=0;
            }
            if(arg0.getCode() == KeyCode.SHIFT){
                shift=false;
            }
            if(arg0.getCode() == KeyCode.CONTROL){
                ctrl=false;
            }
        });
        mainCanvas.setOnKeyPressed(arg0 -> {
            if(arg0.getCode() == KeyCode.LEFT) {
                xa=mainCanvas.getWidth()/200f;
                if(shift) xa=mainCanvas.getWidth()/200f * 3;
            }
            if(arg0.getCode() == KeyCode.RIGHT) {
                xa=-mainCanvas.getWidth()/200f;
                if(shift) xa=-mainCanvas.getWidth()/200f * 3;
            }
            if(arg0.getCode() == KeyCode.UP) {
                ya=mainCanvas.getWidth()/200f;
                if(shift) ya=mainCanvas.getWidth()/200f * 3;
            }
            if(arg0.getCode() == KeyCode.DOWN) {
                ya=-mainCanvas.getWidth()/200f;
                if(shift) ya=-mainCanvas.getWidth()/200f * 3;
            }
            if(arg0.getCode() == KeyCode.SHIFT){
                shift=true;
            }
            if(arg0.getCode() == KeyCode.ESCAPE){
                if(editing && !cancelEditing) {
                    cancelEditing = true;
                    animationStartTime = System.nanoTime();
                }
                if(inventory && !cancelInventory) {
                    cancelInventory = true;
                    animationStartTime = System.nanoTime();
                }
                    if(menu && !cancelMenu) {
                        cancelMenu = true;
                        animationStartTime = System.nanoTime();
                    }
                    if(!menu && !inventory){
                        menu = true;
                        animationStartTime = System.nanoTime();
                        final double[] width = {0};
                        final double[] height = {0};
                        new AnimationTimer()
                        {
                            public void handle(long currentNanoTime)
                            {
                                if(endAll) stop();
                                double time = (currentNanoTime - animationStartTime) / 1000000000.0;
                                int speed = 1000;
                                int sizeMult = 3;
                                if(time*speed>mainCanvas.getWidth()/sizeMult && !cancelMenu){
                                    time=mainCanvas.getWidth()/sizeMult/speed;
                                }
                                gc.setFill(Color.color(150/255f,150/255f,150/255f,150/255f));
                                gc.setStroke(Color.BLACK);
                                gc.setLineWidth(5);
                                returnToMenu.setVisible(true);
                                if(!cancelMenu) {
                                    gc.fillRect(mainCanvas.getWidth() - (time * speed), 0, mainCanvas.getWidth() / sizeMult, mainCanvas.getHeight());
                                    gc.strokeRect(mainCanvas.getWidth() - (time * speed), 0, mainCanvas.getWidth() / sizeMult, mainCanvas.getHeight());
                                    gc.setFill(Color.WHITESMOKE);
                                    gc.setLineWidth(1);
                                    gc.fillRoundRect((mainCanvas.getWidth() - (time * speed))+((mainCanvas.getWidth() / sizeMult)/3)/2, mainCanvas.getHeight()/16f, (mainCanvas.getWidth() / sizeMult)*.66f, mainCanvas.getHeight()/16f, 40, 40);
                                    gc.strokeRoundRect((mainCanvas.getWidth() - (time * speed))+((mainCanvas.getWidth() / sizeMult)/3)/2, mainCanvas.getHeight()/16f, (mainCanvas.getWidth() / sizeMult)*.66f, mainCanvas.getHeight()/16f, 40, 40);
                                    gc.setFill(Color.BLACK);
                                    gc.setTextAlign(TextAlignment.CENTER);
                                    returnToMenu.setMaxHeight(mainCanvas.getHeight()/16f);
                                    returnToMenu.setMinHeight(mainCanvas.getHeight()/16f);
                                    returnToMenu.setMaxWidth((mainCanvas.getWidth() - (time * speed))+((mainCanvas.getWidth() / sizeMult)/3)/2);
                                    returnToMenu.setMinWidth((mainCanvas.getWidth() - (time * speed))+((mainCanvas.getWidth() / sizeMult)/3)/2);
                                    returnToMenu.setLayoutX((mainCanvas.getWidth() / sizeMult)*.66f);
                                    returnToMenu.setLayoutY(mainCanvas.getHeight()/16f);
                                    gc.fillText("Return To Menu",(mainCanvas.getWidth() - (time * speed))+((mainCanvas.getWidth() / sizeMult)*.5),(mainCanvas.getHeight()/16f)*1.66);
                                    if((new Rect((mainCanvas.getWidth() - (time * speed))+((mainCanvas.getWidth() / sizeMult)/3)/2, mainCanvas.getHeight()/16f, (mainCanvas.getWidth() / sizeMult)*.66f, mainCanvas.getHeight()/16f).isTouching(new Rect(mouseX,mouseY,10,10)))&&mousePressed){
                                        MainScreen ms = new MainScreen();
                                        try {
                                            mainCanvas.getScene().setRoot(ms.getRootPane());
                                        }catch(NullPointerException ignored){}
                                        endAll=true;
                                    }
                                }else{
                                    gc.fillRect((mainCanvas.getWidth() *.66) + (time*speed), 0, mainCanvas.getWidth() / sizeMult, mainCanvas.getHeight());
                                    gc.strokeRect(mainCanvas.getWidth() *.66 + (time*speed), 0, mainCanvas.getWidth() / sizeMult, mainCanvas.getHeight());
                                    gc.setFill(Color.WHITESMOKE);
                                    gc.setLineWidth(1);
                                    gc.fillRoundRect((mainCanvas.getWidth() *.66 + (time * speed))+((mainCanvas.getWidth() / sizeMult)/3)/2, mainCanvas.getHeight()/16f, (mainCanvas.getWidth() / sizeMult)*.66f, mainCanvas.getHeight()/16f, 40, 40);
                                    gc.strokeRoundRect((mainCanvas.getWidth() *.66 + (time * speed))+((mainCanvas.getWidth() / sizeMult)/3)/2, mainCanvas.getHeight()/16f, (mainCanvas.getWidth() / sizeMult)*.66f, mainCanvas.getHeight()/16f, 40, 40);
                                    gc.setFill(Color.BLACK);
                                    gc.setTextAlign(TextAlignment.CENTER);
                                    gc.fillText("Return To Menu",(mainCanvas.getWidth() *.66 + (time * speed))+((mainCanvas.getWidth() / sizeMult)*.5),(mainCanvas.getHeight()/16f)*1.66);
                                    if((new Rect((mainCanvas.getWidth() - (time * speed))+((mainCanvas.getWidth() / sizeMult)/3)/2, mainCanvas.getHeight()/16f, (mainCanvas.getWidth() / sizeMult)*.66f, mainCanvas.getHeight()/16f).isTouching(new Rect(mouseX,mouseY,10,10)))&&mousePressed){
                                        MainScreen ms = new MainScreen();
                                        try {
                                            mainCanvas.getScene().setRoot(ms.getRootPane());
                                        }catch(NullPointerException ignored){}
                                        endAll=true;
                                    }
                                }
                                if (time*speed>mainCanvas.getWidth()/sizeMult && cancelMenu){
                                    cancelMenu=false;
                                    menu=false;
                                    returnToMenu.setVisible(false);
                                    stop();
                                }
                                gc.setLineWidth(1);
                            }
                        }.start();
                    }
            }
            if(arg0.getCode() == KeyCode.CONTROL){
                ctrl=true;
            }
            if(arg0.getCode() == KeyCode.C && ctrl && selection && tool == 1){
                copy = new Tile[(int)(selectRect.w)][(int)(selectRect.h)];
                for(double i=selectRect.x;i<selectRect.x+selectRect.w;i++){
                    for(double j=selectRect.y;j<selectRect.y+selectRect.h;j++){
                        copy[(int)(i-selectRect.x)][(int)(j-selectRect.y)] = new Tile(l.t[(int)i][(int)j].id);
                    }
                }
            }
            if(arg0.getCode() == KeyCode.X && ctrl && selection && tool == 1) {
                copy = new Tile[(int) (selectRect.w)][(int) (selectRect.h)];
                for (double i = selectRect.x; i < selectRect.x + selectRect.w; i++) {
                    for (double j = selectRect.y; j < selectRect.y + selectRect.h; j++) {
                        copy[(int) (i - selectRect.x)][(int) (j - selectRect.y)] = new Tile(l.t[(int) i][(int) j].id);
                    }
                }
                Action a = new Action(new Vector(selectRect.x, selectRect.y));
                Tile[][] old = new Tile[(int) selectRect.w][(int) selectRect.h];
                Tile[][] change = new Tile[(int) selectRect.w][(int) selectRect.h];
                for (double i = selectRect.x; i < selectRect.x + selectRect.w; i++) {
                    for (double j = selectRect.y; j < selectRect.y + selectRect.h; j++) {
                        old[(int) i - (int) selectRect.x][(int) j - (int) selectRect.y] = l.t[(int) i][(int) j];
                        change[(int) i - (int) selectRect.x][(int) j - (int) selectRect.y] = new Tile(0);
                        l.t[(int) i][(int) j] = new Tile(0);
                    }
                }
                a.setOld(old);
                a.setNew(change);
                ArrayList<Action> toRemove = new ArrayList<Action>();
                for (int i = actionIndex + 1; i < actions.size(); i++) toRemove.add(actions.get(i));
                actions.removeAll(toRemove);
                actions.add(a);
                actionIndex = actions.size() - 1;
            }
            if(arg0.getCode() == KeyCode.V && ctrl && selection && copy!=null) {
                int MX = (int) Math.floor((mouseX - scrollX) / (mainCanvas.getHeight() / 20f));
                int MY = (int) Math.floor((mouseY - scrollY) / (mainCanvas.getHeight() / 20f));
                Action a = new Action(new Vector(MX, MY));
                Tile[][] old = new Tile[copy.length][copy[0].length];
                a.setNew(copy);
                for (int i = MX; i < MX + copy.length; i++) {
                    for (int j = MY; j < MY + copy[i - MX].length; j++) {
                        old[i - MX][j - MY] = l.t[i][j].copy();
                        l.t[i][j] = new Tile(copy[i - MX][j - MY].id);
                    }
                }
                a.setOld(old);
                ArrayList<Action> toRemove = new ArrayList<Action>();
                for (int i = actionIndex+1; i < actions.size(); i++) toRemove.add(actions.get(i));
                actions.removeAll(toRemove);
                actions.add(a);
                actionIndex=actions.size()-1;
            }
            if(arg0.getCode() == KeyCode.Z && actions.size()>0 && actionIndex > -1){
                l = actions.get(actionIndex).undo(l);
                actionIndex--;
                actionIndex = Math.max(-1,actionIndex);
            }
            if(arg0.getCode() == KeyCode.Y && actions.size()>0 && actionIndex < actions.size()-1){
                actionIndex++;
                l = actions.get(actionIndex).redo(l);
                actionIndex = Math.min(actions.size()-1,actionIndex);
            }
            if(arg0.getCode() == KeyCode.DELETE && selection && tool == 1){
                Action a = new Action(new Vector(selectRect.x, selectRect.y));
                Tile[][] old = new Tile[(int)selectRect.w][(int)selectRect.h];
                Tile[][] change = new Tile[(int)selectRect.w][(int)selectRect.h];
                for(double i=selectRect.x;i<selectRect.x+selectRect.w;i++){
                    for(double j=selectRect.y;j<selectRect.y+selectRect.h;j++){
                        old[(int)i-(int)selectRect.x][(int)j-(int)selectRect.y] = l.t[(int)i][(int)j];
                        change[(int)i-(int)selectRect.x][(int)j-(int)selectRect.y] = new Tile(0);
                        l.t[(int)i][(int)j] = new Tile(0);
                    }
                }
                a.setOld(old);
                a.setNew(change);
                ArrayList<Action> toRemove = new ArrayList<Action>();
                for (int i = actionIndex+1; i < actions.size(); i++) toRemove.add(actions.get(i));
                actions.removeAll(toRemove);
                actions.add(a);
                actionIndex=actions.size()-1;
            }
            if(arg0.getCode() == KeyCode.I){
                if(!editing && !inventory){
                    inventory = true;
                    animationStartTime = System.nanoTime();
                    final double[] width = {0};
                    final double[] height = {0};
                    new AnimationTimer()
                    {
                        public void handle(long currentNanoTime)
                        {
                            if(cancelInventory){
                                width[0] -= mainCanvas.getWidth()/60;
                                height[0] -= mainCanvas.getHeight()/60;
                            }else {
                                width[0] += mainCanvas.getWidth()/60;
                                height[0] += mainCanvas.getHeight()/60;
                            }
                            int size = 0;
                            if(width[0] > mainCanvas.getWidth()/1.3f && !cancelInventory){
                                width[0] = mainCanvas.getWidth()/1.3f;
                                size++;
                            }
                            if(height[0] > mainCanvas.getHeight()/1.3f && !cancelInventory){
                                height[0] = mainCanvas.getHeight()/1.3f;
                                size++;
                            }
                            gc.setFill(Color.color(150/255f,150/255f,150/255f,150/255f));
                            gc.setStroke(Color.BLACK);
                            gc.setLineWidth(5);
                            gc.fillRect((mainCanvas.getWidth()/2) - (width[0])/2, (mainCanvas.getHeight()/2) - (height[0])/2, width[0], height[0]);
                            gc.strokeRect((mainCanvas.getWidth()/2) - (width[0])/2, (mainCanvas.getHeight()/2) - (height[0])/2, width[0], height[0]);
                            gc.setLineWidth(2);
                            if(size==2){
                                String[] descriptions = {"A Blank Tile", "Standard Solid Tile", "Goal Tile", "Lava Tile", "Bouncy Tile", "Slippery Tile", "Water:\nCan be Swam In", "The Starting Point\nfor Your Level", "A Checkpoint", "When Hit Will Toggle\nThe State Of The Level", "Only Solid When\nLevel State is \"Off\"", "Only Solid When\nLevel State is \"On\"", "Text Tile", "Shallow Water:\nDoes Not Effect\nMovement", "Generates Enemies", "Performs a Function\nWhen Moved Over", "Tinted Glass:\nChanges Colour Of Light\nGoing Through It", "Logical OR Gate", "Logical AND Gate", "Logical NOR Gate", "Button", "Light", "Door", "Logical NAND Gate", "Logical XOR Gate", "Logical XNOR Gate", "Logical NOT Gate", "An Upward One-Way Tile", "A Platform For a One-Way Tile"};
                                String[] labels = {"General Tiles", "Liquids", "Functional Tiles", "Toggle", "Logical Tiles"};
                                int[][] inventoryTiles = {{0, 1, 2, 4, 5, 27, 28, 16}, {6, 13, 3}, {7, 8, 14, 15, 12}, {9, 10, 11}, {17, 19, 18, 23, 24, 25, 26, 20, 21, 22}};
                                for (int i=0; i<inventoryTiles.length; i++) {
                                    gc.setFill(Color.BLACK);
                                    gc.setStroke(Color.BLACK);
                                    gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, (int)(height[0])/25f));
                                    gc.setTextAlign(TextAlignment.LEFT);
                                    gc.fillText(labels[i], (mainCanvas.getWidth()/2) - (width[0])/2 + mainCanvas.getWidth()/50f,((mainCanvas.getHeight()/1.9) - (height[0])/2)+(i*mainCanvas.getHeight()/7) - ((mainCanvas.getHeight()/15f) - (mainCanvas.getHeight()/20f))/2 - mainCanvas.getHeight()/45f + mainCanvas.getHeight()/30f);
                                    if(i>0)
                                        gc.strokeRect((mainCanvas.getWidth()/2) - (width[0])/2,((mainCanvas.getHeight()/1.9) - (height[0])/2)+(i*mainCanvas.getHeight()/7) - ((mainCanvas.getHeight()/15f) - (mainCanvas.getHeight()/20f))/2 - mainCanvas.getHeight()/45f,(mainCanvas.getWidth()/1.3f)*.66,1);
                                        gc.strokeLine(((mainCanvas.getWidth()/2) - (width[0])/2)+ (mainCanvas.getWidth()/1.3f)*.66,(mainCanvas.getHeight()/2) - (height[0])/2,((mainCanvas.getWidth()/2) - (width[0])/2)+ (mainCanvas.getWidth()/1.3f)*.66,((mainCanvas.getHeight()/2) - (height[0])/2) + height[0]);
                                    for (int j=0; j<inventoryTiles[i].length; j++) {
                                        Tile tile = new Tile(inventoryTiles[i][j]);
                                        Rect hitBox = new Rect(((mainCanvas.getWidth()/1.9) - (width[0])/2)+(j*((mainCanvas.getHeight()/30)+(mainCanvas.getHeight()/20f))), ((mainCanvas.getHeight()/1.9) - (height[0])/2)+(i*mainCanvas.getHeight()/7) + mainCanvas.getHeight()/30f, mainCanvas.getHeight()/20f, mainCanvas.getHeight()/20f);
                                        tile.setHitBox(hitBox);
                                        hitBox = new Rect((((mainCanvas.getWidth()/1.9) - (width[0])/2)+(j*((mainCanvas.getHeight()/30)+(mainCanvas.getHeight()/20f)))) - ((mainCanvas.getHeight()/15f) - (mainCanvas.getHeight()/20f))/2, ((mainCanvas.getHeight()/1.9) - (height[0])/2)+(i*mainCanvas.getHeight()/7) - ((mainCanvas.getHeight()/15f) - (mainCanvas.getHeight()/20f))/2 + mainCanvas.getHeight()/30f, mainCanvas.getHeight()/15f, mainCanvas.getHeight()/15f);
                                        if (inventoryTiles[i][j]==selectedTile) {
                                            Tile describe = new Tile(inventoryTiles[i][j]);
                                            Rect hitBox1 = new Rect((1.08846*mainCanvas.getWidth() - 0.5*width[0])+(mainCanvas.getWidth()/40f),(1f/30f)*(16*mainCanvas.getHeight() - 15*height[0]),100,100);
                                            describe.setHitBox(hitBox1);
                                            describe.render(gc, tileSet, mainCanvas.getHeight()/20f, 1, null, false, false, Color.BLACK,l, 0, 0, 0);
                                            gc.setTextAlign(TextAlignment.CENTER);
                                            gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, (int)(height[0])/25f));
                                            gc.setFill(Color.BLACK);
                                            gc.fillText(descriptions[selectedTile], (1.08846*mainCanvas.getWidth() - 0.5*width[0])+(mainCanvas.getWidth()/20f), (1f/30f)*(16*mainCanvas.getHeight() - 15*height[0]) + mainCanvas.getHeight()/5,(mainCanvas.getWidth()/1.3f)*.66);
                                            gc.setFill(Color.grayRgb(50));
                                            gc.fillRect(hitBox.x, hitBox.y, hitBox.w, hitBox.h);
                                        }
                                        if (hitBox.contains(new Vector(mouseX, mouseY))) {
                                            if (inventoryTiles[i][j]!=selectedTile) {
                                                gc.setFill(Color.grayRgb(75));
                                                gc.fillRect(hitBox.x, hitBox.y, hitBox.w, hitBox.h);
                                            }
                                            if (mousePressed && mouseButton==0) {
                                                selectedTile = inventoryTiles[i][j];
                                            }
                                        }
                                        tile.render(gc, tileSet, mainCanvas.getHeight()/20f, 1, null, false, false, Color.BLACK,l, 0, 0, 0);
                                    }
                                }
                            }
                            gc.setFill(Color.WHITESMOKE);
                            gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, (int)(height[0])/25f));
                            gc.fillRoundRect((mainCanvas.getWidth()/2) - (width[0]/2)/2, (mainCanvas.getHeight()/2) - (height[0])/2 - ((height[0])/20)/2, width[0]/2, height[0]/20, 20, 20);
                            gc.strokeRoundRect((mainCanvas.getWidth()/2) - (width[0]/2)/2, (mainCanvas.getHeight()/2) - (height[0])/2 - ((height[0])/20)/2, width[0]/2, height[0]/20, 20, 20);
                            gc.setFill(Color.BLACK);
                            gc.setTextAlign(TextAlignment.CENTER);
                            gc.fillText("Inventory!?",mainCanvas.getWidth()/2,(mainCanvas.getHeight()/2) - (height[0])/2 - ((height[0])/20)/2 + ((height[0])/28));
                            if (height[0]<0 && cancelInventory){
                                cancelInventory=false;
                                inventory=false;
                                stop();
                            }
                            gc.setLineWidth(1);
                        }
                    }.start();
                }
            }
        });
        mainCanvas.setOnMouseDragged(arg0 -> {
            if(arg0.isPrimaryButtonDown()){
                mouseButton=0;
            }
            if(arg0.isSecondaryButtonDown()){
                mouseButton=1;
            }
            if(arg0.isMiddleButtonDown()){
                mouseButton=2;
            }
            mouseX=arg0.getX();
            mouseY=arg0.getY();
            mousePressed=true;
        });
        mainCanvas.setOnMousePressed(arg0 -> {
            if(arg0.isPrimaryButtonDown()){
                mouseButton=0;
            }
            if(arg0.isSecondaryButtonDown()){
                mouseButton=1;
            }
            if(arg0.isMiddleButtonDown()){
                mouseButton=2;
            }
            mouseX=arg0.getX();
            mouseY=arg0.getY();
            mousePressed=true;
        });
        mainCanvas.setOnMouseReleased(arg0 -> {
            mouseX=arg0.getX();
            mouseY=arg0.getY();
            mousePressed=false;
        });
        mainCanvas.setOnMouseDragReleased(arg0 -> {
            mouseX=arg0.getX();
            mouseY=arg0.getY();
            mousePressed=false;
        });
        final int[] fps = {0};
        final int[] count = {0};
        final long[] pre = {0};
        l = new Level(6,6);
        gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, 25));

        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                try{
                if (endAll) stop();
                int centerX = 0;
                int centerY = 0;
                for (int i = 0; i < l.t[0].length; i++) {
                    for (int j = 0; j < l.t.length; j++) {
                        try {
                            if (l.t[j][i].hitBox.isTouching(new Rect(mainCanvas.getWidth() / 2, mainCanvas.getHeight() / 2, 5, 5))) {
                                centerX = j;
                                centerY = i;
                            }
                        } catch (NullPointerException ignored) {
                        }
//                        if(thumbnailRect.isTouching(l.t[j][i].hitBox)){
//                            out.append(l.t[j][i].id).append(",");
//                            count++;
//                        }
                    }
                }
                double tileSize = mainCanvas.getHeight() / 20f;
                Rect thumbnailRect = new Rect(mainCanvas.getWidth() / 2 - thumbnailWidth / 2, mainCanvas.getHeight() / 2 - thumbnailHeight / 2, thumbnailWidth, thumbnailHeight);
                double xPos = thumbnailRect.x;
                double yPos = thumbnailRect.y;
                double cornerX = ((centerX - 8) * tileSize) + scrollX;
                double cornerY = ((centerY - 6) * tileSize) + scrollY;
                offset = new Vector((xPos - cornerX) / tileSize, (yPos - cornerY) / tileSize);
                thumbnailWidth = tileSize * 15;
                thumbnailHeight = tileSize * 11;
                scrollX += xa;
                scrollY += ya;
                gc.setFill(Color.GRAY);
                gc.fillRect(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight());
                int MX = (int) Math.floor((mouseX - scrollX) / tileSize);
                int MY = (int) Math.floor((mouseY - scrollY) / tileSize);
                int mouseXAligned = (int) ((MX * tileSize) + scrollX);
                int mouseYAligned = (int) ((MY * tileSize) + scrollY);
                if (editing || inventory) {
                    l.render(scrollX, scrollY, tileSize, gc, mainCanvas.getWidth(), mainCanvas.getHeight(), tileSet);
                    if (mousePressed && mouseButton == 1 && editing) {
                        editTile = l.t[MX][MY];
                    }
                } else {
                    l.render(scrollX, scrollY, tileSize, gc, mainCanvas.getWidth(), mainCanvas.getHeight(), tileSet);
                    gc.setStroke(Color.WHITESMOKE);
                    gc.setLineWidth(1);
                    gc.strokeRect(mouseXAligned, mouseYAligned, tileSize, tileSize);
                    switch (tool) {
                        case (0):
                            Tile highlightTile = new Tile(selectedTile);
                            highlightTile.setHitBox(new Rect(mouseXAligned, mouseYAligned, tileSize, tileSize));
                            highlightTile.render(gc, tileSize, .5f);
                            try {
                                Tile t = l.t[MX][MY];
                            } catch (ArrayIndexOutOfBoundsException e) {
                                gc.setStroke(Color.RED);
                                gc.strokeLine(highlightTile.hitBox.x, highlightTile.hitBox.y, highlightTile.hitBox.x + highlightTile.hitBox.w, highlightTile.hitBox.y + highlightTile.hitBox.h);
                                gc.strokeLine(highlightTile.hitBox.x + highlightTile.hitBox.w, highlightTile.hitBox.y, highlightTile.hitBox.x, highlightTile.hitBox.y + highlightTile.hitBox.h);
                            }
                            mainCanvas.setCursor(Cursor.DISAPPEAR);
                            break;
                        case (1):
                            selectRect = new Rect((selectionStart.x * tileSize) + scrollX, (selectionStart.y * tileSize) + scrollY, selectionSize.x * tileSize, selectionSize.y * tileSize);
                            if (tool == 1)
                                selectRect.render(gc);
                            int rectX = (int) Math.min(selectionStart.x, selectionSize.x + selectionStart.x);
                            int rectY = (int) Math.min(selectionStart.y, selectionSize.y + selectionStart.y);
                            int rectXC = (int) Math.max(selectionStart.x, selectionSize.x + selectionStart.x);
                            int rectYC = (int) Math.max(selectionStart.y, selectionSize.y + selectionStart.y);
                            selectRect = new Rect(rectX, rectY, rectXC - rectX, rectYC - rectY);
                            mainCanvas.setCursor(Cursor.CROSSHAIR);
                            break;
                        case (2):
                            if (tileOneForWire != null) {
                                try {
                                    Tile t = l.t[MX][MY];
                                    gc.setStroke(Color.BLACK);
                                    gc.strokeLine(t.hitBox.x, t.hitBox.y + (t.hitBox.h / 2) - (tileSize / 20), tileOneForWire.hitBox.x + tileOneForWire.hitBox.w - (tileSize / 10), tileOneForWire.hitBox.y + (tileOneForWire.hitBox.h / 2) - (tileSize / 20));
                                    tileOneForWire.hitBox.render(gc);
                                    Vector newInput = new Vector(Math.round((tileOneForWire.hitBox.x - scrollX) / tileSize), Math.round((tileOneForWire.hitBox.y - scrollY) / tileSize));
                                    (new Rect(newInput.x * tileSize + scrollX, newInput.y * tileSize + scrollY, tileSize, tileSize)).render(gc);
                                } catch (ArrayIndexOutOfBoundsException ignored) {
                                }
                            }
                            for (int i = 0; i < l.t.length; i++) {
                                for (int j = 0; j < l.t[i].length; j++) {
                                    if (l.t[i][j].id >= 17 && l.t[i][j].id <= 26) {
                                        if (l.t[i][j].inputs != null) {
                                            for (int k = 0; k < l.t[i][j].inputs.length; k++) {
                                                Vector v = l.t[i][j].inputs[k];
                                                Tile t = l.t[(int) v.x][(int) v.y];
                                                if (t.id < 17 || t.id > 26) {
                                                    Vector[] temp = l.t[i][j].inputs.clone();
                                                    l.t[i][j].inputs = new Vector[l.t[i][j].inputs.length - 1];
                                                    for (int m = 0, n = 0; m < l.t[i][j].inputs.length; m++) {
                                                        if (temp[m] == v) continue;
                                                        l.t[i][j].inputs[n++] = temp[m];
                                                    }
                                                    continue;
                                                }
                                                if (t.state) {
                                                    gc.setStroke(Color.RED);
                                                } else
                                                    gc.setStroke(Color.BLACK);
                                                gc.strokeLine(l.t[i][j].hitBox.x, l.t[i][j].hitBox.y + ((k + 1) * (tileSize / (l.t[i][j].inputs.length + 1))) - (tileSize / 20), t.hitBox.x + t.hitBox.w - (tileSize / 10), t.hitBox.y + (t.hitBox.h / 2) - (tileSize / 20));
                                            }
                                        }
                                    }
                                }
                            }
                    }
                    if (!mousePressed) {
                        if (selecting) {
                            selecting = false;
                            selection = true;
                        }
                        if (tool == 2) {
                            try {
                                if (tileOneForWire != null) {
                                    if (tileOneForWire != l.t[MX][MY]) {
                                        if ((l.t[MX][MY].id >= 17 && l.t[MX][MY].id <= 26)) {
                                            Vector newInput = new Vector(Math.round((tileOneForWire.hitBox.x - scrollX) / tileSize), Math.round((tileOneForWire.hitBox.y - scrollY) / tileSize));
                                            for (int i = 0; i < l.t[MX][MY].inputs.length; i++) {
                                                if (l.t[MX][MY].inputs[i].x == newInput.x && l.t[MX][MY].inputs[i].y == newInput.y) {
                                                    gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, 25));
                                                    gc.setFill(Color.WHITE);
                                                    gc.setTextAlign(TextAlignment.LEFT);
                                                    gc.fillText("FPS:" + fps[0], 0, 25);
                                                    tileOneForWire = null;
                                                    return;
                                                }
                                            }
                                            Action a = new Action(new Vector(MX, MY));
                                            Tile[][] old = new Tile[1][1];
                                            old[0][0] = l.t[MX][MY].copy();
                                            a.setOld(old);
                                            Vector[] tempInputs = l.t[MX][MY].inputs.clone();
                                            l.t[MX][MY].inputs = new Vector[tempInputs.length + 1];
                                            System.arraycopy(tempInputs, 0, l.t[MX][MY].inputs, 0, tempInputs.length);
                                            l.t[MX][MY].inputs[l.t[MX][MY].inputs.length - 1] = newInput;
                                            Tile[][] change = new Tile[1][1];
                                            change[0][0] = l.t[MX][MY].copy();
                                            a.setNew(change);
                                            ArrayList<Action> toRemove = new ArrayList<Action>();
                                            for (int i = actionIndex + 1; i < actions.size(); i++) toRemove.add(actions.get(i));
                                            actions.removeAll(toRemove);
                                            actions.add(a);
                                            actionIndex = actions.size() - 1;
                                        }
                                        tileOneForWire = null;
                                    }
                                }
                            } catch (ArrayIndexOutOfBoundsException ignored) {
                            }
                        }
                    }
                    if (mousePressed)
                        if (mouseButton == 0) {
                            try {
                                if (tool == 0 && l.t[MX][MY].id != selectedTile) {
                                    Action a = new Action(new Vector(MX, MY));
                                    Tile[][] old = new Tile[1][1];
                                    old[0][0] = l.t[MX][MY].copy();
                                    Tile[][] change = new Tile[1][1];
                                    change[0][0] = new Tile(selectedTile);
                                    a.setNew(change);
                                    a.setOld(old);
                                    l.t[MX][MY].id = selectedTile;
                                    l.t[MX][MY].inputs = new Vector[0];
                                    l.t[MX][MY].state = false;
                                    l.t[MX][MY].lightIntensity = Color.color(0, 0, 0);
                                    for (int i = 0; i < l.t.length; i++) {
                                        for (int j = 0; j < l.t[i].length; j++) {
                                            for (int k = 0; k < l.t[i][j].inputs.length; k++) {
                                                Vector v = l.t[i][j].inputs[k];
                                                Tile t = l.t[(int) v.x][(int) v.y];
                                                if (t.id < 17 || t.id > 26) {
                                                    Vector[] temp = l.t[i][j].inputs.clone();
                                                    l.t[i][j].inputs = new Vector[l.t[i][j].inputs.length - 1];
                                                    for (int m = 0, n = 0; m < l.t[i][j].inputs.length; m++) {
                                                        if (temp[m] == v) continue;
                                                        l.t[i][j].inputs[n++] = temp[m];
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    ArrayList<Action> toRemove = new ArrayList<Action>();
                                    for (int i = actionIndex + 1; i < actions.size(); i++) toRemove.add(actions.get(i));
                                    actions.removeAll(toRemove);
                                    actions.add(a);
                                    actionIndex = actions.size() - 1;
                                }
                            } catch (ArrayIndexOutOfBoundsException ignored) {
                            }
                            if (tool == 1) {
                                if (selection) {
                                    if (!(selectRect).isTouching(new Rect(mouseX, mouseY, 10, 10))) {
                                        selection = false;
                                        selecting = true;
                                        selectionStart = new Vector(MX, MY);
                                        selectionSize = new Vector(0, 0);
                                    }
                                } else if (selecting) {
                                    selectionSize = new Vector((MX + 1) - selectionStart.x, (MY + 1) - selectionStart.y);
                                } else {
                                    selecting = true;
                                    selectionStart = new Vector(MX, MY);
                                    selectionSize = new Vector(0, 0);
                                }
                            }
                            if (tool == 2) {
                                if (tileOneForWire == null && ((l.t[MX][MY].id >= 17 && l.t[MX][MY].id <= 20) || l.t[MX][MY].id >= 23 && l.t[MX][MY].id <= 26)) {
                                    tileOneForWire = l.t[MX][MY];
                                }
                            }
                        } else if (mouseButton == 1) {
                            if (tool == 0) {
                                editing = true;
                                editTile = l.t[MX][MY];
                                animationStartTime = System.nanoTime();
                                new AnimationTimer() {
                                    public void handle(long currentNanoTime) {
                                        double t = (currentNanoTime - animationStartTime) / 1000000000.0;
                                        int speed = 1000;
                                        int sizeMult = 3;
                                        if (t * speed > mainCanvas.getWidth() / sizeMult && !cancelEditing) {
                                            t = mainCanvas.getWidth() / sizeMult / speed;
                                        }
                                        gc.setFill(Color.color(150 / 255f, 150 / 255f, 150 / 255f, 150 / 255f));
                                        gc.setStroke(Color.BLACK);
                                        gc.setLineWidth(5);
                                        if (!cancelEditing) {
                                            gc.fillRect(mainCanvas.getWidth() - (t * speed), 0, mainCanvas.getWidth() / sizeMult, mainCanvas.getHeight());
                                            gc.strokeRect(mainCanvas.getWidth() - (t * speed), 0, mainCanvas.getWidth() / sizeMult, mainCanvas.getHeight());
                                        } else {
                                            gc.fillRect((mainCanvas.getWidth() * .66) + (t * speed), 0, mainCanvas.getWidth() / sizeMult, mainCanvas.getHeight());
                                            gc.strokeRect(mainCanvas.getWidth() * .66 + (t * speed), 0, mainCanvas.getWidth() / sizeMult, mainCanvas.getHeight());
                                        }

                                        if (t * speed > mainCanvas.getWidth() / sizeMult && cancelEditing) {
                                            cancelEditing = false;
                                            editing = false;
                                            editTile = null;
                                            stop();
                                        }
                                        gc.setLineWidth(1);
                                    }
                                }.start();
                            } else if (tool == 1) {
                                Action a = new Action(new Vector(selectRect.x, selectRect.y));
                                Tile[][] old = new Tile[(int) selectRect.w][(int) selectRect.h];
                                Tile[][] change = new Tile[(int) selectRect.w][(int) selectRect.h];
                                int count = 0;
                                for (double i = selectRect.x; i < selectRect.x + selectRect.w; i++) {
                                    for (double j = selectRect.y; j < selectRect.y + selectRect.h; j++) {
                                        old[(int) i - (int) selectRect.x][(int) j - (int) selectRect.y] = l.t[(int) i][(int) j];
                                        change[(int) i - (int) selectRect.x][(int) j - (int) selectRect.y] = new Tile(selectedTile);
                                        if (l.t[(int) i][(int) j].id == selectedTile) count++;
                                        l.t[(int) i][(int) j] = new Tile(selectedTile);
                                    }
                                }
                                if (count != selectRect.w * selectRect.h) {
                                    a.setOld(old);
                                    a.setNew(change);
                                    ArrayList<Action> toRemove = new ArrayList<Action>();
                                    for (int i = actionIndex + 1; i < actions.size(); i++) toRemove.add(actions.get(i));
                                    actions.removeAll(toRemove);
                                    actions.add(a);
                                    actionIndex = actions.size() - 1;
                                }
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
                //Draw all the data about the thumbnail, including the screen and offset.
//                thumbnailRect.render(gc);
//                gc.setStroke(Color.RED);
//                gc.strokeLine(cornerX,cornerY,cornerX+(offset.x*tileSize),cornerY+(offset.y*tileSize));
                gc.setFont(Font.font("Cambay", FontWeight.BOLD, FontPosture.ITALIC, 25));
                gc.setFill(Color.WHITE);
                gc.setTextAlign(TextAlignment.LEFT);
                gc.fillText("FPS:" + fps[0], 0, 25);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }

    static public final double map(double value,
                                   double istart,
                                   double istop,
                                   double ostart,
                                   double ostop) {
        return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
    }

    public void loadLevel(File f){
        this.levelName=f;
        LevelParser lp = null;
        try {
            lp = new LevelParser(f);
            lp.loadLevel(f);
        }catch(IOException ignored){}
        l = (Level)lp.get("levelData");
        l.calculateLighting(500);
    }

    public void reload(){
        loadLevel(levelName);
    }

    private String[] saveLevel(Canvas mainCanvas){
        String[] outPut = new String[4];
        int[] levelData = new int[l.t.length*l.t[0].length + 3];
        levelData[0] = l.screenX();
        levelData[1] = l.screenY();
        for(int i=0;i<l.t.length;i++){
            for(int j=0;j<l.t[i].length;j++){
                levelData[(l.t.length * j + i) + 2] = l.t[i][j].id;
            }
        }
        StringBuilder out = new StringBuilder("[").append(l.screenX()).append(",").append(l.screenY()).append(",");
        for(int j=0;j<l.t[0].length;j++) {
            for (int i = 0; i < l.t.length; i++) {
                out.append(l.t[i][j].id);
                if(l.t[i][j].id==12) out.append("{text:").append(l.t[i][j].text).append("}");
                if(l.t[i][j].id>=17 && l.t[i][j].id<=26 && l.t[i][j].inputs.length>0) {
                    StringBuilder vectors = new StringBuilder("{inputs:");
                    for(int k=0;k<l.t[i][j].inputs.length;k++){
                        vectors.append(l.t[i][j].inputs[k].x).append(" ").append(l.t[i][j].inputs[k].y);
                        if(k != l.t[i][j].inputs.length-1) vectors.append(" ");
                    }
                    out.append(vectors).append("}");
                }
                if (!(i==l.t.length-1&&j==l.t[i].length-1)) out.append(",");
            }
        }
        out.append("]");
        System.out.println("levelData="+out);
        outPut[0]="levelData="+out;
        Rect thumbnailRect = new Rect(mainCanvas.getWidth()/2 - thumbnailWidth/2,mainCanvas.getHeight()/2 - thumbnailHeight/2, thumbnailWidth, thumbnailHeight);
        out = new StringBuilder("[");
        int count = 0;
        int centerX = 0;
        int centerY = 0;
        for(int i = 0;i< l.t[0].length;i++){
            for(int j=0;j<l.t.length;j++){
                if(l.t[j][i].hitBox.isTouching(new Rect(mainCanvas.getWidth()/2,mainCanvas.getHeight()/2,5,5))){
                    centerX = j;
                    centerY = i;
                }
//                        if(thumbnailRect.isTouching(l.t[j][i].hitBox)){
//                            out.append(l.t[j][i].id).append(",");
//                            count++;
//                        }
            }
        }
        for(int i = centerY-6;i< centerY+7;i++) {
            for (int j = centerX-8; j < centerX+9; j++) {
                try {
                    out.append(l.t[j][i].id).append(",");
                }catch (ArrayIndexOutOfBoundsException e){
                    out.append("0,");
                }
                count++;
            }
        }
        out.deleteCharAt(out.length()-1);
        out.append("]");
        System.out.println("thumbnail="+out);
        outPut[1]="thumbnail="+out;
        double xPos = (centerX*(mainCanvas.getHeight()/20f))-scrollX;
        double yPos = (centerY*(mainCanvas.getHeight()/20f))-scrollY;
        System.out.println("offset=["+-offset.x+","+-offset.y+"]");
        outPut[2]="offset=["+-offset.x+","+-offset.y+"]";
        outPut[3]="name=\"Untitled\"";
        return outPut;
    }

    public Pane getRootPane() {
        return rootPane ;
    }
}
