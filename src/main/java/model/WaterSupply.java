package model;

public class WaterSupply {
    private static final int MAX_WATER_LEVEL = Integer.MAX_VALUE; //  TODO: set to unlimited water level for now
    private static final int EVAPORATION_RATE = 500; // 500 ml per hour
    private int currentWaterLevel;

    public WaterSupply() {
        this.currentWaterLevel = MAX_WATER_LEVEL;
    }

    public boolean useWater(int amount) {
        if (currentWaterLevel >= amount) {
            currentWaterLevel -= amount;
            return true;
        }
        return false;
    }

    public void refill() {
        currentWaterLevel = MAX_WATER_LEVEL;
    }

    public void evaporateWater() {
        currentWaterLevel = Math.max(0, currentWaterLevel - EVAPORATION_RATE);
    }

    public int getCurrentWaterLevel() {
        return currentWaterLevel;
    }
} 