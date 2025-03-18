package controller;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Garden;
import model.Inventory;
import model.LogSystem;
import model.plants.Plant;
import org.controlsfx.control.RangeSlider;

import java.util.Collections;
import java.util.List;
import java.util.Arrays;

public class AddPlantController {
    @FXML private VBox layout;
    @FXML private ComboBox<String> plantDropdown;
    @FXML private RangeSlider waterRangeSlider, tempRangeSlider;
    @FXML private Label waterValueLabel, tempValueLabel;
    @FXML private Slider sunlightSlider, survivalTimeSlider, growthTimeSlider;
    @FXML private Label sunlightLabel, survivalTimeLabel, growthTimeLabel;
    @FXML private ComboBox<String> pestsDropdown;

    private LogSystem logSystem;
    private InventoryController inventoryController;
     // ✅ Reference to `GardenController`

    public void setContext(LogSystem logSystem, InventoryController inventoryController) {
        this.inventoryController = inventoryController;
        this.logSystem = logSystem;
    }

    @FXML
    public void initialize() {
        plantDropdown.getItems().addAll("Eggplant", "Lettuce", "Lavender", "Corn", "Pumpkin", "Carrot","Watermelon","Grape","Tomato","Strawberry");
        
        // 添加植物选择监听器
        plantDropdown.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updatePestsDropdown(newVal);
            }
        });

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

    private void updatePestsDropdown(String plantName) {
        pestsDropdown.getItems().clear();
        pestsDropdown.getItems().add("None");
        
        // 根据植物类型添加对应的害虫
        switch (plantName) {
            case "Corn" -> pestsDropdown.getItems().addAll("aphids", "corn borers");
            case "Pumpkin" -> pestsDropdown.getItems().addAll("aphids", "squash bugs", "powdery mildew");
            case "Lavender" -> pestsDropdown.getItems().addAll("aphids", "spider mites");
            default -> pestsDropdown.getItems().add("aphids");  // 默认至少对蚜虫敏感
        }
        
        // 默认选择"None"
        pestsDropdown.setValue("None");
    }

    private void addPlant() {
        String plantName = plantDropdown.getValue();
        if (plantName == null) return;

        try {
            System.out.println("✅ Adding plant seed to inventory...");
            int minWater = (int) waterRangeSlider.getLowValue();
            int maxWater = (int) waterRangeSlider.getHighValue();
            int sunlight = (int) sunlightSlider.getValue();
            int minTemp = (int) tempRangeSlider.getLowValue();
            int maxTemp = (int) tempRangeSlider.getHighValue();
            int survivalTime = (int) survivalTimeSlider.getValue();
            int growthTime = (int) growthTimeSlider.getValue();
            String selectedPest = pestsDropdown.getValue();

            List<String> vulnerablePests = switch (plantName) {
                case "Corn" -> Arrays.asList("aphids", "corn borers");
                case "Pumpkin" -> Arrays.asList("aphids", "squash bugs", "powdery mildew");
                case "Lavender" -> Arrays.asList("aphids", "spider mites");
                default -> Collections.singletonList("aphids");
            };

            String growingImagePath = "/images/plants/" + plantName.toLowerCase() + ".png";
            String matureImagePath = "/images/plants/" + plantName.toLowerCase() + "-mature.png";

            Plant plantSeed = (Plant) Class.forName("model.plants." + plantName)
                    .getDeclaredConstructor(String.class, int.class, int.class, int.class, int.class,
                            int.class, int.class, int.class, List.class, String.class, String.class)
                    .newInstance(plantName, minWater, maxWater, growthTime, sunlight, minTemp, maxTemp,
                            survivalTime, vulnerablePests, growingImagePath, matureImagePath);

            Inventory inventory = Inventory.getInstance();

            boolean isUpdate = inventory.hasSeed(plantName); // ✅ Check if the seed exists
            if (isUpdate) {
                inventory.updateSeed(plantSeed);
                logSystem.logEvent("Updated " + plantName + " seed in inventory.");
                System.out.println("🔄 Updated " + plantName + " seed in inventory.");
            } else {
                inventory.addSeed(plantSeed);
                logSystem.logEvent("Added " + plantName + " seed to inventory.");
                System.out.println("✅ Added " + plantName + " seed to inventory.");
            }

            // ✅ Notify InventoryController to show the message
            if (inventoryController != null) {
                inventoryController.showSuccessMessage(plantName, isUpdate);
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
