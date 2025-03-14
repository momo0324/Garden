package model.systems;

import model.plants.Plant;
import model.Inventory;

public class HarvestingSystem extends SystemAbs {
    private Inventory inventory;

    public HarvestingSystem(Inventory inventory) {
        super("Harvesting");
        this.inventory = inventory;
    }

    @Override
    public void operate() {
        for (Plant plant : inventory.getPlants()) {
            if (plant.isFullyGrown() && !plant.getIsHarvested()) {
                plant.harvest();
                inventory.addPlant(plant);
            }
        }
    }
}
