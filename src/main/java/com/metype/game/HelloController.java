package com.metype.game;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class HelloController {

    public Button storyButton;
    public Button editorButton;
    public Button optionsButton;
    public Button customButton;
    public Button exitButton;
    public Button backButton;
    public ListView listView;

    @FXML
    protected void optionsButtonClick() {
        OptionsScreen options = new OptionsScreen();
        optionsButton.getScene().setRoot(options.getRootPane());
    }

    @FXML
    protected void editorButtonClick() {
        final double[] newWidth = {1};
        final double[] newHeight = {1};
        Stage askEditType = new Stage();
        askEditType.initModality(Modality.APPLICATION_MODAL);
        askEditType.setTitle("Choose What To Edit");
        askEditType.setHeight(230);
        askEditType.setWidth(412);
        askEditType.setResizable(false);
        Button newPack = new Button("Create a new pack.");
        newPack.setDisable(true);
        Button newLevel = new Button("Create a new level.");
        Button openLevel = new Button("Load an existing level.");
        Button cancel = new Button("Cancel and return to Menu.");
        newPack.setPrefHeight(1000);
        newLevel.setPrefHeight(1000);
        openLevel.setPrefHeight(1000);
        cancel.setPrefHeight(1000);
        newPack.setPrefWidth(1000);
        newLevel.setPrefWidth(1000);
        openLevel.setPrefWidth(1000);
        cancel.setPrefWidth(1000);
        VBox layout = new VBox(4);
        cancel.setOnAction(actionEvent -> askEditType.close());
        openLevel.setOnAction(actionEvent -> {
            CustomScreen cs = new CustomScreen();
            editorButton.getScene().setRoot(cs.getRootPane());
            askEditType.close();
        });
        newLevel.setOnAction(actionEvent -> {
            Stage popupwindow = new Stage();
            popupwindow.initModality(Modality.APPLICATION_MODAL);
            popupwindow.setTitle("Create A New Level");
            Label horizontalLabel = new Label("Change Width of the level (in screens, a screen is 15 tiles wide)");
            Slider horizontalSlider = new Slider();
            horizontalSlider.setMajorTickUnit(1);
            horizontalSlider.setMinorTickCount(0);
            horizontalSlider.setBlockIncrement(1);
            horizontalSlider.setSnapToTicks(true);
            horizontalSlider.showTickMarksProperty().set(true);
            horizontalSlider.showTickLabelsProperty().set(true);
            horizontalSlider.setMax(15);
            horizontalSlider.setMin(1);
            horizontalSlider.valueProperty().addListener((observableValue, number, t1) -> newWidth[0] = t1.doubleValue());
            Label verticalLabel = new Label("Change Height of the level (in screens, a screen is 11 tiles tall)");
            Slider verticalSlider = new Slider();
            verticalSlider.setMajorTickUnit(1);
            verticalSlider.setMinorTickCount(0);
            verticalSlider.setBlockIncrement(1);
            verticalSlider.setSnapToTicks(true);
            verticalSlider.showTickMarksProperty().set(true);
            verticalSlider.showTickLabelsProperty().set(true);
            verticalSlider.setMax(15);
            verticalSlider.setMin(1);
            verticalSlider.valueProperty().addListener((observableValue, number, t1) -> newHeight[0] = t1.doubleValue());
            Button okay = new Button("Create New Level");
            Button cancel1 = new Button("Cancel");
            okay.setOnAction(actionEvent1 -> {
                EditorScreen editor = new EditorScreen();
                editor.l = new Level((int) newWidth[0], (int) newHeight[0]);
                editorButton.getScene().setRoot(editor.getRootPane());
                popupwindow.close();
                askEditType.close();
            });
            cancel1.setOnAction(actionEvent12 -> {
                popupwindow.close();
                askEditType.close();
                return;
            });
            VBox layout1 = new VBox(5);
            HBox cheating = new HBox(2);
            cheating.getChildren().addAll(cancel1, okay);
            layout1.getChildren().addAll(horizontalLabel, horizontalSlider, verticalLabel, verticalSlider, cheating);
            layout1.setAlignment(Pos.CENTER);
            cheating.setAlignment(Pos.CENTER);
            Scene scene1 = new Scene(layout1, 300, 250);
            popupwindow.setHeight(230);
            popupwindow.setWidth(412);
            popupwindow.resizableProperty().set(false);
            popupwindow.setScene(scene1);
            popupwindow.showAndWait();
        });
        layout.getChildren().addAll(newPack, newLevel, openLevel, cancel);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        askEditType.setScene(scene);
        askEditType.showAndWait();
    }

    @FXML
    protected void customButtonClick() {
        CustomScreen custom = new CustomScreen();
        customButton.getScene().setRoot(custom.getRootPane());
    }

    @FXML
    protected void exitButtonClick() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    protected void backButtonClick() {
        MainScreen main = new MainScreen();
        backButton.getScene().setRoot(main.getRootPane());
    }
}