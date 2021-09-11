package com.metype.game;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javafx.scene.paint.*;
import javafx.scene.canvas.*;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class CustomScreen {

    HBox rootPane;

    public CustomScreen() {

        try {
            rootPane = new FXMLLoader(HelloApplication.class.getResource("workingCustomScreen.fxml")).load();
        } catch (IOException ignored){

        }
        ListView<ToggleButton> lv = (ListView<ToggleButton>)((GridPane)(rootPane.getChildren().get(0))).getChildren().get(2);
        ToggleGroup tg = new ToggleGroup();
        ObservableList<ToggleButton> itemList = lv.getItems();
        File levelDirectory = new File(System.getProperty("user.dir") + File.separator + "levels" + File.separator);
        if(levelDirectory.list().length==0) {
            try {
                rootPane = new FXMLLoader(HelloApplication.class.getResource("workingCustomNothingScreen.fxml")).load();
            } catch (IOException ignored){

            }
            return;
        }
        for(File f : levelDirectory.listFiles()) {
            if(!f.getName().endsWith(".lev") && !f.getName().endsWith(".zip")) continue;
            LevelParser lp = null;
            try {
                if(!f.getName().endsWith(".zip")) {
                    lp = new LevelParser(f);
                }
            }catch(IOException ignored){}
            ToggleButton b;
            if(f.getName().endsWith(".zip")){
                b = new ToggleButton(f.getName().split(".zip")[0]);
            }else {
                b = new ToggleButton(lp.get("name").toString());
            }
            b.setMaxWidth(1000);
            b.setToggleGroup(tg);
            LevelParser finalLp = lp;
            b.addEventHandler(ActionEvent.ANY, (ActionEvent e) -> {
                if(((ToggleButton)e.getSource()).isSelected()){
                    ((Label)((GridPane)(rootPane.getChildren().get(0))).getChildren().get(6)).setText(b.getText());
                    Canvas c = (Canvas)((GridPane)(rootPane.getChildren().get(0))).getChildren().get(4);
                    ((GridPane)(rootPane.getChildren().get(0))).getChildren().get(5).setVisible(false);
                    GraphicsContext gc = c.getGraphicsContext2D();
                    gc.setFill(Color.GRAY);
                    gc.fillRect(0, 0, c.getWidth(), c.getHeight());
                    String[] thumb = null;
                    String[] off = null;
                    try {
                        thumb = (String[]) finalLp.get("thumbnail");
                        off = (String[]) finalLp.get("offset");
                    }catch(NullPointerException ignored){}
                    int j = 0;
                    if(thumb!=null)
                    for(int i=1;i<thumb.length-1;i++){
                        int id = 0;
                        if(!thumb[i].equalsIgnoreCase("")) {
                            id = Integer.parseInt(thumb[i]);
                        }
                        int y = (int)((Math.floor(i/17.1f) * c.getHeight()/11) + Float.parseFloat(off[2]) * c.getHeight()/11.0);
                        int x = (int)(((i-1)%17 * c.getWidth()/15) + Float.parseFloat(off[1]) * c.getWidth()/15.0);
                        Tile tile = new Tile(id);
                        tile.setHitBox(new Rect(x,y,c.getHeight()/11,c.getHeight()/11));
                        tile.render(gc,(int)(c.getHeight()/10.45));
                    }
                    else{
                        gc.setFill(Color.BLACK);
                        gc.setTextAlign(TextAlignment.CENTER);
                        try {
                            gc.setFont(Font.loadFont(HelloApplication.class.getResource("fonts/Cambay-BoldItalic.ttf").openStream(), 25));
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        gc.fillText("No Thumbnail", c.getWidth()/2, c.getHeight()/2, c.getWidth()/3);
                    }
                    ((GridPane)(rootPane.getChildren().get(0))).getChildren().get(7).setDisable(false);
                    ((GridPane)(rootPane.getChildren().get(0))).getChildren().get(8).setDisable(false);
                    ((Button)((GridPane)(rootPane.getChildren().get(0))).getChildren().get(7)).setOnAction(new EventHandler<ActionEvent>() {
                        @Override public void handle(ActionEvent e) {
                            PlayScreen play = new PlayScreen();
                            play.tileSize = rootPane.getHeight()/11f;
                            if(f.getName().endsWith(".zip"))
                                play.loadPack(f);
                            else
                                play.loadLevel(f);
                            rootPane.getScene().setRoot(play.getRootPane());
                        }
                    });
                    ((Button)((GridPane)(rootPane.getChildren().get(0))).getChildren().get(8)).setOnAction(new EventHandler<ActionEvent>() {
                        @Override public void handle(ActionEvent e) {
                            EditorScreen edit = new EditorScreen();
                            edit.loadLevel(finalLp.file());
                            rootPane.getScene().setRoot(edit.getRootPane());
                        }
                    });
                }else{
                    ((GridPane)(rootPane.getChildren().get(0))).getChildren().get(5).setVisible(true);
                    ((Label)((GridPane)(rootPane.getChildren().get(0))).getChildren().get(6)).setText("No Selected Level");
                    ((GridPane)(rootPane.getChildren().get(0))).getChildren().get(7).setDisable(true);
                    ((GridPane)(rootPane.getChildren().get(0))).getChildren().get(8).setDisable(true);
                    Canvas c = (Canvas)((GridPane)(rootPane.getChildren().get(0))).getChildren().get(4);
                    GraphicsContext gc = c.getGraphicsContext2D();
                    gc.setFill(Color.GRAY);
                    gc.fillRect(0, 0, c.getWidth(), c.getHeight());
                }
            });
            itemList.add(b);
        }
        lv.setItems(itemList);
    }

    public Pane getRootPane() {
        return rootPane ;
    }
}
