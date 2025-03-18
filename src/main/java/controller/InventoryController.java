package controller;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Garden;
import model.Inventory;
import model.LogSystem;
import model.plants.Plant;
import java.util.*;

class InventoryItem {
    private Plant plant;
    private int quantity;

    public InventoryItem(Plant plant, int quantity) {
        this.plant = plant;
        this.quantity = quantity;
    }

    public Plant getPlant() {
        return plant;
    }

    public int getQuantity() {
        return quantity;
    }

    public void incrementQuantity() {
        this.quantity++;
    }
}

public class InventoryController {
    @FXML
    private ListView<InventoryItem> harvestedListView;
    @FXML private TabPane inventoryTabs;
    @FXML private ListView<InventoryItem> seedsListView;
    @FXML
    private Button closeButton;
    private Inventory inventory;
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
        loadInventory();
    }

    @FXML
    public void initialize() {
        seedsListView.setCellFactory(lv -> createInventoryCell(seedsListView));  // ✅ Seeds → Hide quantity
        harvestedListView.setCellFactory(lv -> createInventoryCell(harvestedListView));  // ✅ Harvested → Show quantity

        this.inventory = new Inventory();  // Mock instance for testing
        loadInventory();
    }

    private ListCell<InventoryItem> createInventoryCell(ListView<InventoryItem> targetListView) {
        return new ListCell<>() {
            private HBox hbox = new HBox(10);
            private ImageView imageView = new ImageView();
            private Label nameLabel = new Label();
            private Label quantityLabel = new Label();  // Only needed for harvested plants

            {
                hbox.getChildren().addAll(imageView, nameLabel);
                if (targetListView == harvestedListView) {  // ✅ Show quantity only for harvested plants
                    hbox.getChildren().add(quantityLabel);
                }
                imageView.setFitWidth(40);
                imageView.setFitHeight(40);
                nameLabel.setStyle("-fx-font-weight: bold;");
                quantityLabel.setStyle("-fx-text-fill: #666;");
            }

            @Override
            protected void updateItem(InventoryItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Plant plant = item.getPlant();
                    imageView.setImage(new Image(getClass().getResource(plant.getCurrentImagePath()).toExternalForm()));
                    nameLabel.setText(plant.getName());

                    if (targetListView == harvestedListView) {
                        quantityLabel.setText("x" + item.getQuantity());  // ✅ Show only for harvested plants
                    }

                    setGraphic(hbox);
                }
            }
        };
    }

    private void loadInventory() {
        seedsListView.getItems().clear();
        harvestedListView.getItems().clear();

        List<Plant> seeds = inventory.getSeeds();
        List<Plant> harvestedPlants = inventory.getHarvestedPlants();

        Map<String, InventoryItem> harvestedMap = new HashMap<>();

        // ✅ Store harvested plants with quantity
        for (Plant plant : harvestedPlants) {
            harvestedMap.computeIfAbsent(plant.getName(), k -> new InventoryItem(plant, 0)).incrementQuantity();
        }

        // ✅ Add seeds directly without quantity tracking
        for (Plant plant : seeds) {
            seedsListView.getItems().add(new InventoryItem(plant, 1));  // Quantity is ignored
        }

        harvestedListView.getItems().addAll(harvestedMap.values());

        System.out.println("✅ Inventory Loaded: " + seeds.size() + " seeds, " + harvestedMap.size() + " harvested plants.");
    }
    @FXML
    private void handleAddNewPlant() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/AddPlantView.fxml"));
            Parent root = loader.load();

            AddPlantController addPlantController = loader.getController();
            LogSystem logSystem=LogSystem.getInstance();
            addPlantController.setContext(logSystem,this);

            Stage addPlantStage = new Stage();
            addPlantStage.setTitle("Add New Plant");
            addPlantStage.setScene(new Scene(root, 400, 500)); // Adjust size if needed
            addPlantStage.showAndWait();  // Wait until plant is added

            // ✅ Refresh inventory after adding new plant
            loadInventory();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Failed to open Add Plant dialog.");
        }
    }

    public void showSuccessMessage(String plantName, boolean isUpdate) {
        String messageText = isUpdate
                ? "🔄 " + plantName + " has been updated in your inventory."
                : "✅ " + plantName + " has been added to your inventory.";

        Label floatingMessage = new Label(messageText);
        floatingMessage.getStyleClass().add("floating-message");

        // ✅ Ensure message expands fully
        floatingMessage.setWrapText(true);  // ✅ Allow text wrapping
        floatingMessage.setMaxWidth(280);
        floatingMessage.setMinWidth(280);
        floatingMessage.setPrefHeight(50);

        // ✅ Get the root VBox (correct type)
        VBox rootVBox = (VBox) inventoryTabs.getScene().getRoot();
        rootVBox.getChildren().add(0, floatingMessage);  // ✅ Insert at the top

        // ✅ Fade-out animation after 3 seconds
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), floatingMessage);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> rootVBox.getChildren().remove(floatingMessage));

        // ✅ Wait 3 seconds, then fade out
        Platform.runLater(() -> {
            new Thread(() -> {
                try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
                Platform.runLater(fadeOut::play);
            }).start();
        });
    }


    @FXML
    private void handleClose() {
        closeButton.getScene().getWindow().hide();
    }
} 