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
            System.out.println(monitoredPlant.getName() + " is still growing.");
        }
    }

    public Plant getMonitoredPlant() {
        return monitoredPlant;
    }

    public void setMonitoredPlant(Plant monitoredPlant) {
        this.monitoredPlant = monitoredPlant;
    }
}
