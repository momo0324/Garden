package model.systems;

public class WaterSupply {
    private int waterTank; // ml of water

    public WaterSupply() {
        this.waterTank = 100000; // Initial water supply
    }

    public void useWater(int amount) {
        if (waterTank >= amount) {
            waterTank -= amount;
            System.out.println(amount + "ml of water used. Remaining water: " + waterTank + "ml.");
        } else {
            System.out.println("Not enough water in supply!");
        }
    }

    public void refillWater(int amount) {
        this.waterTank += amount;
        System.out.println("Water supply refilled. Current water: " + waterTank + "ml.");
    }

    public int getWaterTank() {
        return waterTank;
    }
}
