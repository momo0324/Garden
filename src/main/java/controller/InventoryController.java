package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Garden;
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
    private ListView<InventoryItem> inventoryListView;
    @FXML
    private Button closeButton;

    public void initialize() {
        // 设置ListView的自定义单元格
        inventoryListView.setCellFactory(lv -> new ListCell<InventoryItem>() {
            private HBox hbox = new HBox(10);
            private ImageView imageView = new ImageView();
            private Label nameLabel = new Label();
            private Label quantityLabel = new Label();

            {
                hbox.getChildren().addAll(imageView, nameLabel, quantityLabel);
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
                    try {
                        Plant plant = item.getPlant();
                        // 设置植物图片
                        String imagePath = plant.getCurrentImagePath();
                        java.lang.System.out.println("尝试加载图片: " + imagePath + " 对于植物: " + plant.getName() + 
                                                  ", 成熟: " + plant.isFullyGrown() + 
                                                  ", 已收获: " + plant.getIsHarvested());
                        
                        Image image;
                        try {
                            // 确保使用正确的图片路径
                            if (plant.getIsHarvested() || plant.isFullyGrown()) {
                                // 如果植物已收获或已成熟，使用成熟图片
                                imagePath = plant.getCurrentImagePath();
                                java.lang.System.out.println("使用成熟图片: " + imagePath);
                            }
                            
                            image = new Image(getClass().getResource(imagePath).toExternalForm());
                            java.lang.System.out.println("成功加载图片: " + imagePath);
                        } catch (Exception e) {
                            java.lang.System.out.println("无法加载图片: " + imagePath + ", 错误: " + e.getMessage());
                            // 尝试使用默认图片
                            try {
                                image = new Image(getClass().getResource("/images/plants/default.png").toExternalForm());
                                java.lang.System.out.println("使用默认图片代替");
                            } catch (Exception ex) {
                                java.lang.System.out.println("无法加载默认图片: " + ex.getMessage());
                                throw ex; // 重新抛出异常，以便在外层捕获
                            }
                        }
                        imageView.setImage(image);

                        // 设置植物名称
                        nameLabel.setText(plant.getName());

                        // 设置数量
                        quantityLabel.setText("x" + item.getQuantity());

                        setGraphic(hbox);
                        setText(null);
                    } catch (Exception e) {
                        java.lang.System.out.println("显示植物时出错: " + e.getMessage());
                        e.printStackTrace();
                        setText(item.getPlant().getName() + " (无法显示图片) x" + item.getQuantity());
                    }
                }
            }
        });

        // 加载库存数据
        loadInventory();
    }

    private void loadInventory() {
        try {
            // 从Garden获取已收获的植物
            Garden garden = Garden.getInstance();
            List<Plant> plants = garden.getInventory();
            java.lang.System.out.println("尝试加载库存...");
            
            if (plants != null) {
                java.lang.System.out.println("库存列表不为空，包含 " + plants.size() + " 个植物");
                
                // 清空当前列表
                inventoryListView.getItems().clear();
                
                if (!plants.isEmpty()) {
                    // 创建一个Map来存储每种植物的数量
                    Map<String, InventoryItem> inventoryMap = new HashMap<>();
                    
                    // 统计每种植物的数量
                    for (Plant plant : plants) {
                        String plantName = plant.getName();
                        java.lang.System.out.println("处理植物: " + plantName + 
                                                  ", 成熟: " + plant.isFullyGrown() + 
                                                  ", 已收获: " + plant.getIsHarvested() +
                                                  ", 图片路径: " + plant.getCurrentImagePath());
                        
                        if (inventoryMap.containsKey(plantName)) {
                            // 如果已经有这种植物，增加数量
                            inventoryMap.get(plantName).incrementQuantity();
                        } else {
                            // 如果是新的植物类型，添加到Map中
                            inventoryMap.put(plantName, new InventoryItem(plant, 1));
                        }
                    }
                    
                    // 将合并后的植物项添加到ListView
                    for (InventoryItem item : inventoryMap.values()) {
                        inventoryListView.getItems().add(item);
                        java.lang.System.out.println("添加到ListView: " + item.getPlant().getName() + " x" + item.getQuantity());
                    }
                    
                    java.lang.System.out.println("已加载 " + inventoryMap.size() + " 种植物到库存视图");
                } else {
                    java.lang.System.out.println("库存中没有植物");
                    // 添加一个提示信息
                    Label emptyLabel = new Label("库存为空");
                    emptyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
                    inventoryListView.setPlaceholder(emptyLabel);
                }
            } else {
                java.lang.System.out.println("无法获取库存列表，返回为null");
                // 添加一个提示信息
                Label errorLabel = new Label("无法加载库存");
                errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");
                inventoryListView.setPlaceholder(errorLabel);
            }
        } catch (Exception e) {
            java.lang.System.out.println("加载库存时出错: " + e.getMessage());
            e.printStackTrace();
            
            // 添加一个提示信息
            Label errorLabel = new Label("加载库存时出错");
            errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");
            inventoryListView.setPlaceholder(errorLabel);
        }
    }

    @FXML
    private void handleClose() {
        closeButton.getScene().getWindow().hide();
    }
} 