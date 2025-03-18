package model;

import controller.GardenController;
import javafx.application.Platform;
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
    private EnvironmentSystem gardenSystem = EnvironmentSystem.getInstance();

    private LightingSensor lightingSensor;

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
        lightingSensor = new LightingSensor();
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
//        //List of all plants
//        List<Class<? extends Plant>> plantTypes = Arrays.asList(
////                model.plants.Tomato.class,
////                model.plants.Rose.class,
//                model.plants.Eggplant.class,
//                model.plants.Lettuce.class,
//                model.plants.Lavender.class,
//                model.plants.Corn.class,
//                model.plants.Pumpkin.class,
//                model.plants.Carrot.class
////                model.plants.Orchid.class,
////                model.plants.Basil.class
//        );

        Random random = new Random();
        int plantCount = 32; // Randomly assign 20-30 plants
        //Sprinklers' position (fixed)
        List<int[]> sprinklerPositions = Arrays.asList(new int[]{1, 1}, new int[]{1, 4}, new int[]{4, 1}, new int[]{4, 4});
        int placedPlants = 0;

        while (placedPlants < plantCount) {
            int x = random.nextInt(GRID_RAW);
            int y = random.nextInt(GRID_COL);

            if (plantGrid[x][y] == null && !isSprinklerPosition(x, y, sprinklerPositions)) {
                try {
                    // ✅ Get available seeds from inventory
                    List<Plant> availableSeeds = Inventory.getInstance().getSeeds();

                    if (availableSeeds.isEmpty()) {
                        java.lang.System.err.println("⚠️ No seeds available in inventory for replanting.");
                        return; // No seeds available, so don't replant
                    }
                    // ✅ Randomly select a seed from inventory
                    Plant selectedSeed = availableSeeds.get(random.nextInt(availableSeeds.size()));

                    // ✅ Create a new instance of the selected seed
                    Plant plant = selectedSeed.getClass().getDeclaredConstructor().newInstance();
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

        // Simulate rainfall
        waterSupply.simulateRainfall();
        logSystem.logEvent("Current water level after possible rainfall: " + waterSupply.getCurrentWaterLevel() + " ml.");
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
                Plant plant = plantGrid[i][j];
                if (plant != null) {
                    int requiredWater = plant.getMinWaterRequirement();
                    int currentMoisture = moistureSensor.getSoilMoistureLevel();

                    if (currentMoisture < requiredWater) {
                        if (waterSupply.useWater(requiredWater)) {
                            plant.water(requiredWater);
                            logSystem.logEvent("Watered plant at (" + i + ", " + j + ") with " + requiredWater + " ml water.");
                        } else {
                            logSystem.logEvent("Not enough water to water plant at (" + i + ", " + j + ").");
                        }
                    } else {
                        logSystem.logEvent("Plant at (" + i + ", " + j + ") does not need watering.");
                    }
                }
            }
        }
    }

    public void applyPestControl() {
        pestControlSystem.operate();
    }

    public void applyHeating() {
        int currentTemperature = temperatureSensor.getCurrentTemperature();
        int minRequiredTemperature = Integer.MAX_VALUE;
        boolean plantsExist = false;

        for (int i = 0; i < GRID_RAW; i++) {
            for (int j = 0; j < GRID_COL; j++) {
                Plant plant = plantGrid[i][j];
                if (plant != null) {
                    plantsExist = true;
                    minRequiredTemperature = Math.min(minRequiredTemperature, plant.getMinIdealTemperature());
                }
            }
        }

        if (!plantsExist) {
            logSystem.logEvent("No plants in the garden. Heating system turned OFF.");
            return; // ✅ Stop heating if no plants are present
        }

        if (currentTemperature < minRequiredTemperature) {
            logSystem.logEvent("Temperature too low (" + currentTemperature + "°C). Heating system activated.");

            int newTemperature = Math.min(minRequiredTemperature, currentTemperature + 5); // ✅ Prevent overheating
            temperatureSensor.setTemperature(newTemperature);

            logSystem.logEvent("Temperature increased to " + temperatureSensor.getCurrentTemperature() + "°C.");
        } else {
            logSystem.logEvent("Temperature is adequate (" + currentTemperature + "°C). No heating needed.");
        }
    }

    public TemperatureSensor getTemperatureSensor() {
        return temperatureSensor;
    }
    public void applyLighting() {
        int currentHour = TimeManager.getSimulatedHour();
        boolean isNightTime = currentHour % 24 < 7 || currentHour % 24 >= 19;
        boolean artificialLightNeeded = false;

        for (int i = 0; i < GRID_RAW; i++) {
            for (int j = 0; j < GRID_COL; j++) {
                Plant plant = plantGrid[i][j];
                if (plant != null && !plant.isFullyGrown()) {
                    int requiredSunlight = plant.getSunlightNeeded();
                    int receivedSunlight = lightingSensor.getSunlightHours();

                    if (receivedSunlight < requiredSunlight) {
                        artificialLightNeeded = true;
                    }
                }
            }
        }

        if (isNightTime && artificialLightNeeded) {
            lightingSensor.toggleArtificialLight(true);
            logSystem.logEvent("Artificial lights turned ON to support plant growth.");
        } else {
            lightingSensor.toggleArtificialLight(false);
            logSystem.logEvent("Artificial lights turned OFF.");
        }
    }

    public void checkPlantHealth() {
        Random random = new Random();

        for (int i = 0; i < GRID_RAW; i++) {
            for (int j = 0; j < GRID_COL; j++) {
                Plant plant = plantGrid[i][j];
                if (plant != null && !plant.isFullyGrown()) {
                    // Fetch environmental conditions
                    int soilMoisture = moistureSensor.getSoilMoistureLevel();
                    int currentLightHours = lightingSystem.getSunlightHours();
                    int currentTemperature = temperatureSensor.getCurrentTemperature();

                    // Plant's required conditions
                    int requiredWater = plant.getMinWaterRequirement();
                    int requiredSunlight = plant.getSunlightNeeded();
                    int minIdealTemperature = plant.getMinIdealTemperature();
                    int maxIdealTemperature = plant.getMaxIdealTemperature();

                    // Allow small temperature fluctuations (±2 degrees tolerance)
                    boolean isIdealTemperature = (currentTemperature >= minIdealTemperature - 2) &&
                            (currentTemperature <= maxIdealTemperature + 2);

                    boolean hasEnoughWater = soilMoisture >= requiredWater;
                    boolean hasEnoughSunlight = currentLightHours >= requiredSunlight;

                    // Log plant conditions for debugging
                    logSystem.logEvent("Checking health of " + plant.getName() + " at (" + i + "," + j + ") - " +
                            "Water: " + soilMoisture + "/" + requiredWater + ", " +
                            "Sunlight: " + currentLightHours + "/" + requiredSunlight + ", " +
                            "Temperature: " + currentTemperature + "°C (Ideal: " + minIdealTemperature + "-" + maxIdealTemperature + "°C)");

                    // If any condition is not met, decrement survival time with a small grace period
                    if (!hasEnoughWater || !hasEnoughSunlight || !isIdealTemperature) {
                        if (plant.getCurrentSurvivalTime() > 2) { // Small buffer before decrementing
                            plant.decreaseSurvivalTime();
                            java.lang.System.out.println("Warning: " + plant.getName() + " at (" + i + "," + j + ") is struggling. Remaining survival time: " + plant.getCurrentSurvivalTime() + " hours.");
                        }
                    } else {
                        // Reset survival time if all conditions are met
                        plant.resetSurvivalTime(currentLightHours, currentTemperature);
                    }

                    // If survival time runs out, plant dies and gets replaced
                    if (plant.getCurrentSurvivalTime() <= 0) {
                        logSystem.logEvent(plant.getName() + " at (" + i + "," + j + ") has died due to prolonged unfavorable conditions.");
                        plantGrid[i][j] = null;
                        java.lang.System.out.println(plant.getName() + " at (" + i + "," + j + ") has died and has been removed from the garden.");

                        // Replant a new random plant after death
                        try {
                            // ✅ Get available seeds from inventory
                            List<Plant> availableSeeds = Inventory.getInstance().getSeeds();

                            if (availableSeeds.isEmpty()) {
                                java.lang.System.err.println("⚠️ No seeds available in inventory for replanting.");
                                return; // No seeds available, so don't replant
                            }
                            // ✅ Randomly select a seed from inventory
                            Plant selectedSeed = availableSeeds.get(random.nextInt(availableSeeds.size()));

                            // ✅ Create a new instance of the selected seed
                            Plant newPlant = selectedSeed.getClass().getDeclaredConstructor().newInstance();
                            plantGrid[i][j] = newPlant;
                            newPlant.resetSurvivalTime(currentLightHours, currentTemperature);
                            logSystem.logEvent("Replanted " + newPlant.getName() + " at (" + i + ", " + j + ").");
                        } catch (Exception e) {
                            java.lang.System.err.println("Error replanting after plant death: " + e.getMessage());
                        }
                    }
                }
            }
        }
    }


    public void harvestPlants() {
        logSystem.logEvent("Harvesting system activated.");
        java.lang.System.out.println("Start Harvesting Plants...");
        Inventory inventory=Inventory.getInstance();

//        List<Class<? extends Plant>> plantTypes = Arrays.asList(
//                model.plants.Eggplant.class,
//                model.plants.Lettuce.class,
//                model.plants.Lavender.class,
//                model.plants.Corn.class,
//                model.plants.Pumpkin.class,
//                model.plants.Carrot.class
//        );
        Random random = new Random();

        int harvestedCount = 0;
        for (int i = 0; i < GRID_RAW; i++) {
            for (int j = 0; j < GRID_COL; j++) {
                Plant plant = plantGrid[i][j];
                if (plant != null && plant.isFullyGrown() && !plant.getIsHarvested()) {
                    inventory.addPlant(plant);
                    plantGrid[i][j] = null; // 从网格中移除植物
                    logSystem.logEvent("Harvested " + plant.getName() + " from (" + i + ", " + j + ").");
                    harvestedCount++;

                    // ✅ Replant a new random plant after harvest
                    try {
                        // ✅ Get available seeds from inventory
                        List<Plant> availableSeeds = Inventory.getInstance().getSeeds();

                        if (availableSeeds.isEmpty()) {
                            java.lang.System.err.println("⚠️ No seeds available in inventory for replanting.");
                            return; // No seeds available, so don't replant
                        }

                        // ✅ Randomly select a seed from inventory
                        Plant selectedSeed = availableSeeds.get(random.nextInt(availableSeeds.size()));

                        // ✅ Create a new instance of the selected seed
                        Plant newPlant = selectedSeed.getClass().getDeclaredConstructor().newInstance();
                        plantGrid[i][j] = newPlant;
                        logSystem.logEvent("Replanted " + newPlant.getName() + " at (" + i + ", " + j + ").");
                    } catch (Exception e) {
                        java.lang.System.err.println("Error replanting after harvest: " + e.getMessage());
                    }
                }
            }
        }

        if (harvestedCount == 0) {
            java.lang.System.out.println("No Plant Harvested.");
            logSystem.logEvent("No Plant Harvested.");
        } else {
            java.lang.System.out.println("Harvest Complete, Harvested " + harvestedCount + " Plants");
            java.lang.System.out.println("There are " + gardenSystem.getInventory().size() + " Plants in Inventory");
        }
    }

    public void logGardenState() {
        logSystem.logEvent("Garden state logged.");
    }

    public void growPlants(GardenController gardenController) {
        for (int i = 0; i < GRID_RAW; i++) {
            for (int j = 0; j < GRID_COL; j++) {
                Plant plant = plantGrid[i][j];
                if (plant != null && !plant.getIsHarvested()) {
                    int soilMoisture = moistureSensor.getSoilMoistureLevel();
                    int currentLightHours = lightingSystem.getSunlightHours();
                    int currentTemperature = temperatureSensor.getCurrentTemperature();

                    boolean hasEnoughWater = soilMoisture >= plant.getMinWaterRequirement();
                    boolean hasEnoughSunlight = currentLightHours >= plant.getSunlightNeeded();

                    // Allow a slight temperature tolerance
                    boolean isIdealTemperature = (currentTemperature >= plant.getMinIdealTemperature() - 2) &&
                            (currentTemperature <= plant.getMaxIdealTemperature() + 2);

                    // Debugging Log
                    logSystem.logEvent("Checking growth of " + plant.getName() + " at (" + i + "," + j + ") - " +
                            "Water: " + soilMoisture + "/" + plant.getMinWaterRequirement() + ", " +
                            "Sunlight: " + currentLightHours + "/" + plant.getSunlightNeeded() + ", " +
                            "Temperature: " + currentTemperature + "°C (Ideal: " + plant.getMinIdealTemperature() + "-" + plant.getMaxIdealTemperature() + "°C)");

                    if (hasEnoughWater && hasEnoughSunlight && isIdealTemperature) {
                        plant.growOneDay(currentLightHours,this,gardenController);

                        logSystem.logEvent("🌱 " + plant.getName() + " at (" + i + "," + j + ") grew! Growth: " +
                                plant.getCurrentGrowthHours() + "/" + plant.getHoursToGrow());
                    } else {
                        logSystem.logEvent("🚨 " + plant.getName() + " at (" + i + "," + j + ") did NOT grow due to insufficient conditions.");
                    }
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
