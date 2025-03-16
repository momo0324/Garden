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
import java.util.Arrays;

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
        if (plantName.equals("Corn")) {
            pestsDropdown.getItems().addAll("aphids", "corn borers");
        } else if (plantName.equals("Pumpkin")) {
            pestsDropdown.getItems().addAll("aphids", "squash bugs", "powdery mildew");
        } else if (plantName.equals("Lavender")) {
            pestsDropdown.getItems().addAll("aphids", "spider mites");
        } else {
            pestsDropdown.getItems().add("aphids");  // 默认至少对蚜虫敏感
        }
        
        // 默认选择"None"
        pestsDropdown.setValue("None");
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
            String selectedPest = pestsDropdown.getValue();
            
            // 设置植物可能受到的害虫攻击类型
            List<String> vulnerablePests;
            if (plantName.equals("Corn")) {
                vulnerablePests = Arrays.asList("aphids", "corn borers");
            } else if (plantName.equals("Pumpkin")) {
                vulnerablePests = Arrays.asList("aphids", "squash bugs", "powdery mildew");
            } else if (plantName.equals("Lavender")) {
                vulnerablePests = Arrays.asList("aphids", "spider mites");
            } else {
                vulnerablePests = Arrays.asList("aphids");  // 默认至少对蚜虫敏感
            }

            String imagePath = "/images/plants/" + plantName.toLowerCase() + ".png";
            String matureImagePath = "/images/plants/" + plantName.toLowerCase() + "_mature.png";

            Plant plant = (Plant) Class.forName("model.plants." + plantName)
                    .getDeclaredConstructor(String.class, int.class, int.class, int.class, int.class,
                            int.class, int.class, int.class, List.class, String.class, String.class)
                    .newInstance(plantName, minWater, maxWater, growthTime, sunlight, minTemp, maxTemp,
                            survivalTime, vulnerablePests, imagePath, matureImagePath);

            // 如果选择了害虫（不是"None"），并且植物对该害虫敏感，则感染植物
            if (selectedPest != null && !selectedPest.equals("None") && plant.isVulnerableTo(selectedPest)) {
                plant.applyPestDamage(selectedPest);
                System.out.println(plantName + " is infected with " + selectedPest);
            }

            if (garden.addPlant(x, y, plant)) {
                logSystem.logEvent("Added " + plantName + " at (" + x + ", " + y + ").\n");
                System.out.println("Added " + plantName + " at (" + x + ", " + y + ").\n");
                gardenController.updateGardenGrid();
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
