module com.ironman.compressiontool {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.ironman.compressiontool to javafx.fxml;
    exports com.ironman.compressiontool;
}