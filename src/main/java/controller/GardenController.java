package controller;

import javafx.application.Platform;
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
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import model.Garden;
import model.Inventory;
import model.LogSystem;
import model.EnvironmentSystem;
import model.plants.Plant;
import javafx.animation.*;
import javafx.scene.paint.Color;
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
    private boolean isSelectMode = false;
    private Button selectedCell = null;
    private boolean isWatering = true;
    @FXML
    private ImageView backgroundImage;  // Reference to background
    @FXML
    private GridPane gardenGrid;
    @FXML
    private Slider speedSlider;

    @FXML
    private TextArea logArea;

    @FXML
    private ImageView sprinkler1, sprinkler2, sprinkler3, sprinkler4;

    @FXML
    private StackPane rootPane; // Add an FXML reference to your root Pane
    @FXML
    private Label dayHourLabel; // Reference to FXML label for displaying time

    @FXML
    private Label temperatureLabel;

    @FXML
    private Button selectButton;

    public void initialize() {
        logSystem=LogSystem.getInstance();
        environmentSystem =EnvironmentSystem.getInstance();
        garden = Garden.getInstance();
        garden.initializeGarden(); // Ensures plants are placed at the start
        setupGardenGrid();
        setupSprinklers();
        updateGardenGrid(); // Ensures plants are displayed at startup
        setupLogArea();

        // Link simulation speed slider
        speedSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            double speedFactor = newValue.doubleValue();
            TimeManager.setSimulationSpeed(speedFactor);
            logArea.appendText("Simulation speed set to: " + speedFactor + "x\n");
        });

        // Start simulation
//        TimeManager.startSimulation(garden);
        startSimulation();


        // display temperature
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
        rain();
        garden.applyWatering();
        garden.applyPestControl();
        garden.applyHeating();
        garden.applyLighting();
        garden.growPlants(this);
        garden.harvestPlants();
        logArea.appendText("Simulation step completed.\n");
    }
    public void rain(){
        Random random = new Random();
        if(random.nextInt(100) < 20){
            int randomAmount = random.nextInt(5000) + 50;
            garden.evaporateWater(true, randomAmount);
            if (randomAmount < 1000) {
                rain(2);
            } else if (randomAmount <= 2000) {
                rain(4);
            } else if (randomAmount <= 3000) {
                rain(6);
            } else if (randomAmount <= 4000) {
                rain(8);
            } else {
                rain(10);
            }
            logSystem.logEvent("Rainfall occurred. Added " + randomAmount + " ml to the water supply.");

        }


    }

    @FXML
    private void stopSimulation() {
        simulationTimeline.stop();
        logArea.appendText("Simulation stopped.\n");
    }

    public void updateSimulationTime() {
        int day = TimeManager.getSimulatedHour()/24;
        int hour = TimeManager.getSimulatedHour()%24;
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
                if (selectedCell != null) {
                    selectedCell.setStyle("-fx-background-color: white; -fx-border-color: black;");
                }
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
            Integer row = GridPane.getRowIndex(selectedCell);
            Integer col = GridPane.getColumnIndex(selectedCell);
            if (row != null && col != null) {
                Plant plant = garden.getPlantAt(row, col);
                if (plant != null) {
                    plant.addSunlight(4);
                    logArea.appendText("Added 4 hours of sunlight to " + plant.getName() + " at (" + row + ", " + col + ").\n");
                    addLightEffect(selectedCell);
                }
            }
        } else {
            garden.toggleLights();
            logArea.appendText("Lighting system toggled.\n");
            addGlobalLightEffect();
        }
    }

    private void addLightEffect(Button cell) {
        // yellow light
        cell.setStyle("-fx-background-color: #FFFF99; -fx-border-color: #FFD700; -fx-border-width: 2px;");

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
        for (Node node : gardenGrid.getChildren()) {
            if (node instanceof Button) {
                Button cell = (Button) node;
                Integer row = GridPane.getRowIndex(cell);
                Integer col = GridPane.getColumnIndex(cell);
                
                if (row != null && col != null) {
                    Plant plant = garden.getPlantAt(row, col);
                    if (plant != null) {
                        cell.setStyle("-fx-background-color: #FFFF99; -fx-border-color: #FFD700; -fx-border-width: 2px;");
                    }
                }
            }
        }

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
            Integer row = GridPane.getRowIndex(selectedCell);
            Integer col = GridPane.getColumnIndex(selectedCell);
            if (row != null && col != null) {
                Plant plant = garden.getPlantAt(row, col);
                if (plant != null) {
                    selectedCell.setStyle("-fx-background-color: #ffebee; -fx-border-color: #ff5252;");

                    FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), selectedCell);
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.3);
                    fadeOut.setCycleCount(2);
                    fadeOut.setAutoReverse(true);

                    fadeOut.setOnFinished(event -> {
                        selectedCell.setStyle("-fx-background-color: white; -fx-border-color: black;");
                    });

                    fadeOut.play();

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


    /** **Fixed Method: logGardenState() to Match FXML** **/
    @FXML
    private void logGardenState() {
        try {
            // Load LogView FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/LogView.fxml"));
            Parent root = loader.load();

            // Set up the new window (Stage)
            Stage logStage = new Stage();
            logStage.setTitle("Garden Logs");
            logStage.setScene(new Scene(root, 550, 600)); // Window size

            // Show the log window
            logStage.show();
        } catch (Exception e) {
            System.err.println("Error loading Log View: " + e.getMessage());
        }
    }

    @FXML
    private void openInventory() {
        try {
            java.lang.System.out.println("Open inventory...");
            logSystem.logEvent("Open inventory...");

            // Use Inventory directly
            Inventory inventory = Inventory.getInstance();
            List<Plant> harvestedPlants = inventory.getHarvestedPlants();
            List<Plant> seeds = inventory.getSeeds();

            java.lang.System.out.println("Check inventory: " + harvestedPlants.size() + "harvested plants, and " + seeds.size() + " seeds");
            logSystem.logEvent("Check inventory: " + harvestedPlants.size() + "harvested plants, and " + seeds.size() + " seeds");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/InventoryView.fxml"));
            Parent root = loader.load();

            // Pass the correct inventory reference
            InventoryController controller = loader.getController();
            controller.setInventory(inventory);

            Stage inventoryStage = new Stage();
            inventoryStage.setTitle("Inventory");
            inventoryStage.setScene(new Scene(root, 300, 400));
            inventoryStage.show();

        } catch (Exception e) {
            java.lang.System.err.println("Error loading inventory: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void selectGridCell(int x, int y) {
        if (rootPane == null) {
            System.err.println("Error: rootPane is NULL. Check if it's defined in FXML.");
            return;
        }

        Plant plant = garden.getPlantAt(x, y);

        if (plant != null) {
            // If a plant already exists, show the plant details
            showPlantDetails(plant);
            return;
        }

        // Get available seeds from the inventory
        List<Plant> seeds = Inventory.getInstance().getSeeds();

        // Create a seed selection dialog
        Dialog<Plant> dialog = new Dialog<>();
        dialog.setTitle("Select a Seed to Plant");
        dialog.setHeaderText(null);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles/dialogStyle.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");

        // Seed selection UI
        VBox contentBox = new VBox(10);
        contentBox.setAlignment(Pos.CENTER);

        // Create seed selection list (Declared here for scope visibility)
        ListView<Plant> seedListView = new ListView<>();

        if (seeds.isEmpty()) {
            // Show "No seeds available" message if empty
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

        // Custom Buttons
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
            System.out.println("Planted " + selectedSeed.getName() + " at (" + x + ", " + y + ")");
            logSystem.logEvent("Planted " + selectedSeed.getName() + " at (" + x + ", " + y + ").");

            Platform.runLater(this::updateGardenGrid);
        }, () -> System.out.println("No seed was selected (Dialog closed without selection)."));
    }

    private void showPlantDetails(Plant plant) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/PlantView.fxml"));
            Parent root = loader.load();

            // Get the controller and pass plant data
            PlantController controller = loader.getController();
            System.out.println("controller: "+controller);
            controller.setPlant(garden, plant);

            // Open the new window
            Stage stage = new Stage();
            stage.setTitle("Plant Details");
            stage.setScene(new Scene(root, 500, 500)); // Adjust size as needed
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading Plant Details View: " + e.getMessage());
        }
    }


    private void updateTemperatureDisplay() {
        int currentTemp = garden.getCurrentTemperature();
        Platform.runLater(() -> {
            temperatureLabel.setText(String.format("Temperature: %d°C", currentTemp));

            temperatureLabel.getStyleClass().removeAll("temperature-cold", "temperature-normal", "temperature-hot");

            if (!temperatureLabel.getStyleClass().contains("temperature-label")) {
                temperatureLabel.getStyleClass().add("temperature-label");
            }

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
        logSystem.logEvent("Start raining....");

        Platform.runLater(() -> {
            rainPane.setPrefSize(rootPane.getWidth(), rootPane.getHeight());
            rainPane.toFront(); // Ensure rain is always visible
            rainPane.setMouseTransparent(true); //  Allows button clicks

            // Set fog effect and fade in
            FadeTransition fadeInFog = new FadeTransition(Duration.seconds(2), rainPane);
            rainPane.setStyle("-fx-background-color: rgba(100, 100, 100, 1); visibility: visible;"); // Start fully transparent
            fadeInFog.setFromValue(0.0);
            fadeInFog.setToValue(0.6); // Make the screen look foggy
            fadeInFog.play();
        });

        // Run animation asynchronously (prevent UI freezing)
        new Thread(() -> {
            environmentSystem.addRainfall(100); // Run environment logic in a separate thread
            Platform.runLater(this::startRainEffect); // Update UI safely
        }).start();
        new Timeline(new KeyFrame(Duration.seconds(hour), e -> stopRainEffect())).play();

    }

    private void startRainEffect() {
        int dropCount = 5; // Only add a few new raindrops per cycle

        for (int i = 0; i < dropCount; i++) {
            // Choose a random color from Blue, Dark Blue, Light Blue
            Color[] rainColors = {Color.BLUE, Color.DARKBLUE, Color.LIGHTBLUE};
            Color randomColor = rainColors[(int) (Math.random() * rainColors.length)];

            // Create a thin, long raindrop with random color
            Rectangle raindrop = new Rectangle(2, 15, randomColor); // Width = 2, Height = 15
            raindrop.setOpacity(0.7);
            raindrop.setLayoutX(Math.random() * 800); // Spread across full width (800px)
            raindrop.setLayoutY(-20); // Start just above the screen (-20)

            Platform.runLater(() -> rainPane.getChildren().add(raindrop));

            // Falling animation with random duration
            TranslateTransition fall = new TranslateTransition(Duration.seconds(2 + Math.random()), raindrop);
            fall.setToY(850); // Ensure it falls past 800 to fully disappear
            fall.setInterpolator(Interpolator.LINEAR);

            // Slight random rotation for realism
            RotateTransition tilt = new RotateTransition(Duration.seconds(2 + Math.random()), raindrop);
            tilt.setByAngle(Math.random() * 10 - 5); // Small angle between -5° and 5°

            // Combine animations
            ParallelTransition rainAnimation = new ParallelTransition(fall, tilt);
            rainAnimation.setOnFinished(e -> Platform.runLater(() -> rainPane.getChildren().remove(raindrop))); // Remove only after full fall
            rainAnimation.play();
        }

        // Keep adding new raindrops individually (every 100ms)
        if (rainTimeline == null || !rainTimeline.getStatus().equals(Animation.Status.RUNNING)) {
            rainTimeline = new Timeline(new KeyFrame(Duration.millis(100), e -> startRainEffect())); // Drops are added smoothly
            rainTimeline.setCycleCount(Timeline.INDEFINITE);
            rainTimeline.play();
        }
    }

    private void stopRainEffect() {
        if (rainTimeline != null) {
            rainTimeline.stop();
            rainTimeline = null; // Reset the timeline
        }
        rainPane.getChildren().clear(); // Remove all raindrops

        // Fade out fog effect
        FadeTransition fadeOutFog = new FadeTransition(Duration.seconds(2), rainPane);
        fadeOutFog.setFromValue(0.6);
        fadeOutFog.setToValue(0.0);
        fadeOutFog.setOnFinished(e -> rainPane.setStyle("visibility: hidden;")); // Hide rainPane after fade out
        fadeOutFog.play();

        System.out.println("Rain and fog have stopped.");
        logSystem.logEvent("Rain and fog have stopped.");
    }

    private void toggleSprinkler(){
        isWatering=!isWatering;
        setupSprinklers();
    }

    private void setupSprinklers() {
        try {
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

                ImageView sprinkler = new ImageView(sprinklerImage);
                sprinkler.setFitWidth(SPRINKLER_SIZE);
                sprinkler.setFitHeight(SPRINKLER_SIZE);
                sprinkler.setMouseTransparent(true);

                Node existingPane = getNodeFromGridPane(gardenGrid, col, row);
                if (existingPane instanceof Pane) {
                    ((Pane) existingPane).getChildren().setAll(sprinkler);
                } else {

                    Pane sprinklerPane = new Pane(sprinkler);
                    sprinklerPane.setMaxSize(0, 0);
                    sprinklerPane.setMouseTransparent(true);

                    sprinkler.setTranslateX((double) -CELL_SIZE / 2);
                    sprinkler.setTranslateY((double) -CELL_SIZE);

                    gardenGrid.add(sprinklerPane, col, row);
                }
            }

            Platform.runLater(() -> gardenGrid.requestLayout());

        } catch (NullPointerException e) {
            System.err.println("Error: Sprinkler image not found! Ensure it's inside 'src/main/resources/images/'.");
        }
    }
    private void disableSoilClick(int col, int row) {
        Node soil = getNodeFromGridPane(gardenGrid, col, row);
        if (soil != null) {
            soil.setDisable(true);
        }
    }

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
