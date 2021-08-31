package com.metype.game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class OptionsScreen {

    HBox rootPane;

    public OptionsScreen() {

        try {
            rootPane = new FXMLLoader(HelloApplication.class.getResource("workingOptionsScreen.fxml")).load();
        } catch (IOException ignored){

        }
    }

    public Pane getRootPane() {
        return rootPane ;
    }
}