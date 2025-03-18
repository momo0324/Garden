package model;

import model.plants.Plant;
import java.util.List;
import java.util.ArrayList;

public class EnvironmentSystem {
    private static EnvironmentSystem instance;
    private int waterSupply;
    private List<Plant> inventory;
    private boolean[] sprinklerStatus;
    private boolean[] pestControlStatus;
    private boolean[] heatingStatus;
    private boolean[] lightingStatus;
    private static final int EVAPORATION_RATE = 100; // Assuming a default evaporation rate

    private EnvironmentSystem() {
        waterSupply = 100000;
        inventory = new ArrayList<>();
        sprinklerStatus = new boolean[9]; // 3x3
        pestControlStatus = new boolean[9];
        heatingStatus = new boolean[9];
        lightingStatus = new boolean[9];
    }

    public static EnvironmentSystem getInstance() {
        if (instance == null) {
            instance = new EnvironmentSystem();
        }
        return instance;
    }

    // wateringSystem
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

    // pestControlSystem
    public void applyPestControl(int gridIndex) {
        if (gridIndex < 0 || gridIndex >= pestControlStatus.length) return;
        
        pestControlStatus[gridIndex] = true;
        java.lang.System.out.println("Applying pest control at grid " + gridIndex);
    }

    // heatingSystem
    public void applyHeating(int gridIndex) {
        if (gridIndex < 0 || gridIndex >= heatingStatus.length) return;
        
        heatingStatus[gridIndex] = true;
        java.lang.System.out.println("Applying heating at grid " + gridIndex);
    }

    // lightingSystem
    public void applyLighting(int gridIndex) {
        if (gridIndex < 0 || gridIndex >= lightingStatus.length) return;
        
        lightingStatus[gridIndex] = true;
        java.lang.System.out.println("Applying lighting at grid " + gridIndex);
    }


    public void addRainfall(int amount) {
        waterSupply += amount;
        java.lang.System.out.println("Rainfall added " + amount + "ml to water supply");
    }


    public List<Plant> getInventory() {

        if (!inventory.isEmpty()) {
            for (Plant plant : inventory) {
                java.lang.System.out.println("- " + plant.getName() + ", 成熟: " + plant.isFullyGrown() + ", 已收获: " + plant.getIsHarvested());
            }
        }
        
        return inventory;
    }

    public int getWaterSupply() {
        return waterSupply;
    }

    // resetSystemStatus
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