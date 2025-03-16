package model;

import model.plants.Plant;
import model.sensors.*;
import model.systems.*;
import util.TimeManager;
import java.util.*;

public class Garden {
    private static Garden instance;
    private static final int GRID_RAW = 8;
    private static final int GRID_COL = 6;
    private Plant[][] plantGrid;
    private MoistureSensor moistureSensor;
    private TemperatureSensor temperatureSensor;
    private PestSensor pestSensor;
    private WateringSystem wateringSystem;
    private PestControlSystem pestControlSystem;
    private LightingSystem lightingSystem;
    private LogSystem logSystem;
    private WaterSupply waterSupply;
    private List<String> activePests = new ArrayList<>();
    private System gardenSystem = System.getInstance();

    private Garden() {
        plantGrid = new Plant[GRID_RAW][GRID_COL];
        moistureSensor = new MoistureSensor();
        temperatureSensor = new TemperatureSensor();
        pestSensor = new PestSensor(moistureSensor, temperatureSensor);
        wateringSystem = new WateringSystem(moistureSensor, this);
        pestControlSystem = new PestControlSystem(pestSensor, this);
        lightingSystem = new LightingSystem(new LightingSensor(), this);
        logSystem = LogSystem.getInstance();
        waterSupply = new WaterSupply();
    }

    public static Garden getInstance() {
        if (instance == null) {
            instance = new Garden();
        }
        return instance;
    }

    /** ✅ Fix: Provide LogSystem Access **/
    public LogSystem getLogSystem() {
        return logSystem;
    }

    /**  Initialize Garden with Random Plants **/
    public void initializeGarden() {
        //List of all plants
        List<Class<? extends Plant>> plantTypes = Arrays.asList(
//                model.plants.Tomato.class,
//                model.plants.Rose.class,
                model.plants.Eggplant.class,
                model.plants.Lettuce.class,
                model.plants.Lavender.class,
                model.plants.Corn.class,
                model.plants.Pumpkin.class,
                model.plants.Carrot.class
//                model.plants.Orchid.class,
//                model.plants.Basil.class
        );

        Random random = new Random();
        int plantCount = random.nextInt(10) + 15; // Randomly assign 15-25 plants
        //Sprinklers' position (fixed)
        List<int[]> sprinklerPositions = Arrays.asList(new int[]{1, 1}, new int[]{1, 4}, new int[]{4, 1}, new int[]{4, 4});
        int placedPlants = 0;

        while (placedPlants < plantCount) {
            int x = random.nextInt(GRID_RAW);
            int y = random.nextInt(GRID_COL);

            if (plantGrid[x][y] == null && !isSprinklerPosition(x, y, sprinklerPositions)) {
                try {
                    Class<? extends Plant> plantType = plantTypes.get(random.nextInt(plantTypes.size()));
                    Plant plant = plantType.getDeclaredConstructor().newInstance();
                    addPlant(x, y, plant);
                    placedPlants++;
                } catch (Exception e) {
                    java.lang.System.err.println("Error creating plant instance: " + e.getMessage());
                }
            }
        }
    }

    /** ✅ Add a Plant to the Garden (For UI & Simulation) **/
    public boolean addPlant(int x, int y, Plant plant) {
        if (x < 0 || x >= GRID_RAW || y < 0 || y >= GRID_COL) {
            logSystem.logEvent("Invalid plant position: (" + x + ", " + y + ").");
            java.lang.System.out.println("grid size:");
            java.lang.System.out.println("Invalid plant position: (" + x + ", " + y + ").");
            return false;
        }

        if (plantGrid[x][y] != null) {
            logSystem.logEvent("Position (" + x + ", " + y + ") is already occupied.");
            java.lang.System.out.println("Position (" + x + ", " + y + ") is already occupied.");
            return false;
        }

        List<int[]> sprinklerPositions = Arrays.asList(new int[]{1, 1}, new int[]{1, 4}, new int[]{4, 1}, new int[]{4, 4});
        if (isSprinklerPosition(x, y, sprinklerPositions)) {
            logSystem.logEvent("Cannot plant at (" + x + ", " + y + ") - Sprinkler is there.");
            java.lang.System.out.println("Cannot plant at (" + x + ", " + y + ") - Sprinkler is there.");
            return false;
        }

        plantGrid[x][y] = plant;
        logSystem.logEvent("Added " + plant.getClass().getSimpleName() + " at (" + x + ", " + y + ").");
        return true;
    }

    /**
     * 从花园中移除指定位置的植物
     * @param x 行坐标
     * @param y 列坐标
     * @return 被移除的植物，如果该位置没有植物则返回null
     */
    public Plant removePlant(int x, int y) {
        if (x < 0 || x >= GRID_RAW || y < 0 || y >= GRID_COL) {
            return null;
        }
        
        Plant removedPlant = plantGrid[x][y];
        if (removedPlant != null) {
            plantGrid[x][y] = null;
            logSystem.logEvent("Removed " + removedPlant.getClass().getSimpleName() + " from (" + x + ", " + y + ").");
        }
        return removedPlant;
    }

    private boolean isSprinklerPosition(int x, int y, List<int[]> sprinklerPositions) {
        for (int[] pos : sprinklerPositions) {
            if (pos[0] == x && pos[1] == y) {
                return true;
            }
        }
        return false;
    }

    public void evaporateWater() {
        waterSupply.evaporateWater();
        logSystem.logEvent("Water evaporated. Remaining water: " + waterSupply.getCurrentWaterLevel() + " ml.");
    }

    public void spawnRandomPests() {
        pestSensor.scanForPests();
        String detectedPest = pestSensor.getDetectedPest();
        if (!detectedPest.equals("None")) {
            activePests.add(detectedPest);
            logSystem.logEvent("Pest infestation detected: " + detectedPest);
        }
    }

    public int getWaterLevel() {
        return waterSupply.getCurrentWaterLevel();
    }

    public void useWater(int amount) {
        if (waterSupply.useWater(amount)) {
            logSystem.logEvent("Water used: " + amount + " ml. Remaining: " + waterSupply.getCurrentWaterLevel() + " ml.");
        } else {
            logSystem.logEvent("Not enough water available!");
        }
    }

    public boolean hasActivePests() {
        return !activePests.isEmpty();
    }

    public void applyWatering() {
        for (int i = 0; i < GRID_RAW; i++) {
            for (int j = 0; j < GRID_COL; j++) {
                waterPlant(i, j);
            }
        }
    }

    public void applyPestControl() {
        pestControlSystem.operate();
    }

    public void applyHeating() {
        temperatureSensor.updateTemperature(); // Ensure latest temperature before deciding to heat
        if (temperatureSensor.getCurrentTemperature() < 15) {
            logSystem.logEvent("Temperature low. Heating system activated.");
            temperatureSensor.setTemperature(temperatureSensor.getCurrentTemperature() + 5);
        }
    }

    public void applyLighting() {
        int currentHour = TimeManager.getSimulatedHour();
        if (currentHour%24 >= 7 && currentHour%24 < 19) {
            logSystem.logEvent("Sunlight is available. No need for artificial lighting.");
        } else {
            logSystem.logEvent("Sun has set. Turning on artificial lights.");
            lightingSystem.operate();
        }
    }

    public void harvestPlants() {
        logSystem.logEvent("Harvesting system activated.");
        java.lang.System.out.println("开始收获植物...");
        
        int harvestedCount = 0;
        for (int i = 0; i < GRID_RAW; i++) {
            for (int j = 0; j < GRID_COL; j++) {
                Plant plant = plantGrid[i][j];
                if (plant != null) {
                    java.lang.System.out.println("检查位置 (" + i + ", " + j + ") 的植物: " + plant.getName());
                    java.lang.System.out.println("植物成熟状态: " + plant.isFullyGrown() + ", 已收获状态: " + plant.getIsHarvested());
                    
                    if (plant.isFullyGrown() && !plant.getIsHarvested()) {
                        gardenSystem.harvestPlant(plant);
                        plantGrid[i][j] = null; // 从网格中移除植物
                        logSystem.logEvent("Harvested " + plant.getName() + " from (" + i + ", " + j + ").");
                        harvestedCount++;
                    }
                }
            }
        }
        
        java.lang.System.out.println("收获完成，共收获了 " + harvestedCount + " 个植物");
        java.lang.System.out.println("当前库存中有 " + gardenSystem.getInventory().size() + " 个植物");
    }

    public void logGardenState() {
        logSystem.logEvent("Garden state logged.");
    }

    public void growPlants() {
        int currentHour = TimeManager.getSimulatedHour();
        int sunlightHours = 0;
        switch (currentHour%24) {
            case 10: {
                sunlightHours = 3;
                break;
            }
            case 15: {
                sunlightHours = 5;
                break;
            }
            case 20: {
                sunlightHours = 4;
                break;
            }
            default: {
                sunlightHours = 0;
            }
        }

        for (int i = 0; i < GRID_RAW; i++) {
            for (int j = 0; j < GRID_COL; j++) {
                Plant plant = plantGrid[i][j];
                if (plant != null && !plant.getIsHarvested()) {
                    plant.growOneDay(sunlightHours);
                    logSystem.logEvent(plant.getName() + " at (" + i + "," + j + ") growth hours: " + 
                                    plant.getCurrentGrowthHours() + "/" + plant.getHoursToGrow());
                }
            }
        }
    }

    public Plant getPlantAt(int x, int y) {
        if (x >= 0 && x < GRID_RAW && y >= 0 && y < GRID_COL) {
            return plantGrid[x][y]; // Returns the plant at (x, y) or null if empty
        }
        return null; // Returns null for out-of-bounds coordinates
    }

    public List<Plant> getInventory() {
        return gardenSystem.getInventory();
    }

    public void waterPlant(int x, int y) {
        Plant plant = getPlantAt(x, y);
        if (plant != null) {
            if (waterSupply.useWater(500)) { // 使用500ml水
                plant.water(500);
                logSystem.logEvent("Watered plant at (" + x + ", " + y + ") with 500ml water.");
            } else {
                logSystem.logEvent("Not enough water to water plant at (" + x + ", " + y + ").");
            }
        }
    }

    public int getCurrentTemperature() {
        return temperatureSensor.getCurrentTemperature();
    }

    public void toggleLights() {
        lightingSystem.setActive(!lightingSystem.isActive());
        if (lightingSystem.isActive()) {
            logSystem.logEvent("Lighting system turned ON.");
            // 给所有植物增加额外的阳光时间
            for (int i = 0; i < GRID_RAW; i++) {
                for (int j = 0; j < GRID_COL; j++) {
                    Plant plant = plantGrid[i][j];
                    if (plant != null) {
                        plant.addSunlight(4);
                    }
                }
            }
        } else {
            logSystem.logEvent("Lighting system turned OFF.");
            // 重置所有植物的额外阳光时间
            for (int i = 0; i < GRID_RAW; i++) {
                for (int j = 0; j < GRID_COL; j++) {
                    Plant plant = plantGrid[i][j];
                    if (plant != null) {
                        plant.resetAdditionalSunlight();
                    }
                }
            }
        }
    }
}
