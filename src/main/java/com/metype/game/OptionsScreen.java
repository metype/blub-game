package com.metype.game;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class OptionsScreen {

    HBox rootPane;

    public OptionsScreen() {

        try {
            rootPane = new FXMLLoader(getClass().getResource("workingOptionsScreen.fxml")).load();
        } catch (IOException ignored) {
        }
    }

    public Pane getRootPane() {
        return rootPane;
    }
}