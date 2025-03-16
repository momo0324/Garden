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
        currentTemperature = 20 + (int)(Math.random() * 11);
        System.out.println("Current temperature: " + currentTemperature + "°C");
    }

    public int getCurrentTemperature() {
        readValue(); // 读取最新温度
        return currentTemperature;
    }

    /** ✅ Allow external systems (like heating) to modify temperature **/
    public void setTemperature(int newTemperature) {
        this.currentTemperature = newTemperature;
    }

    /** ✅ Automatically adjusts temperature based on the time of day **/
    public void updateTemperature() {
        int currentHour = TimeManager.getSimulatedHour();

        if (currentHour >= 6 && currentHour < 12) {
            currentTemperature = new Random().nextInt(6) + 15; // 15°C - 20°C (Morning)
        } else if (currentHour >= 12 && currentHour < 18) {
            currentTemperature = new Random().nextInt(11) + 20; // 20°C - 30°C (Afternoon)
        } else {
            currentTemperature = new Random().nextInt(6) + 10; // 10°C - 15°C (Night)
        }
    }
}
