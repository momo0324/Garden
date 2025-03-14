package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.plants.Plant;

public class PlantController {
    public ImageView plantImageView;

    @FXML private Label plantNameLabel, waterLabel, sunlightLabel, temperatureLabel;
    @FXML private ProgressBar growthBar;

    @FXML
    private ImageView pestIndicator;

    @FXML
    private Button closeButton;

    private Plant plant;

    public void setPlant(Plant plant) {
        this.plant = plant;
        updatePlantInfo();
    }

    private void updatePlantInfo() {
        if (plant != null) {
            String plantImagePath = plant.getCurrentImagePath();
            Image image = new Image(getClass().getResource(plantImagePath).toExternalForm());
            plantImageView.setImage(image);
            // ✅ Set plant name
            plantNameLabel.setText(plant.getName());

            // ✅ Display min-max water requirement
            waterLabel.setText(plant.getMinWaterRequirement() + " - " + plant.getMaxWaterRequirement() + " ml/day");

            // ✅ Display sunlight needed
            sunlightLabel.setText(plant.getSunlightNeeded() + " hrs/day");

            // ✅ Display temperature range
            temperatureLabel.setText(plant.getMinIdealTemperature() + " - " + plant.getMaxIdealTemperature() + " °C");

            // Set growth progress
            double growthProgress = (double) plant.getCurrentGrowthHours() / plant.getHoursToGrow();
            growthBar.setProgress(Math.min(1.0, growthProgress));

            // Pest indicator (show icon if plant is vulnerable)
            if (!plant.getVulnerableToPests().isEmpty()) {
                System.out.println("here!!!");
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
}
