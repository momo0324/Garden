package model.systems;

import model.sensors.TemperatureSensor;
import model.Garden;
import model.plants.Plant;

public class HeatingSystem extends SystemAbs {
    private TemperatureSensor temperatureSensor;
    private Garden garden;
    private static final int HEATING_INCREMENT = 5;
    private static final int MIN_TEMPERATURE_THRESHOLD = 15;

    public HeatingSystem(TemperatureSensor sensor, Garden garden) {
        super("Heating");
        this.temperatureSensor = sensor;
        this.garden = garden;
    }

    @Override
    public void operate() {
        if (!isActive) {
            System.out.println("Heating system is OFF.");
            return;
        }

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                Plant plant = garden.getPlantAt(row, col);
                if (plant != null && temperatureSensor.getTemperature() < MIN_TEMPERATURE_THRESHOLD) {
                    int newTemp = temperatureSensor.getTemperature() + HEATING_INCREMENT;
                    temperatureSensor.setTemperature(newTemp);
                    garden.getLogSystem().logEvent("Heating system activated at (" + row + "," + col + ") for " + plant.getClass().getSimpleName());
                }
            }
        }
    }
}
