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

        int[][] sprinklerCoverage = {
            {0, 0, 2, 2},
            {0, 3, 2, 5},
            {3, 0, 5, 2},
            {3, 3, 5, 5}
        };

        for (int[] coverage : sprinklerCoverage) {
            for (int row = coverage[0]; row <= coverage[2]; row++) {
                for (int col = coverage[1]; col <= coverage[3]; col++) {
                    garden.waterPlant(row, col);
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
