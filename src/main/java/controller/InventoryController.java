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

// 创建一个新的类来表示库存中的植物项
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

//    public void initialize() {
//        // 设置ListView的自定义单元格
//        harvestedListView.setCellFactory(lv -> new ListCell<InventoryItem>() {
//            private HBox hbox = new HBox(10);
//            private ImageView imageView = new ImageView();
//            private Label nameLabel = new Label();
//            private Label quantityLabel = new Label();
//
//            {
//                hbox.getChildren().addAll(imageView, nameLabel, quantityLabel);
//                imageView.setFitWidth(40);
//                imageView.setFitHeight(40);
//                nameLabel.setStyle("-fx-font-weight: bold;");
//                quantityLabel.setStyle("-fx-text-fill: #666;");
//            }
//
//            @Override
//            protected void updateItem(InventoryItem item, boolean empty) {
//                super.updateItem(item, empty);
//                if (empty || item == null) {
//                    setGraphic(null);
//                    setText(null);
//                } else {
//                    try {
//                        Plant plant = item.getPlant();
//                        // 设置植物图片
//                        String imagePath = plant.getCurrentImagePath();
//                        java.lang.System.out.println("尝试加载图片: " + imagePath + " 对于植物: " + plant.getName() +
//                                                  ", 成熟: " + plant.isFullyGrown() +
//                                                  ", 已收获: " + plant.getIsHarvested());
//
//                        Image image;
//                        try {
//                            // 确保使用正确的图片路径
//                            if (plant.getIsHarvested() || plant.isFullyGrown()) {
//                                // 如果植物已收获或已成熟，使用成熟图片
//                                imagePath = plant.getCurrentImagePath();
//                                java.lang.System.out.println("使用成熟图片: " + imagePath);
//                            }
//
//                            image = new Image(getClass().getResource(imagePath).toExternalForm());
//                            java.lang.System.out.println("成功加载图片: " + imagePath);
//                        } catch (Exception e) {
//                            java.lang.System.out.println("无法加载图片: " + imagePath + ", 错误: " + e.getMessage());
//                            // 尝试使用默认图片
//                            try {
//                                image = new Image(getClass().getResource("/images/plants/default.png").toExternalForm());
//                                java.lang.System.out.println("使用默认图片代替");
//                            } catch (Exception ex) {
//                                java.lang.System.out.println("无法加载默认图片: " + ex.getMessage());
//                                throw ex; // 重新抛出异常，以便在外层捕获
//                            }
//                        }
//                        imageView.setImage(image);
//
//                        // 设置植物名称
//                        nameLabel.setText(plant.getName());
//
//                        // 设置数量
//                        quantityLabel.setText("x" + item.getQuantity());
//
//                        setGraphic(hbox);
//                        setText(null);
//                    } catch (Exception e) {
//                        java.lang.System.out.println("显示植物时出错: " + e.getMessage());
//                        e.printStackTrace();
//                        setText(item.getPlant().getName() + " (无法显示图片) x" + item.getQuantity());
//                    }
//                }
//            }
//        });
//
//        // 加载库存数据
//        loadInventory();
//    }
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


//    private void loadInventory() {
//        try {
//            // 从Garden获取已收获的植物
//            Garden garden = Garden.getInstance();
//            List<Plant> plants = garden.getInventory();
//            java.lang.System.out.println("尝试加载库存...");
//
//            if (plants != null) {
//                java.lang.System.out.println("库存列表不为空，包含 " + plants.size() + " 个植物");
//
//                // 清空当前列表
//                harvestedListView.getItems().clear();
//
//                if (!plants.isEmpty()) {
//                    // 创建一个Map来存储每种植物的数量
//                    Map<String, InventoryItem> inventoryMap = new HashMap<>();
//
//                    // 统计每种植物的数量
//                    for (Plant plant : plants) {
//                        String plantName = plant.getName();
//                        java.lang.System.out.println("处理植物: " + plantName +
//                                                  ", 成熟: " + plant.isFullyGrown() +
//                                                  ", 已收获: " + plant.getIsHarvested() +
//                                                  ", 图片路径: " + plant.getCurrentImagePath());
//
//                        if (inventoryMap.containsKey(plantName)) {
//                            // 如果已经有这种植物，增加数量
//                            inventoryMap.get(plantName).incrementQuantity();
//                        } else {
//                            // 如果是新的植物类型，添加到Map中
//                            inventoryMap.put(plantName, new InventoryItem(plant, 1));
//                        }
//                    }
//
//                    // 将合并后的植物项添加到ListView
//                    for (InventoryItem item : inventoryMap.values()) {
//                        harvestedListView.getItems().add(item);
//                        java.lang.System.out.println("添加到ListView: " + item.getPlant().getName() + " x" + item.getQuantity());
//                    }
//
//                    java.lang.System.out.println("已加载 " + inventoryMap.size() + " 种植物到库存视图");
//                } else {
//                    java.lang.System.out.println("库存中没有植物");
//                    // 添加一个提示信息
//                    Label emptyLabel = new Label("库存为空");
//                    emptyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
//                    harvestedListView.setPlaceholder(emptyLabel);
//                }
//            } else {
//                java.lang.System.out.println("无法获取库存列表，返回为null");
//                // 添加一个提示信息
//                Label errorLabel = new Label("无法加载库存");
//                errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");
//                harvestedListView.setPlaceholder(errorLabel);
//            }
//        } catch (Exception e) {
//            java.lang.System.out.println("加载库存时出错: " + e.getMessage());
//            e.printStackTrace();
//
//            // 添加一个提示信息
//            Label errorLabel = new Label("加载库存时出错");
//            errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");
//            harvestedListView.setPlaceholder(errorLabel);
//        }
//    }
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
        floatingMessage.setMaxWidth(280);   // ✅ Prevents cutoff while keeping readable
        floatingMessage.setMinWidth(280);   // ✅ Ensures consistent width
        floatingMessage.setPrefHeight(50);  // ✅ Gives it enough space

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