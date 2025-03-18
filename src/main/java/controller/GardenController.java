package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Garden;
import model.Inventory;
import model.LogSystem;
import model.EnvironmentSystem;
import model.plants.Plant;
import javafx.animation.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import util.TimeManager;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class GardenController {
    public BorderPane mainLayout;
    final int CELL_SIZE = 50;
    public Pane rainPane;
    public Button inventoryButton;
    private Garden garden;
    private Timeline simulationTimeline,rainTimeline;
    private LogSystem logSystem;
    private EnvironmentSystem environmentSystem;
    private boolean isSelectMode = false; // 新增：选择模式标志
    private Button selectedCell = null; // 新增：当前选中的格子
    private boolean isWatering = true;
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
    private Label temperatureLabel; // 温度显示标签

    @FXML
    private Button selectButton;  // 添加按钮引用

    public void initialize() {
        environmentSystem =EnvironmentSystem.getInstance();
        garden = Garden.getInstance();
        garden.initializeGarden(); // Ensures plants are placed at the start
        setupGardenGrid();
        setupSprinklers();
        updateGardenGrid(); // Ensures plants are displayed at startup
        setupLogArea();
        rain(24);


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


        // 开始定期更新温度显示
        Timeline temperatureUpdateTimeline = new Timeline(new KeyFrame(
            Duration.seconds(1),
            event -> updateTemperatureDisplay()
        ));
        temperatureUpdateTimeline.setCycleCount(Timeline.INDEFINITE);
        temperatureUpdateTimeline.play();
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
        garden.growPlants(this);
        garden.harvestPlants();
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
        garden.growPlants(this);
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

    public void updateGardenGrid() {
        updateGardenGrid(false);  // Calls the method below with `false` as default
    }

    public void updateGardenGrid(boolean isFullyGrown) {
        Platform.runLater(() -> {
            // Remove only plant images while keeping sprinklers
            System.out.println("Updating Garden Grid. Fully Grown: " + isFullyGrown);
            gardenGrid.getChildren().removeIf(node ->
                    node instanceof ImageView &&
                            node != sprinkler1 && node != sprinkler2 &&
                            node != sprinkler3 && node != sprinkler4
            );

            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 6; col++) {
                    Plant plant = garden.getPlantAt(row, col);

                    if (plant != null) {
                        String plantImagePath;
                        if(isFullyGrown){
                            plantImagePath = plant.getMatureImagePath();

                        }else{
                            plantImagePath=plant.getCurrentImagePath();
                        }

                        Image plantImage;
                        try {
                            plantImage = new Image(getClass().getResource(plantImagePath).toExternalForm(),false);
                        } catch (NullPointerException e) {
                            plantImage = new Image(getClass().getResource("/images/plants/default.png").toExternalForm());
                        }

                        ImageView plantView = new ImageView(plantImage);
                        plantView.setFitWidth(20);
                        plantView.setFitHeight(20);
                        plantView.setMouseTransparent(true);
                        gardenGrid.add(plantView, col, row);
                    }
                }
            }
            gardenGrid.requestLayout();
            gardenGrid.applyCss();
        });
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
                selectedCell.setStyle("-fx-background-color: white; -fx-border-color: transparent;");
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
            toggleSprinkler();
            logArea.appendText("Watering system activated.\n");
//            animateSprinklers();
        }
    }

    private void animateWaterDroplet(int row, int col) {
        try {
            clearOldEffects("addWater.gif");

            String addWaterEffect = getClass().getResource("/images/addWater.gif").toExternalForm();
            Image waterGif = new Image(addWaterEffect);

            ImageView waterEffect = new ImageView(waterGif);
            waterEffect.setFitWidth(100);
            waterEffect.setFitHeight(120);
            waterEffect.setMouseTransparent(true);

            Pane waterEffectPane = new Pane(waterEffect);
            waterEffectPane.setMaxSize(0, 0);
            waterEffectPane.setMouseTransparent(true);

            waterEffect.setTranslateX((double) -CELL_SIZE / 2);
            waterEffect.setTranslateY((double) -CELL_SIZE*2 );

            gardenGrid.add(waterEffectPane, col, row);

            GridPane.setMargin(waterEffectPane, new Insets(-2, -2, -2, -2));

            // ✅ 1.5 秒后自动删除 GIF
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
                gardenGrid.getChildren().remove(waterEffectPane);
            }));
            timeline.setCycleCount(1);
            timeline.play();

            Platform.runLater(() -> gardenGrid.requestLayout());

        } catch (Exception e) {
            System.err.println("Error: Water effect GIF not found! Ensure it's inside 'src/main/resources/images/'.");
        }
    }
    @FXML
    private void handleToggleLights() {
        if (isSelectMode && selectedCell != null) {
            // 获取选中格子的坐标
            Integer row = GridPane.getRowIndex(selectedCell);
            Integer col = GridPane.getColumnIndex(selectedCell);
            if (row != null && col != null) {
                Plant plant = garden.getPlantAt(row, col);
                if (plant != null) {
                    plant.addSunlight(4); // 增加4小时的阳光时间
                    logArea.appendText("Added 4 hours of sunlight to " + plant.getName() + " at (" + row + ", " + col + ").\n");
                    // 添加视觉效果
                    addLightEffect(selectedCell);
                }
            }
        } else {
            garden.toggleLights();
            logArea.appendText("Lighting system toggled.\n");
            // 为所有有植物的格子添加照明效果
            addGlobalLightEffect();
        }
    }

    private void addLightEffect(Button cell) {
        // 创建一个黄色的发光效果
        cell.setStyle("-fx-background-color: #FFFF99; -fx-border-color: #FFD700; -fx-border-width: 2px;");
        
        // 2秒后恢复原样
        Timeline timeline = new Timeline(new KeyFrame(
            Duration.seconds(2),
            event -> {
                if (isSelectMode) {
                    cell.setStyle("-fx-background-color: lightgreen; -fx-border-color: green; -fx-border-width: 2px;");
                } else {
                    cell.setStyle("-fx-background-color: white; -fx-border-color: black;");
                }
            }
        ));
        timeline.play();
    }

    private void addGlobalLightEffect() {
        // 遍历所有格子
        for (Node node : gardenGrid.getChildren()) {
            if (node instanceof Button) {
                Button cell = (Button) node;
                Integer row = GridPane.getRowIndex(cell);
                Integer col = GridPane.getColumnIndex(cell);
                
                if (row != null && col != null) {
                    Plant plant = garden.getPlantAt(row, col);
                    if (plant != null) {
                        // 创建一个黄色的发光效果
                        cell.setStyle("-fx-background-color: #FFFF99; -fx-border-color: #FFD700; -fx-border-width: 2px;");
                    }
                }
            }
        }
        
        // 2秒后恢复所有格子的原样
        Timeline timeline = new Timeline(new KeyFrame(
            Duration.seconds(2),
            event -> {
                for (Node node : gardenGrid.getChildren()) {
                    if (node instanceof Button) {
                        Button cell = (Button) node;
                        Integer row = GridPane.getRowIndex(cell);
                        Integer col = GridPane.getColumnIndex(cell);
                        
                        if (row != null && col != null) {
                            Plant plant = garden.getPlantAt(row, col);
                            if (plant != null) {
                                cell.setStyle("-fx-background-color: white; -fx-border-color: black;");
                            }
                        }
                    }
                }
            }
        ));
        timeline.play();
    }

    @FXML
    private void handleAdjustTemperature() {
        if (selectedCell != null) {
            // 获取选中格子的位置
            Integer row = GridPane.getRowIndex(selectedCell);
            Integer col = GridPane.getColumnIndex(selectedCell);
            if (row != null && col != null) {
                Plant plant = garden.getPlantAt(row, col);
                if (plant != null) {
                    // 创建温暖效果动画
                    selectedCell.setStyle("-fx-background-color: #ffebee; -fx-border-color: #ff5252;");
                    
                    // 创建渐变动画
                    FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), selectedCell);
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.3);
                    fadeOut.setCycleCount(2);
                    fadeOut.setAutoReverse(true);
                    
                    // 动画结束后恢复原样
                    fadeOut.setOnFinished(event -> {
                        selectedCell.setStyle("-fx-background-color: white; -fx-border-color: black;");
                    });
                    
                    // 播放动画
                    fadeOut.play();
                    
                    // 应用加热效果
                    garden.applyHeating();
                    logArea.appendText("Heating applied to " + plant.getName() + " at (" + row + "," + col + ").\n");
                    updateTemperatureDisplay();
                }
            }
        } else {
            garden.applyHeating();
            logArea.appendText("Heating system activated.\n");
            updateTemperatureDisplay();
        }
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

            // ✅ Use Inventory directly
            Inventory inventory = Inventory.getInstance();
            List<Plant> harvestedPlants = inventory.getHarvestedPlants();
            List<Plant> seeds = inventory.getSeeds();

            java.lang.System.out.println("库存信息: " + harvestedPlants.size() + " 个收获的植物, " + seeds.size() + " 颗种子");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/InventoryView.fxml"));
            Parent root = loader.load();

            // ✅ Pass the correct inventory reference
            InventoryController controller = loader.getController();
            controller.setInventory(inventory);

            Stage inventoryStage = new Stage();
            inventoryStage.setTitle("库存");
            inventoryStage.setScene(new Scene(root, 300, 400));
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

        // ✅ Get available seeds from the inventory
        List<Plant> seeds = Inventory.getInstance().getSeeds();

        // ✅ Create a seed selection dialog
        Dialog<Plant> dialog = new Dialog<>();
        dialog.setTitle("Select a Seed to Plant");
        dialog.setHeaderText(null);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles/dialogStyle.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");

        // ✅ Seed selection UI
        VBox contentBox = new VBox(10);
        contentBox.setAlignment(Pos.CENTER);

        // ✅ Create seed selection list (Declared here for scope visibility)
        ListView<Plant> seedListView = new ListView<>();

        if (seeds.isEmpty()) {
            // ✅ Show "No seeds available" message if empty
            Label emptyMessage = new Label("No seeds available in inventory.");
            emptyMessage.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
            contentBox.getChildren().add(emptyMessage);
        } else {
            seedListView.getItems().addAll(seeds);
            seedListView.setCellFactory(param -> new ListCell<>() {
                private final ImageView imageView = new ImageView();

                @Override
                protected void updateItem(Plant plant, boolean empty) {
                    super.updateItem(plant, empty);
                    if (empty || plant == null) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        imageView.setImage(new Image(getClass().getResource(plant.getCurrentImagePath()).toExternalForm()));
                        imageView.setFitWidth(40);
                        imageView.setFitHeight(40);
                        setGraphic(imageView);
                        setText(plant.getName());
                    }
                }
            });

            contentBox.getChildren().add(seedListView);
        }

        dialogPane.setContent(contentBox);

        // ✅ Custom Buttons
        ButtonType plantButton = new ButtonType("Plant", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        if(seeds.isEmpty()){
            dialogPane.getButtonTypes().setAll(cancelButton);
        }else{
            dialogPane.getButtonTypes().setAll(plantButton, cancelButton);
        }
        dialog.setResultConverter(button -> {
            if (button == plantButton) {
                return seedListView.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        Optional<Plant> result = dialog.showAndWait();

        result.ifPresentOrElse(selectedSeed -> {
            garden.addPlant(x, y, selectedSeed);
            System.out.println("🌱 Planted " + selectedSeed.getName() + " at (" + x + ", " + y + ")");
            logSystem.logEvent("Planted " + selectedSeed.getName() + " at (" + x + ", " + y + ").");

            Platform.runLater(this::updateGardenGrid);
        }, () -> System.out.println("❌ No seed was selected (Dialog closed without selection)."));
    }

    private void showPlantDetails(Plant plant) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/PlantView.fxml"));
            Parent root = loader.load();

            // ✅ Get the controller and pass plant data
            PlantController controller = loader.getController();
            System.out.println("controller: "+controller);
            controller.setPlant(garden, plant);

            // ✅ Open the new window
            Stage stage = new Stage();
            stage.setTitle("Plant Details");
            stage.setScene(new Scene(root, 300, 500)); // Adjust size as needed
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading Plant Details View: " + e.getMessage());
        }
    }


    private void updateTemperatureDisplay() {
        int currentTemp = garden.getCurrentTemperature();
        Platform.runLater(() -> {
            temperatureLabel.setText(String.format("Temperature: %d°C", currentTemp));
            
            // 移除所有温度相关的样式类
            temperatureLabel.getStyleClass().removeAll("temperature-cold", "temperature-normal", "temperature-hot");
            
            // 添加基础样式类
            if (!temperatureLabel.getStyleClass().contains("temperature-label")) {
                temperatureLabel.getStyleClass().add("temperature-label");
            }
            
            // 根据温度添加对应的样式类
            if (currentTemp < 15) {
                temperatureLabel.getStyleClass().add("temperature-cold");
            } else if (currentTemp > 30) {
                temperatureLabel.getStyleClass().add("temperature-hot");
            } else {
                temperatureLabel.getStyleClass().add("temperature-normal");
            }
        });
    }

    private void rain(int hour){

        Platform.runLater(() -> {
            rainPane.setPrefSize(rootPane.getWidth(), rootPane.getHeight());
            rainPane.toFront(); // ✅ Ensure rain is always visible
            rainPane.setMouseTransparent(true); // ✅ Allows button clicks

            // ✅ Set fog effect and fade in
            FadeTransition fadeInFog = new FadeTransition(Duration.seconds(2), rainPane);
            rainPane.setStyle("-fx-background-color: rgba(100, 100, 100, 1); visibility: visible;"); // Start fully transparent
            fadeInFog.setFromValue(0.0);
            fadeInFog.setToValue(0.6); // ✅ Make the screen look foggy
            fadeInFog.play();
        });

        // ✅ Run animation asynchronously (prevent UI freezing)
        new Thread(() -> {
            environmentSystem.addRainfall(100); // ✅ Run environment logic in a separate thread
            Platform.runLater(this::startRainEffect); // ✅ Update UI safely
        }).start();
        new Timeline(new KeyFrame(Duration.seconds(hour), e -> stopRainEffect())).play();

    }

    private void startRainEffect() {
        int dropCount = 5; // ✅ Only add a few new raindrops per cycle

        for (int i = 0; i < dropCount; i++) {
            // ✅ Choose a random color from Blue, Dark Blue, Light Blue
            Color[] rainColors = {Color.BLUE, Color.DARKBLUE, Color.LIGHTBLUE};
            Color randomColor = rainColors[(int) (Math.random() * rainColors.length)];

            // ✅ Create a thin, long raindrop with random color
            Rectangle raindrop = new Rectangle(2, 15, randomColor); // Width = 2, Height = 15
            raindrop.setOpacity(0.7);
            raindrop.setLayoutX(Math.random() * 800); // ✅ Spread across full width (800px)
            raindrop.setLayoutY(-20); // ✅ Start just above the screen (-20)

            Platform.runLater(() -> rainPane.getChildren().add(raindrop));

            // ✅ Falling animation with random duration
            TranslateTransition fall = new TranslateTransition(Duration.seconds(2 + Math.random()), raindrop);
            fall.setToY(850); // ✅ Ensure it falls past 800 to fully disappear
            fall.setInterpolator(Interpolator.LINEAR);

            // ✅ Slight random rotation for realism
            RotateTransition tilt = new RotateTransition(Duration.seconds(2 + Math.random()), raindrop);
            tilt.setByAngle(Math.random() * 10 - 5); // ✅ Small angle between -5° and 5°

            // ✅ Combine animations
            ParallelTransition rainAnimation = new ParallelTransition(fall, tilt);
            rainAnimation.setOnFinished(e -> Platform.runLater(() -> rainPane.getChildren().remove(raindrop))); // ✅ Remove only after full fall
            rainAnimation.play();
        }

        // ✅ Keep adding new raindrops individually (every 100ms)
        if (rainTimeline == null || !rainTimeline.getStatus().equals(Animation.Status.RUNNING)) {
            rainTimeline = new Timeline(new KeyFrame(Duration.millis(100), e -> startRainEffect())); // ✅ Drops are added smoothly
            rainTimeline.setCycleCount(Timeline.INDEFINITE);
            rainTimeline.play();
        }
    }
    private void stopRainEffect() {
        if (rainTimeline != null) {
            rainTimeline.stop();
            rainTimeline = null; // ✅ Reset the timeline
        }
        rainPane.getChildren().clear(); // ✅ Remove all raindrops

        // ✅ Fade out fog effect
        FadeTransition fadeOutFog = new FadeTransition(Duration.seconds(2), rainPane);
        fadeOutFog.setFromValue(0.6);
        fadeOutFog.setToValue(0.0);
        fadeOutFog.setOnFinished(e -> rainPane.setStyle("visibility: hidden;")); // ✅ Hide rainPane after fade out
        fadeOutFog.play();

        System.out.println("🌤️ Rain and fog have stopped.");
    }

    private void toggleSprinkler(){
        isWatering=!isWatering;
        setupSprinklers();
    }

    private void setupSprinklers() {
        try {
            // ✅ 先清除旧的 Sprinkler，防止重复添加
            clearOldEffects("sprinklers.gif");
            clearOldEffects("sprinkler-turnoff.png");

            String sprinklerImagePath;
            if (isWatering) {
                sprinklerImagePath = getClass().getResource("/images/sprinklers.gif").toExternalForm();
            } else {
                sprinklerImagePath = getClass().getResource("/images/sprinkler-turnoff.png").toExternalForm();
            }

            Image sprinklerImage = new Image(sprinklerImagePath);

            int[][] sprinklerPositions = {
                    {1, 1}, {1, 4}, {4, 1}, {4, 4}
            };

            int SPRINKLER_SIZE = 100;

            for (int[] pos : sprinklerPositions) {
                int col = pos[0];
                int row = pos[1];
                disableSoilClick(col, row);

                // ✅ 创建 Sprinkler ImageView
                ImageView sprinkler = new ImageView(sprinklerImage);
                sprinkler.setFitWidth(SPRINKLER_SIZE);
                sprinkler.setFitHeight(SPRINKLER_SIZE);
                sprinkler.setMouseTransparent(true); // 🔥 避免影响点击事件

                // ✅ 获取已存在的 Pane，更新 Image 而不是新建 Pane
                Node existingPane = getNodeFromGridPane(gardenGrid, col, row);
                if (existingPane instanceof Pane) {
                    ((Pane) existingPane).getChildren().setAll(sprinkler); // ✅ 直接替换 Sprinkler 图片
                } else {
                    // ✅ 如果没有旧的 Sprinkler，则创建新的 Pane 并添加到 GridPane
                    Pane sprinklerPane = new Pane(sprinkler);
                    sprinklerPane.setMaxSize(0, 0);
                    sprinklerPane.setMouseTransparent(true);

                    // ✅ 计算 Sprinkler 的偏移量，让它覆盖 2x2 格子
                    sprinkler.setTranslateX((double) -CELL_SIZE / 2);
                    sprinkler.setTranslateY((double) -CELL_SIZE);

                    gardenGrid.add(sprinklerPane, col, row);
                }
            }

            // ✅ 强制刷新 UI
            Platform.runLater(() -> gardenGrid.requestLayout());

        } catch (NullPointerException e) {
            System.err.println("Error: Sprinkler image not found! Ensure it's inside 'src/main/resources/images/'.");
        }
    }
    // ✅ 禁用 Sprinkler 下面的 Soil 单元格
    private void disableSoilClick(int col, int row) {
        Node soil = getNodeFromGridPane(gardenGrid, col, row);
        if (soil != null) {
            soil.setDisable(true); // 禁用 Soil 点击
        }
    }

    // ✅ 获取指定 GridPane 单元格的 Node
    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            Integer nodeCol = GridPane.getColumnIndex(node);
            Integer nodeRow = GridPane.getRowIndex(node);
            if (nodeCol != null && nodeRow != null && nodeCol == col && nodeRow == row) {
                return node;
            }
        }
        return null;
    }

    // ✅ 清除旧的 Sprinkler，防止重复添加
    private void clearOldEffects(String imageFileName) {
        List<Node> toRemove = new ArrayList<>();
        for (Node node : gardenGrid.getChildren()) {
            if (node instanceof Pane && !((Pane) node).getChildren().isEmpty()) {
                Node child = ((Pane) node).getChildren().get(0);
                if (child instanceof ImageView) {
                    ImageView imgView = (ImageView) child;
                    if (imgView.getImage().getUrl().contains(imageFileName)) {
                        toRemove.add(node);
                    }
                }
            }
        }
        gardenGrid.getChildren().removeAll(toRemove);
    }

}
