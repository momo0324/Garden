package model.systems;

import model.sensors.MoistureSensor;
import model.Garden;
import model.plants.Plant;

public class WateringSystem extends SystemAbs {
    private MoistureSensor moistureSensor;
    private Garden garden;
    private int waterTank;
    private static final int EVAPORATION_RATE = 500;
    private static final int WATER_USE_PER_PLANT = 500;

    public WateringSystem(MoistureSensor sensor, Garden garden) {
        super("Watering");
        this.moistureSensor = sensor;
        this.garden = garden;
        this.waterTank = 100000;
    }

    @Override
    public void operate() {
        if (!isActive) {
            System.out.println("Watering system is OFF.");
            return;
        }

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                Plant plant = garden.getPlantAt(row, col);
                if (plant != null && moistureSensor.getSoilMoistureLevel() < 30) {
                    moistureSensor.setSoilMoistureLevel(moistureSensor.getSoilMoistureLevel() + 30);
                    garden.getLogSystem().logEvent("Watering system activated at (" + row + "," + col + ") for " + plant.getClass().getSimpleName());
                }
            }
        }
    }

    public void evaporateWater() {
        waterTank = Math.max(0, waterTank - EVAPORATION_RATE);
        System.out.println("Water evaporated. Remaining water level: " + waterTank + " ml.");
    }

    public int getWaterTank() {
        return waterTank;
    }

    public void refillWaterTank(int amount) {
        this.waterTank += amount;
        System.out.println("Water tank refilled. Current water level: " + waterTank + " ml.");
    }
}
