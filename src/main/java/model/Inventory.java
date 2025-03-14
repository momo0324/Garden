package model;

import model.plants.Plant;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<Plant> harvestedPlants;

    public Inventory() {
        this.harvestedPlants = new ArrayList<>();
    }

    public void addPlant(Plant plant) {
        if (plant.isFullyGrown() && !plant.getIsHarvested()) {
            harvestedPlants.add(plant);
            plant.setHarvested(true);
            System.out.println("Added " + plant.getName() + " to inventory.");
        } else {
            System.out.println("Cannot add " + plant.getName() + " to inventory. Not fully grown or already harvested.");
        }
    }

    public void removePlant(Plant plant) {
        if (harvestedPlants.contains(plant)) {
            harvestedPlants.remove(plant);
            System.out.println("Removed " + plant.getName() + " from inventory.");
        } else {
            System.out.println("Plant not found in inventory.");
        }
    }

    public List<Plant> getPlants() {
        return harvestedPlants;
    }

    public void displayInventory() {
        System.out.println("Inventory contains:");
        for (Plant plant : harvestedPlants) {
            System.out.println("- " + plant.getName());
        }
    }
}
