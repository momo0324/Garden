package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.plants.Plant;
import model.Garden;

public class PlantController {
    public ImageView plantImageView;

    @FXML private Label plantNameLabel, waterLabel, sunlightLabel, temperatureLabel;
    @FXML private ProgressBar growthBar;

    @FXML
    private ImageView pestIndicator;

    @FXML
    private Button closeButton;

    private Plant plant;

    private Garden garden;

    public void setPlant(Garden garden, Plant plant) {
        this.garden = garden;
        this.plant = plant;
        updatePlantInfo();
    }

    private void updatePlantInfo() {
        if (plant != null) {
            String plantImagePath = plant.getCurrentImagePath();
            Image image = new Image(getClass().getResource(plantImagePath).toExternalForm());
            plantImageView.setImage(image);

            plantNameLabel.setText(plant.getName());

            waterLabel.setText(plant.getMinWaterRequirement() + " - " + plant.getMaxWaterRequirement() + " ml/day" + " (Current: " + plant.getCurrentWaterLevel() + " ml)");

            sunlightLabel.setText(plant.getSunlightNeeded() + " hrs/day" + " (" + plant.getCurrentGrowthHours() + " hrs)");

            temperatureLabel.setText(plant.getMinIdealTemperature() + " - " + plant.getMaxIdealTemperature() + " °C" + " (Current: " + garden.getCurrentTemperature() + " °C)");

            double growthProgress = (double) plant.getCurrentGrowthHours() / plant.getHoursToGrow();
            growthBar.setProgress(Math.min(1.0, growthProgress));

            if (plant.getCurrentPest() != null) {
                try {
                    Image pestImage = new Image(getClass().getResource("/images/pest2.png").toExternalForm());
                    pestIndicator.setImage(pestImage);
                    pestIndicator.setVisible(true);
                } catch (Exception e) {
                    System.err.println("Error loading pest image: " + e.getMessage());
                }
            } else {
                pestIndicator.setImage(null);
                pestIndicator.setVisible(false);
            }
        }
    }

    @FXML
    private void handleClose() {
        closeButton.getScene().getWindow().hide();
    }
}
