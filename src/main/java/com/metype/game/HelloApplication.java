package com.metype.game;

import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Font.loadFont(HelloApplication.class.getResource("fonts/Cambay-BoldItalic.ttf").openStream(), 14);
        MainScreen main = new MainScreen();
        Scene scene = new Scene(main.getRootPane());
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setTitle("The Adventures of Blob");
        stage.setScene(scene);
        stage.show();
        InvalidationListener sceneChangeListener = new InvalidationListener(){
            @Override
            public void invalidated(Observable o) {
                Canvas c;
                try {
                    c = (Canvas) ((HBox) stage.getScene().getRoot()).getChildren().get(0);
                    c.setWidth(stage.getWidth());
                    c.setHeight(stage.getHeight());
                }catch(Exception ignored){};
            }
        };
        stage.getScene().rootProperty().addListener(sceneChangeListener);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if(keyEvent.getCode() == KeyCode.F11){
                stage.setFullScreenExitHint("Hit F11 again to exit fullscreen.");
                stage.setFullScreenExitKeyCombination(new KeyCodeCombination(KeyCode.F11,KeyCombination.SHORTCUT_DOWN));
                stage.setFullScreen(!stage.isFullScreen());
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}