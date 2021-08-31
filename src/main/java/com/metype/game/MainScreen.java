package com.metype.game;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class MainScreen {

    HBox rootPane;

    public MainScreen() {

        try {
            rootPane = new FXMLLoader(HelloApplication.class.getResource("workingTitleScreen.fxml")).load();
        } catch (IOException ignored){

        }
    }

    public Pane getRootPane() {
        return rootPane ;
    }
}