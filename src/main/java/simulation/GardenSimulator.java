package simulation;

import controller.GardenController;
import javafx.application.Platform;
import model.EnvironmentSystem;
import model.Garden;
import model.Inventory;
import model.LogSystem;
import model.plants.Plant;

import java.util.List;
import java.util.Random;

public class GardenSimulator {
    public static void main(String[] args) {
        // ✅ Initialize garden and logging system
        Garden garden = Garden.getInstance();
        Inventory inventory = Inventory.getInstance();
        LogSystem logSystem = LogSystem.getInstance();
        EnvironmentSystem environmentSystem = EnvironmentSystem.getInstance();
        System.out.println("🌱 Starting Garden Simulation...");
        garden.initializeGarden();  // ✅ Initialize the garden grid

        // ✅ Check if seeds exist before planting
        List<Plant> seeds = inventory.getSeeds();
        if (seeds.isEmpty()) {
            System.err.println("⚠️ No seeds available in inventory! Cannot start simulation.");
            return;
        }
        Plant seed = seeds.getFirst(); // ✅ Safe retrieval

        // ✅ Water and plant seed
        environmentSystem.addRainfall(1000);
        for (int i = 0; i < 3; i++) { // ✅ Simulate multiple watering actions
            garden.applyWatering();
            sleepOneHour();
        }
        if (garden.getPlantAt(6,4)==null){
            garden.addPlant(6, 4, seed);
            System.out.println("✅ Planted " + seed.getName() + " at (6,4).");
        }


        // ✅ Day 1: Water the plant
        garden.waterPlant(2, 3);
        sleepOneHour();

        // ✅ Day 2: Increase temperature and add pests
        garden.getTemperatureSensor().setTemperature(28);
        garden.applyPestControl();
        sleepOneHour();

        // ✅ Simulate plant growth over 10 days
        for (int day = 3; day <= 12; day++) {
            System.out.println("🌞 Day " + day + " - Growing plants...");

            // ✅ Adjust temperature dynamically
            int newTemperature = 20 + new Random().nextInt(10);
            garden.getTemperatureSensor().setTemperature(newTemperature);
            System.out.println("🌡️ Temperature set to " + newTemperature + "°C.");

            garden.growPlants(null);
            sleepOneHour();
        }

        // ✅ Harvest if plant is fully grown
        System.out.println("🌾 Checking for harvestable plants...");
        garden.harvestPlants();

        // ✅ Simulate replanting after death (2-sec delay)
        replantAfterDelay(garden, 2, 3);

        // ✅ Final garden status
        garden.getStatus();
    }

    private static void sleepOneHour() {
        try {
            Thread.sleep(1000); // Simulate 1-hour passing (1 second in real-time)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void replantAfterDelay(Garden garden, int x, int y) {
        new Thread(() -> {
            try {
                Thread.sleep(2000); // ✅ Wait 2 seconds before replanting
                List<Plant> availableSeeds = Inventory.getInstance().getSeeds();

                if (!availableSeeds.isEmpty()) {
                    Random random = new Random();
                    Plant newPlant = availableSeeds.get(random.nextInt(availableSeeds.size()));
                    if (garden.getPlantAt(x,y)==null){
                        garden.addPlant(x, y, newPlant);
                        System.out.println("✅ Replanted " + newPlant.getName() + " at (" + x + "," + y + ").");
                    }else{
                        System.out.println("Position ("+x+", "+y+") is already occupied.");
                    }




                } else {
                    System.err.println("⚠️ No seeds available for replanting.");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

}