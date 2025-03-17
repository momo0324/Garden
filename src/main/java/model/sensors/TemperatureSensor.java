package model.sensors;

import util.TimeManager;
import java.util.Random;

public class TemperatureSensor extends Sensor {
    private int currentTemperature;

    public TemperatureSensor() {
        super("Temperature");
        this.currentTemperature = 25; // 默认温度25°C
    }

    @Override
    public void readValue() {
        // 模拟温度变化：在20-30度之间随机波动
        updateTemperature();
        System.out.println("Current temperature: " + currentTemperature + "°C");
    }

    public int getCurrentTemperature() {
        return currentTemperature;
    }

    /** ✅ Allow external systems (like heating) to modify temperature **/
    public void setTemperature(int newTemperature) {
        this.currentTemperature = newTemperature;
    }

    /** ✅ Automatically adjusts temperature based on the time of day **/
    public void updateTemperature() {
        int currentHour = TimeManager.getSimulatedHour(); // In-game hour

        // Map in-game time to a full 24-hour real-world temperature cycle
        double normalizedHour = (double) currentHour / 24.0 * Math.PI * 2;  // Convert hour to range [0, 2π]

        // Define temperature range based on environmental factors
        int minTemp = 15;  // Lowest temp at night
        int maxTemp = 30;  // Peak temp in the afternoon

        // Compute temperature using a sine wave (shifted so 14:00 is the hottest time)
        currentTemperature = (int) ((maxTemp - minTemp) / 2 * Math.sin(normalizedHour - Math.PI / 2) + (maxTemp + minTemp) / 2);

        System.out.println("Updated Temperature: " + currentTemperature + "°C");
    }
}
