module com.metype.game {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;

    opens com.metype.game to javafx.fxml;
    exports com.metype.game;
}