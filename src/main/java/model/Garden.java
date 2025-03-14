package model;

import model.plants.Plant;
import model.sensors.*;
import model.systems.*;
import util.TimeManager;
import java.util.*;

public class Garden {
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
    private int waterLevel = 100000; // Initial water level
    private static final int EVAPORATION_RATE = 500; // 500 ml per hour
    private List<String> activePests = new ArrayList<>();

    public Garden() {
        plantGrid = new Plant[GRID_RAW][GRID_COL];
        moistureSensor = new MoistureSensor();
        temperatureSensor = new TemperatureSensor();
        pestSensor = new PestSensor(moistureSensor, temperatureSensor);
        wateringSystem = new WateringSystem(moistureSensor, this);
        pestControlSystem = new PestControlSystem(pestSensor, this);
        lightingSystem = new LightingSystem(new LightingSensor(), this);
        logSystem =LogSystem.getInstance();
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
                    System.err.println("Error creating plant instance: " + e.getMessage());
                }
            }
        }
    }

    /** ✅ Add a Plant to the Garden (For UI & Simulation) **/
    public boolean addPlant(int x, int y, Plant plant) {
        if (x < 0 || x >= GRID_RAW || y < 0 || y >= GRID_COL) {
            logSystem.logEvent("Invalid plant position: (" + x + ", " + y + ").");
            System.out.println("grid size:");
            System.out.println("Invalid plant position: (" + x + ", " + y + ").");
            return false;
        }

        if (plantGrid[x][y] != null) {
            logSystem.logEvent("Position (" + x + ", " + y + ") is already occupied.");
            System.out.println("Position (" + x + ", " + y + ") is already occupied.");
            return false;
        }

        List<int[]> sprinklerPositions = Arrays.asList(new int[]{1, 1}, new int[]{1, 4}, new int[]{4, 1}, new int[]{4, 4});
        if (isSprinklerPosition(x, y, sprinklerPositions)) {
            logSystem.logEvent("Cannot plant at (" + x + ", " + y + ") - Sprinkler is there.");
            System.out.println("Cannot plant at (" + x + ", " + y + ") - Sprinkler is there.");
            return false;
        }

        plantGrid[x][y] = plant;
        logSystem.logEvent("Added " + plant.getClass().getSimpleName() + " at (" + x + ", " + y + ").");
        return true;
    }

    public boolean removePlant(int x, int y) {
        if (x < 0 || x >= GRID_RAW || y < 0 || y >= GRID_COL) {
            logSystem.logEvent("Invalid remove position: (" + x + ", " + y + ").");
            return false;
        }

        if (plantGrid[x][y] == null) {
            logSystem.logEvent("No plant found at (" + x + ", " + y + ").");
            return false;
        }

        logSystem.logEvent("Removed " + plantGrid[x][y].getClass().getSimpleName() + " from (" + x + ", " + y + ").");
        plantGrid[x][y] = null;
        return true;
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
        waterLevel = Math.max(0, waterLevel - EVAPORATION_RATE);
        logSystem.logEvent("Water evaporated. Remaining water: " + waterLevel + " ml.");
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
        return waterLevel;
    }

    public void useWater(int amount) {
        if (waterLevel >= amount) {
            waterLevel -= amount;
            logSystem.logEvent("Water used: " + amount + " ml. Remaining: " + waterLevel + " ml.");
        } else {
            logSystem.logEvent("Not enough water available!");
        }
    }

    public boolean hasActivePests() {
        return !activePests.isEmpty();
    }

    public void applyWatering() {
        wateringSystem.operate();
    }

    public void applyPestControl() {
        pestControlSystem.operate();
    }

    public void applyHeating() {
        temperatureSensor.updateTemperature(); // Ensure latest temperature before deciding to heat
        if (temperatureSensor.getTemperature() < 15) {
            logSystem.logEvent("Temperature low. Heating system activated.");
            temperatureSensor.setTemperature(temperatureSensor.getTemperature() + 5);
        }
    }

    public void applyLighting() {
        int currentHour = TimeManager.getSimulatedHour();
        if (currentHour >= 7 && currentHour < 19) {
            logSystem.logEvent("Sunlight is available. No need for artificial lighting.");
        } else {
            logSystem.logEvent("Sun has set. Turning on artificial lights.");
            lightingSystem.operate();
        }
    }

    public void harvestPlants() {
        logSystem.logEvent("Harvesting system activated.");
    }

    public void logGardenState() {
        logSystem.logEvent("Garden state logged.");
    }

    public Plant getPlantAt(int x, int y) {
        if (x >= 0 && x < GRID_RAW && y >= 0 && y < GRID_COL) {
            return plantGrid[x][y]; // Returns the plant at (x, y) or null if empty
        }
        return null; // Returns null for out-of-bounds coordinates
    }
}
