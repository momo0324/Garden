package model;

import model.plants.Plant;
import java.util.List;
import java.util.ArrayList;

public class System {
    private static System instance;
    private int waterSupply; // 水供应量（毫升）
    private List<Plant> inventory; // 库存
    private boolean[] sprinklerStatus; // 喷水器状态
    private boolean[] pestControlStatus; // 害虫控制状态
    private boolean[] heatingStatus; // 加热系统状态
    private boolean[] lightingStatus; // 照明系统状态
    private static final int EVAPORATION_RATE = 100; // Assuming a default evaporation rate

    private System() {
        waterSupply = 100000; // 初始水量
        inventory = new ArrayList<>();
        sprinklerStatus = new boolean[9]; // 3x3网格
        pestControlStatus = new boolean[9];
        heatingStatus = new boolean[9];
        lightingStatus = new boolean[9];
    }

    public static System getInstance() {
        if (instance == null) {
            instance = new System();
        }
        return instance;
    }

    // 浇水系统
    public void waterPlant(int gridIndex, Plant plant) {
        if (gridIndex < 0 || gridIndex >= sprinklerStatus.length) return;
        
        int waterNeeded = plant.getMinWaterRequirement();
        if (waterSupply >= waterNeeded) {
            waterSupply -= waterNeeded;
            sprinklerStatus[gridIndex] = true;
            java.lang.System.out.println("Watering plant at grid " + gridIndex + " with " + waterNeeded + "ml water");
        } else {
            java.lang.System.out.println("Not enough water supply!");
        }
    }

    // 害虫控制系统
    public void applyPestControl(int gridIndex) {
        if (gridIndex < 0 || gridIndex >= pestControlStatus.length) return;
        
        pestControlStatus[gridIndex] = true;
        java.lang.System.out.println("Applying pest control at grid " + gridIndex);
    }

    // 加热系统
    public void applyHeating(int gridIndex) {
        if (gridIndex < 0 || gridIndex >= heatingStatus.length) return;
        
        heatingStatus[gridIndex] = true;
        java.lang.System.out.println("Applying heating at grid " + gridIndex);
    }

    // 照明系统
    public void applyLighting(int gridIndex) {
        if (gridIndex < 0 || gridIndex >= lightingStatus.length) return;
        
        lightingStatus[gridIndex] = true;
        java.lang.System.out.println("Applying lighting at grid " + gridIndex);
    }

    // 收获系统
    public void harvestPlant(Plant plant) {
        if (plant.isFullyGrown() && !plant.getIsHarvested()) {
            inventory.add(plant);
            plant.setHarvested(true);
            java.lang.System.out.println("Plant " + plant.getName() + " harvested and added to inventory");
        }
    }

    // 添加降雨
    public void addRainfall(int amount) {
        waterSupply += amount;
        java.lang.System.out.println("Rainfall added " + amount + "ml to water supply");
    }

    // 获取库存
    public List<Plant> getInventory() {
        return inventory;
    }

    // 获取水供应量
    public int getWaterSupply() {
        return waterSupply;
    }

    // 重置系统状态
    public void resetSystemStatus() {
        for (int i = 0; i < sprinklerStatus.length; i++) {
            sprinklerStatus[i] = false;
            pestControlStatus[i] = false;
            heatingStatus[i] = false;
            lightingStatus[i] = false;
        }
    }

    public void evaporateWater() {
        waterSupply = Math.max(0, waterSupply - EVAPORATION_RATE);
        java.lang.System.out.println("Water evaporated. Remaining water level: " + waterSupply + " ml.");
    }
} 