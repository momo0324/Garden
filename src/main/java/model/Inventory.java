package model;

import model.plants.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Inventory {
    private List<Plant> harvestedPlants;
    private List<Plant> seeds; // New list to store seeds separately
    private static Inventory instance; // Singleton instance
    private LogSystem logSystem=LogSystem.getInstance();

    public Inventory() {
        this.seeds = new ArrayList<>();
        this.harvestedPlants = new ArrayList<>();
        initializeDefaultSeeds();
    }
    public static Inventory getInstance() {
        if (instance == null) {
            instance = new Inventory(); // Initialize only once
        }
        return instance;
    }


    public void addPlant(Plant plant) {
        if (plant.isFullyGrown() && !plant.getIsHarvested()) {
            harvestedPlants.add(plant);
            plant.setHarvested(true);
            java.lang.System.out.println("Added " + plant.getName() + " to inventory.");
        } else {
            java.lang.System.out.println("Cannot add " + plant.getName() + " to inventory. Not fully grown or already harvested.");
        }
    }

    public void removePlant(Plant plant) {
        if (harvestedPlants.contains(plant)) {
            harvestedPlants.remove(plant);
            java.lang.System.out.println("Removed " + plant.getName() + " from inventory.");
        } else {
            java.lang.System.out.println("Plant not found in inventory.");
        }
    }

    public List<Plant> getHarvestedPlants() {
        return harvestedPlants;
    }

    public void displayInventory() {
        java.lang.System.out.println("Inventory contains:");
        for (Plant plant : harvestedPlants) {
            java.lang.System.out.println("- " + plant.getName());
        }
    }

    public boolean addSeed(Plant seed) {
        if (!seeds.contains(seed)) {
            seeds.add(seed);
            System.out.println("Seed added: " + seed.getName());
            return true;
        }
        return false;
    }

    /**
     * Removes a seed when it is planted.
     * @param seed The seed to remove.
     * @return true if successfully removed, false otherwise.
     */
    public boolean removeSeed(Plant seed) {
        if (seeds.remove(seed)) {
            System.out.println("Seed planted and removed from inventory: " + seed.getName());
            return true;
        }
        System.out.println("Seed not found in inventory: " + seed.getName());
        return false;
    }
    public List<Plant> getSeeds() {
        return new ArrayList<>(seeds);
    }
    public boolean hasSeed(String plantName) {
        return seeds.stream().anyMatch(seed -> seed.getName().equals(plantName));
    }
    public void updateSeed(Plant newSeed) {
        for (int i = 0; i < seeds.size(); i++) {
            if (seeds.get(i).getName().equals(newSeed.getName())) {
                seeds.set(i, newSeed);
                return;
            }
        }
    }

    private void initializeDefaultSeeds() {
        try {
            seeds.add(new Lavender());
            seeds.add(new Tomato());
            seeds.add(new Watermelon());
            seeds.add(new Lettuce());

            System.out.println("Default seeds added: Lavender , Tomato, Watermelon & Lettuce");
            logSystem.logEvent("Default seeds added: Lavender, Tomato, Watermelon & Lettuce");

        } catch (Exception e) {
            System.err.println("Error initializing default seeds: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
