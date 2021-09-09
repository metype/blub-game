module com.metype.game {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.dyn4j;

    opens com.metype.game to javafx.fxml;
    exports com.metype.game;
}