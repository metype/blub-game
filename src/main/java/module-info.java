module com.metype.game {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.metype.game to javafx.fxml;
    exports com.metype.game;
}