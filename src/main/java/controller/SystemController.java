package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import model.Garden;

public class SystemController {
    private Garden garden;

    @FXML
    private TextArea logArea;

    public void initialize() {
        this.garden = new Garden();
    }

    @FXML
    private void applyHeating() {
        garden.applyHeating();
        logArea.appendText("Heating system activated.\n");
    }

    @FXML
    private void applyLighting() {
        garden.applyLighting();
        logArea.appendText("Lighting system activated.\n");
    }
}
