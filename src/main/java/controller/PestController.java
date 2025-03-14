package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import model.Garden;

public class PestController {
    private Garden garden;

    @FXML
    private TextArea logArea;

    public void initialize() {
        this.garden = Garden.getInstance();
    }

    @FXML
    private void applyPestControl() {
        garden.applyPestControl();
        logArea.appendText("Pest control system activated.\n");
    }
}
