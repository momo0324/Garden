package model.sensors;

import model.plants.Plant;

public class GrowthSensor extends Sensor {
    private Plant monitoredPlant;

    public GrowthSensor(Plant plant) {
        super("Growth");
        this.monitoredPlant = plant;
    }

    @Override
    public void readValue() {
        if (monitoredPlant.isFullyGrown()) {
            System.out.println(monitoredPlant.getName() + " is fully grown and ready for harvest.");
        } else {
            System.out.println(monitoredPlant.getName() + " is still growing (" + monitoredPlant.getCurrentGrowthHours() + "/" + monitoredPlant.getHoursToGrow() + " hours).");
        }
    }

    /** ✅ Updates plant growth if conditions are met **/
    public void updateGrowth(boolean hasEnoughWater, boolean hasEnoughSunlight, boolean isIdealTemperature) {
        if (monitoredPlant.isFullyGrown()) {
            return; // Already fully grown, no need to update
        }

        // Check if all growth conditions are met
        if (hasEnoughWater && hasEnoughSunlight && isIdealTemperature) {
            monitoredPlant.grow(1); // Simulating 1 hour of growth
        }
    }

    public Plant getMonitoredPlant() {
        return monitoredPlant;
    }

    public void setMonitoredPlant(Plant monitoredPlant) {
        this.monitoredPlant = monitoredPlant;
    }
}
