package model.sensors;

import java.util.Random;

public class MoistureSensor extends Sensor {
    private int soilMoistureLevel;

    public MoistureSensor() {
        super("Moisture");
        this.soilMoistureLevel = new Random().nextInt(2496) + 5; // Initial value between 5-2500
        System.out.println("Initial Soil Moisture Level: " + soilMoistureLevel + " ml");
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

    /** Moisture reduces by 5% every hour **/
    public void reduceSoilMoisture() {
        int reduction = (int) (soilMoistureLevel * 0.05); // 5% evaporation
        setSoilMoistureLevel(soilMoistureLevel - reduction);
        System.out.println("Soil moisture decreased due to evaporation. New level: " + soilMoistureLevel + " ml");
    }

}
