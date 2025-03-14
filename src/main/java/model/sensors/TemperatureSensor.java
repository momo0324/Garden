package model.sensors;

import util.TimeManager;
import java.util.Random;

public class TemperatureSensor extends Sensor {
    private int temperature;

    public TemperatureSensor() {
        super("Temperature");
        updateTemperature(); // Initialize with the correct daily cycle
    }

    @Override
    public void readValue() {
        System.out.println("Current temperature: " + temperature + "°C");
    }

    public int getTemperature() {
        return temperature;
    }

    /** ✅ Allow external systems (like heating) to modify temperature **/
    public void setTemperature(int newTemperature) {
        this.temperature = newTemperature;
    }

    /** ✅ Automatically adjusts temperature based on the time of day **/
    public void updateTemperature() {
        int currentHour = TimeManager.getSimulatedHour();

        if (currentHour >= 6 && currentHour < 12) {
            temperature = new Random().nextInt(6) + 15; // 15°C - 20°C (Morning)
        } else if (currentHour >= 12 && currentHour < 18) {
            temperature = new Random().nextInt(11) + 20; // 20°C - 30°C (Afternoon)
        } else {
            temperature = new Random().nextInt(6) + 10; // 10°C - 15°C (Night)
        }
    }
}
