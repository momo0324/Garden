package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Garden;
import model.LogSystem;
import model.plants.Plant;
import javafx.animation.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import util.TimeManager;
import javafx.geometry.Insets;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GardenController {
    public BorderPane mainLayout;
    private Garden garden;
    private Timeline simulationTimeline;
    private LogSystem logSystem;
    private boolean isSelectMode = false; // 新增：选择模式标志
    private Button selectedCell = null; // 新增：当前选中的格子
    @FXML
    private ImageView backgroundImage;  // ✅ Reference to background

    @FXML
    private GridPane gardenGrid;

    @FXML
    private TextField plantNameInput, xInput, yInput;

    @FXML
    private Button waterButton, lightButton, tempButton, pestControlButton, harvestButton, logButton;

    @FXML
    private Slider speedSlider;

    @FXML
    private TextArea logArea;

    @FXML
    private ImageView sprinkler1, sprinkler2, sprinkler3, sprinkler4;

    @FXML
    private StackPane rootPane; // ✅ Add an FXML reference to your root Pane
    @FXML
    private Label dayHourLabel; // Reference to FXML label for displaying time

    @FXML
    private Button selectButton;  // 添加按钮引用

    public void initialize() {
        garden = Garden.getInstance();
        garden.initializeGarden(); // Ensures plants are placed at the start
        setupGardenGrid();
        setupSprinklers();
        updateGardenGrid(); // Ensures plants are displayed at startup
        setupLogArea();

        // ✅ Link simulation speed slider
        speedSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            double speedFactor = newValue.doubleValue();
            TimeManager.setSimulationSpeed(speedFactor);
            logArea.appendText("Simulation speed set to: " + speedFactor + "x\n");
        });

        // Start simulation
        TimeManager.startSimulation(garden);
        startSimulation();
        logSystem=LogSystem.getInstance();
    }

    private void startSimulation() {
        simulationTimeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> runSimulationStep()));
        simulationTimeline.setCycleCount(Timeline.INDEFINITE);
        simulationTimeline.play();
        // update UI time
        Timeline simulationTimeline2 = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateSimulationTime()));
        simulationTimeline2.setCycleCount(Timeline.INDEFINITE);
        simulationTimeline2.play();
    }

    private void runSimulationStep() {
        garden.applyWatering();
        garden.applyPestControl();
        garden.applyHeating();
        garden.applyLighting();
        garden.updatePlants();
        garden.harvestPlants();
        updateGardenGrid();
        logArea.appendText("Simulation step completed.\n");
    }

    @FXML
    private void stopSimulation() {
        simulationTimeline.stop();
        logArea.appendText("Simulation stopped.\n");
    }

    // 更新 UI 时间的方法
    public void updateSimulationTime() {
        int day = TimeManager.getSimulatedHour()/24;
        int hour = TimeManager.getSimulatedHour()%24;

        // 确保在 JavaFX 线程更新 UI
        Platform.runLater(() -> {
            dayHourLabel.setText("Day " + day + ", Hour " + hour);
        });
    }

    private void setupGardenGrid() {
        gardenGrid.getChildren().clear();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 6; col++) {
                final int x = row;
                final int y = col;

                Button cell = new Button();
                cell.setMinSize(50, 50);
                cell.setStyle("-fx-background-color: white; -fx-border-color: black;");
                cell.setOnAction(e -> handleCellClick(cell, x, y));
                gardenGrid.add(cell, col, row);
            }
        }
    }

    private void setupLogArea() {
        logArea = new TextArea();
    }

    private void setupSprinklers() {
        try {
            String sprinklerImagePath = getClass().getResource("/images/sprinklers.png").toExternalForm();
            Image sprinklerImage = new Image(sprinklerImagePath);

            sprinkler1 = new ImageView(sprinklerImage);
            sprinkler2 = new ImageView(sprinklerImage);
            sprinkler3 = new ImageView(sprinklerImage);
            sprinkler4 = new ImageView(sprinklerImage);

            sprinkler1.setFitWidth(30);
            sprinkler1.setFitHeight(30);
            sprinkler2.setFitWidth(30);
            sprinkler2.setFitHeight(30);
            sprinkler3.setFitWidth(30);
            sprinkler3.setFitHeight(30);
            sprinkler4.setFitWidth(30);
            sprinkler4.setFitHeight(30);

            gardenGrid.add(sprinkler1, 1, 1);
            gardenGrid.add(sprinkler2, 1, 4);
            gardenGrid.add(sprinkler3, 4, 1);
            gardenGrid.add(sprinkler4, 4, 4);

        } catch (NullPointerException e) {
            System.err.println("Error: Sprinkler image not found! Ensure it's inside 'src/main/resources/images/'.");
        }
    }

    public void updateGardenGrid() {
        // Remove only plant images while keeping sprinklers
        gardenGrid.getChildren().removeIf(node -> node instanceof ImageView && node != sprinkler1 && node != sprinkler2 && node != sprinkler3 && node != sprinkler4);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 6; col++) {
                Plant plant = garden.getPlantAt(row, col);

                if (plant != null) {
                    // Ensure lowercase filenames to match image names
                    String plantImagePath = plant.getCurrentImagePath();
                    Image plantImage;

                    try {
                        plantImage = new Image(getClass().getResource(plantImagePath).toExternalForm());
                    } catch (NullPointerException e) {
                        System.err.println("Image not found for " + plant.getClass().getSimpleName() + ", using default.");
                        plantImage = new Image(getClass().getResource("/images/plants/default.png").toExternalForm());
                    }

                    ImageView plantView = new ImageView(plantImage);
                    plantView.setFitWidth(20);
                    plantView.setFitHeight(20);

                    gardenGrid.add(plantView, col, row);
                }
            }
        }
    }

    private void handleCellClick(Button cell, int x, int y) {
        if (isSelectMode) {
            Plant plant = garden.getPlantAt(x, y);
            if (plant != null) {
                // 如果之前有选中的格子，恢复其样式
                if (selectedCell != null) {
                    selectedCell.setStyle("-fx-background-color: white; -fx-border-color: black;");
                }
                // 高亮当前选中的格子
                cell.setStyle("-fx-background-color: lightgreen; -fx-border-color: green; -fx-border-width: 2px;");
                selectedCell = cell;
            } else {
                showAddPlantDialog(x, y);
            }
        } else {
            selectGridCell(x, y);
        }
    }

    @FXML
    private void toggleSelectMode() {
        isSelectMode = !isSelectMode;
        if (isSelectMode) {
            selectButton.setStyle("-fx-background-color: #90EE90; -fx-border-color: #228B22; -fx-border-width: 2px;");
            selectButton.setText("Cancel Select");
        } else {
            selectButton.setStyle("");
            selectButton.setText("Select Plant");
            if (selectedCell != null) {
                selectedCell.setStyle("-fx-background-color: white; -fx-border-color: black;");
                selectedCell = null;
            }
        }
    }

    @FXML
    private void handleWaterPlants() {
        if (isSelectMode && selectedCell != null) {
            // 获取选中格子的坐标
            Integer row = GridPane.getRowIndex(selectedCell);
            Integer col = GridPane.getColumnIndex(selectedCell);
            if (row != null && col != null) {
                Plant plant = garden.getPlantAt(row, col);
                if (plant != null) {
                    garden.waterPlant(row, col);
                    animateWaterDroplet(row, col);
                    logArea.appendText("Watering plant at (" + row + ", " + col + ").\n");
                }
            }
        } else {
            garden.applyWatering();
            logArea.appendText("Watering system activated.\n");
            animateSprinklers();
        }
    }

    private void animateWaterDroplet(int row, int col) {
        Circle droplet = new Circle(5, Color.BLUE);
        gardenGrid.add(droplet, col, row);

        TranslateTransition drop = new TranslateTransition(Duration.seconds(1), droplet);
        drop.setByY(30);
        drop.setCycleCount(3);
        drop.setAutoReverse(true);

        FadeTransition fade = new FadeTransition(Duration.seconds(1), droplet);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setCycleCount(3);
        fade.setAutoReverse(true);

        ParallelTransition waterAnimation = new ParallelTransition(drop, fade);
        waterAnimation.setOnFinished(e -> gardenGrid.getChildren().remove(droplet));
        waterAnimation.play();
    }

    private void animateSprinklers() {
        for (int i = 0; i < 4; i++) {
            Circle droplet = new Circle(5, Color.BLUE);
            gardenGrid.add(droplet, (i < 2) ? 1 : 4, (i % 2 == 0) ? 1 : 4);

            TranslateTransition drop = new TranslateTransition(Duration.seconds(1), droplet);
            drop.setByY(30);
            drop.setCycleCount(3);
            drop.setAutoReverse(true);

            FadeTransition fade = new FadeTransition(Duration.seconds(1), droplet);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fade.setCycleCount(3);
            fade.setAutoReverse(true);

            ParallelTransition waterAnimation = new ParallelTransition(drop, fade);
            waterAnimation.setOnFinished(e -> gardenGrid.getChildren().remove(droplet));
            waterAnimation.play();
        }
    }

    @FXML
    private void handleToggleLights() {
        garden.applyLighting();
        logArea.appendText("Lighting system activated.\n");
    }

    @FXML
    private void handleAdjustTemperature() {
        garden.applyHeating();
        logArea.appendText("Heating system activated.\n");
    }

    @FXML
    private void handlePestControl() {
        garden.applyPestControl();
        logArea.appendText("Pest control activated.\n");
    }

    @FXML
    private void handleHarvest() {
        garden.harvestPlants();
        updateGardenGrid();
        logArea.appendText("Harvesting system activated.\n");
    }

    /** ✅ **Fixed Method: logGardenState() to Match FXML** **/
    @FXML
    private void logGardenState() {
        try {
            // ✅ Load LogView FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/LogView.fxml"));
            Parent root = loader.load();

            // ✅ Set up the new window (Stage)
            Stage logStage = new Stage();
            logStage.setTitle("Garden Logs");
            logStage.setScene(new Scene(root, 550, 600)); // Window size

            // ✅ Show the log window
            logStage.show();
        } catch (Exception e) {
            System.err.println("Error loading Log View: " + e.getMessage());
        }
//        garden.logGardenState();
//        logArea.appendText("Garden state logged.\n");
    }

    public void handleGridClick(MouseEvent mouseEvent) {
        System.out.println("clicked!!");
    }

    @FXML
    private void openInventory() {
        try {
            java.lang.System.out.println("尝试打开库存界面...");
            
            // 确保库存中有数据
            List<Plant> inventory = garden.getInventory();
            java.lang.System.out.println("当前库存中有 " + inventory.size() + " 个植物");
            
            // 加载InventoryView FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/InventoryView.fxml"));
            Parent root = loader.load();
            
            // 获取控制器并手动刷新库存
            InventoryController controller = loader.getController();
            
            // 设置新窗口
            Stage inventoryStage = new Stage();
            inventoryStage.setTitle("库存");
            inventoryStage.setScene(new Scene(root, 300, 400)); // 窗口大小
            
            // 显示库存窗口
            inventoryStage.show();
            
            java.lang.System.out.println("库存界面已打开");
        } catch (Exception e) {
            java.lang.System.err.println("加载库存视图时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void selectGridCell(int x, int y) {
        System.out.println("Click grid: " + x + " , " + y);

        if (rootPane == null) {
            System.err.println("Error: rootPane is NULL. Check if it's defined in FXML.");
            return;
        }

        Plant plant = garden.getPlantAt(x, y);

        if (plant != null) {
            // ✅ If a plant already exists, show the plant details
            showPlantDetails(plant);
            return;
        }

        // ✅ Create a custom DialogPane (instead of Alert)
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(null);  // ✅ Remove the default title
        dialog.setHeaderText(null); // ✅ Remove header
        dialog.initStyle(StageStyle.UNDECORATED); // ✅ Removes default styling (Optional)

        // ✅ Style the Dialog (Apply CSS)
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles/dialogStyle.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");

        // ✅ Set Content
        dialogPane.setContent(new Label("Do you want to add a plant here?"));

        // ✅ Custom Buttons
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogPane.getButtonTypes().addAll(okButton, cancelButton);

        // ✅ Handle Button Clicks
        dialog.showAndWait().ifPresent(response -> {
            if (response == okButton) {
                showAddPlantDialog(x, y); // ✅ Open plant selection dialog
            }
        });
    }

    private void showPlantDetails(Plant plant) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/PlantView.fxml"));
            Parent root = loader.load();

            // ✅ Get the controller and pass plant data
            PlantController controller = loader.getController();
            System.out.println("controller: "+controller);
            controller.setPlant(plant);

            // ✅ Open the new window
            Stage stage = new Stage();
            stage.setTitle("Plant Details");
            stage.setScene(new Scene(root, 300, 500)); // Adjust size as needed
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading Plant Details View: " + e.getMessage());
        }
    }

    public void showAddPlantDialog(int x, int y) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/AddPlantView.fxml"));
            Parent root = loader.load();

            // ✅ Get controller and set context
            AddPlantController controller = loader.getController();
            controller.setContext(garden, logSystem, this, x, y); // ✅ Pass `this` (GardenController)

            Stage stage = new Stage();
            stage.setTitle("Add Plant");
            stage.setScene(new Scene(root, 400, 550));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
