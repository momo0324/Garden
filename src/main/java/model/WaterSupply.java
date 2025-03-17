package model;

import java.util.Random;

public class WaterSupply {
    private static final int MAX_WATER_LEVEL = 100000; // Limited water storage capacity
    private static final int EVAPORATION_RATE = 500; // 500 ml per hour
    private int currentWaterLevel;

    public WaterSupply() {
        this.currentWaterLevel = MAX_WATER_LEVEL; // Start with a full tank
    }

    public boolean useWater(int amount) {
        if (currentWaterLevel >= amount) {
            currentWaterLevel -= amount;
            return true;
        }
        return false; // Not enough water available
    }

    public void refill(int amount) {
        currentWaterLevel = Math.min(MAX_WATER_LEVEL, currentWaterLevel + amount);
    }

    public void evaporateWater() {
        currentWaterLevel = Math.max(0, currentWaterLevel - EVAPORATION_RATE);
    }

    public void simulateRainfall() {
        Random random = new Random();
        if (random.nextInt(100) < 20) { // 20% chance of rain
            int rainfallAmount = random.nextInt(5000) + 500; // 500 to 5000 ml
            refill(rainfallAmount);
            java.lang.System.out.println("Rainfall occurred. Added " + rainfallAmount + " ml to the water supply.");
        }
    }

    public int getCurrentWaterLevel() {
        return currentWaterLevel;
    }
}
