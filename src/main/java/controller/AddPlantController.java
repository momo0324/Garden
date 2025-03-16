package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Garden;
import model.LogSystem;
import model.plants.Plant;
import org.controlsfx.control.RangeSlider;

import java.util.Collections;
import java.util.List;

public class AddPlantController {
    @FXML private VBox layout;
    @FXML private ComboBox<String> plantDropdown;
    @FXML private RangeSlider waterRangeSlider, tempRangeSlider;
    @FXML private Label waterValueLabel, tempValueLabel;
    @FXML private Slider sunlightSlider, survivalTimeSlider, growthTimeSlider;
    @FXML private Label sunlightLabel, survivalTimeLabel, growthTimeLabel;
    @FXML private ComboBox<String> pestsDropdown;

    private int x, y;
    private Garden garden;
    private LogSystem logSystem;
    private GardenController gardenController; // ✅ Reference to `GardenController`

    public void setContext(Garden garden, LogSystem logSystem, GardenController gardenController, int x, int y) {
        this.garden = garden;
        this.logSystem = logSystem;
        this.gardenController = gardenController; // ✅ Store reference
        this.x = x;
        this.y = y;
    }

    @FXML
    public void initialize() {
        plantDropdown.getItems().addAll("Eggplant", "Lettuce", "Lavender", "Corn", "Pumpkin", "Carrot");
        pestsDropdown.getItems().addAll("None", "Aphid", "Caterpillar", "CarrotRustFly");

        waterRangeSlider.lowValueProperty().addListener((obs, oldVal, newVal) -> updateRangeLabel(waterRangeSlider, waterValueLabel, "ml/day"));
        waterRangeSlider.highValueProperty().addListener((obs, oldVal, newVal) -> updateRangeLabel(waterRangeSlider, waterValueLabel, "ml/day"));
        tempRangeSlider.lowValueProperty().addListener((obs, oldVal, newVal) -> updateRangeLabel(tempRangeSlider, tempValueLabel, "°C"));
        tempRangeSlider.highValueProperty().addListener((obs, oldVal, newVal) -> updateRangeLabel(tempRangeSlider, tempValueLabel, "°C"));

        sunlightSlider.valueProperty().addListener((obs, oldVal, newVal) -> sunlightLabel.setText(String.format("%.0f hrs/day", newVal)));
        survivalTimeSlider.valueProperty().addListener((obs, oldVal, newVal) -> survivalTimeLabel.setText(String.format("%.0f days", newVal)));
        growthTimeSlider.valueProperty().addListener((obs, oldVal, newVal) -> growthTimeLabel.setText(String.format("%.0f hours", newVal)));

        // ✅ Create "Add Plant" Button Dynamically
        Button confirmButton = new Button("Add Plant");
        confirmButton.getStyleClass().add("add-plant-button");
        confirmButton.setOnAction(e -> addPlant());

        // ✅ Add the button to the layout at the bottom
        HBox buttonContainer = new HBox(confirmButton);
        buttonContainer.setSpacing(15);
        buttonContainer.setAlignment(javafx.geometry.Pos.CENTER);
        layout.getChildren().add(buttonContainer);
    }

    private void updateRangeLabel(RangeSlider slider, Label label, String unit) {
        label.setText(String.format("%.0f - %.0f %s", slider.getLowValue(), slider.getHighValue(), unit));
    }

    private void addPlant() {
        String plantName = plantDropdown.getValue();
        if (plantName == null) return;

        try {
            System.out.println("✅ Adding plant to garden...");
            int minWater = (int) waterRangeSlider.getLowValue();
            int maxWater = (int) waterRangeSlider.getHighValue();
            int sunlight = (int) sunlightSlider.getValue();
            int minTemp = (int) tempRangeSlider.getLowValue();
            int maxTemp = (int) tempRangeSlider.getHighValue();
            int survivalTime = (int) survivalTimeSlider.getValue();
            int growthTime = (int) growthTimeSlider.getValue();
            List<String> pests = Collections.singletonList(pestsDropdown.getValue());

            String imagePath = "/images/plants/" + plantName.toLowerCase() + ".png";
            String matureImagePath = "/images/plants/" + plantName.toLowerCase() + "_mature.png";

            Plant plant = (Plant) Class.forName("model.plants." + plantName)
                    .getDeclaredConstructor(String.class, int.class, int.class, int.class, int.class,
                            int.class, int.class, int.class, List.class, String.class, String.class)
                    .newInstance(plantName, minWater, maxWater, growthTime, sunlight, minTemp, maxTemp,
                            survivalTime, pests, imagePath, matureImagePath);

            if (garden.addPlant(x, y, plant)) {
                logSystem.logEvent("Added " + plantName + " at (" + x + ", " + y + ").\n");
                System.out.println("Added " + plantName + " at (" + x + ", " + y + ").\n");
                gardenController.updateGardenGrid(); // ✅ Update grid after adding plant
            }

            closeDialog();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void closeDialog() {
        Stage stage = (Stage) layout.getScene().getWindow();
        stage.close();
    }
}
