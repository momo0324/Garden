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
import java.util.List;

public class InventoryController {
    @FXML
    private ListView<Plant> inventoryListView;
    @FXML
    private Button closeButton;

    public void initialize() {
        // 设置ListView的自定义单元格
        inventoryListView.setCellFactory(lv -> new ListCell<Plant>() {
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
            protected void updateItem(Plant plant, boolean empty) {
                super.updateItem(plant, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    // 设置植物图片
                    Image image = new Image(getClass().getResource(plant.getCurrentImagePath()).toExternalForm());
                    imageView.setImage(image);

                    // 设置植物名称
                    nameLabel.setText(plant.getName());

                    // 设置数量（这里假设每个植物有一个数量属性，如果没有需要添加）
                    quantityLabel.setText("x1");

                    setGraphic(hbox);
                    setText(null);
                }
            }
        });

        // 加载库存数据
        loadInventory();
    }

    private void loadInventory() {
        // 从Garden获取已收获的植物
        Garden garden = Garden.getInstance();
        List<Plant> plants = garden.getInventory();
        if (plants != null && !plants.isEmpty()) {
            inventoryListView.getItems().clear();
            inventoryListView.getItems().addAll(plants);
            java.lang.System.out.println("Loaded " + plants.size() + " plants into inventory view");
        } else {
            java.lang.System.out.println("No harvested plants found in inventory");
        }
    }

    @FXML
    private void handleClose() {
        closeButton.getScene().getWindow().hide();
    }
} 