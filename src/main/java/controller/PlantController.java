package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Garden;
import model.plants.Plant;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class PlantController {
    public ImageView plantImageView;

    @FXML private Label plantNameLabel, waterLabel, sunlightLabel, temperatureLabel;
    @FXML private ProgressBar growthBar;

    @FXML
    private ImageView pestIndicator;

    @FXML
    private Button closeButton;

    @FXML
    private Button removeButton;

    private Plant plant;
    private int plantX = -1;
    private int plantY = -1;
    private Garden garden;
    private GardenController gardenController;

    public void setPlant(Plant plant, GardenController controller) {
        this.plant = plant;
        this.garden = Garden.getInstance();
        this.gardenController = controller;
        
        // Find plant position
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 6; j++) {
                if (garden.getPlantAt(i, j) == plant) {
                    this.plantX = i;
                    this.plantY = j;
                    break;
                }
            }
            if (plantX != -1) break;
        }
        updatePlantInfo();
    }

    private void updatePlantInfo() {
        if (plant != null) {
            String plantImagePath = plant.getCurrentImagePath();
            Image image = new Image(getClass().getResource(plantImagePath).toExternalForm());
            plantImageView.setImage(image);
            
            plantNameLabel.setText(plant.getName());
            waterLabel.setText(plant.getMinWaterRequirement() + " - " + plant.getMaxWaterRequirement() + " ml/day");
            sunlightLabel.setText(plant.getSunlightNeeded() + " hrs/day");
            temperatureLabel.setText(plant.getMinIdealTemperature() + " - " + plant.getMaxIdealTemperature() + " °C");

            double growthProgress = (double) plant.getCurrentGrowthHours() / plant.getHoursToGrow();
            growthBar.setProgress(Math.min(1.0, growthProgress));

            if (!plant.getVulnerableToPests().isEmpty()) {
                Image pestImage = new Image(getClass().getResource("/images/pest2.png").toExternalForm());
                pestIndicator.setImage(pestImage);
            } else {
                pestIndicator.setImage(null);
            }
        }
    }

    @FXML
    private void handleClose() {
        closeButton.getScene().getWindow().hide();
    }

    @FXML
    private void handleRemove() {
        if (plant != null && plantX != -1 && plantY != -1) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Removal");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to remove this " + plant.getName() + "?");
            
            // Set button text
            ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Yes");
            ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("No");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Remove plant from garden
                    garden.removePlant(plantX, plantY);
                    // Update garden grid
                    if (gardenController != null) {
                        gardenController.updateGardenGrid();
                    }
                    // Close plant details window
                    Stage stage = (Stage) removeButton.getScene().getWindow();
                    stage.close();
                }
            });
        }
    }
}
