package model.sensors;

import java.util.Random;

public class MoistureSensor extends Sensor {
    private int soilMoistureLevel;

    public MoistureSensor() {
        super("Moisture");
        this.soilMoistureLevel = new Random().nextInt(101); // Initial value between 0-100
    }

    @Override
    public void readValue() {
        System.out.println("Soil moisture level: " + soilMoistureLevel + "%");
    }

    public int getSoilMoistureLevel() {
        return soilMoistureLevel;
    }

    public void setSoilMoistureLevel(int level) {
        this.soilMoistureLevel = Math.max(0, Math.min(100, level)); // Keep within bounds
    }

    /** ✅ Moisture reduces by 5% every hour **/
    public void reduceSoilMoisture() {
        setSoilMoistureLevel(soilMoistureLevel - 5);
        System.out.println("Soil moisture decreased due to evaporation.");
    }
}
