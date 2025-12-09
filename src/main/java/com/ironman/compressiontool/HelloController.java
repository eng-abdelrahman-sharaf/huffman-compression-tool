package com.ironman.compressiontool;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import java.io.File;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");

        // Optional: filter types
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        // Show the dialog
        File selectedFile = fileChooser.showOpenDialog(null);

        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
