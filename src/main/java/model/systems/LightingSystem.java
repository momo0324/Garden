package model.systems;

import model.sensors.LightingSensor;
import model.Garden;
import model.plants.Plant;
import util.TimeManager;

public class LightingSystem extends SystemAbs {
    private LightingSensor lightingSensor;
    private Garden garden;

    public LightingSystem(LightingSensor sensor, Garden garden) {
        super("Lighting");
        this.lightingSensor = sensor;
        this.garden = garden;
    }

    @Override
    public void operate() {
        int currentHour = TimeManager.getSimulatedHour();
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                Plant plant = garden.getPlantAt(row, col);
                if (plant != null) {
                    if (currentHour >= 7 && currentHour < 19) {
                        garden.getLogSystem().logEvent("Sunlight sufficient at (" + row + "," + col + ") for " + plant.getClass().getSimpleName());
                    } else {
                        garden.getLogSystem().logEvent("Lighting system activated at (" + row + "," + col + ") for " + plant.getClass().getSimpleName());
                        lightingSensor.setSunlightHours(6);
                    }
                }
            }
        }
    }
}
