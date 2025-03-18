package model.sensors;

import model.LogSystem;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PestSensor extends Sensor {
    private String detectedPest;
    private MoistureSensor moistureSensor;
    private TemperatureSensor temperatureSensor;
    private LogSystem logSystem;

    public PestSensor(MoistureSensor moistureSensor, TemperatureSensor temperatureSensor) {
        super("Pest");
        this.detectedPest = "None";
        this.moistureSensor = moistureSensor;
        this.temperatureSensor = temperatureSensor;
        this.logSystem=LogSystem.getInstance();
    }

    @Override
    public void readValue() {
        System.out.println("Detected pest: " + detectedPest);
    }

    /** ✅ Pest spawn probability increases with warmth and moisture **/
    public void scanForPests() {
        int baseChance = 10; // Base spawn chance
        int moistureLevel = moistureSensor.getSoilMoistureLevel();
        int temperature = temperatureSensor.getCurrentTemperature();

        // Increase probability if conditions are favorable
        if (moistureLevel > 50) baseChance += 20; // More moisture, higher chance
        if (temperature > 25) baseChance += 15; // Warmer temperatures encourage pest activity

        // Ensure pests are selected only when they are meant to spawn
        if (new Random().nextInt(100) < baseChance) {
            List<String> pests = Arrays.asList("Aphid", "Slug", "SpiderMite", "Caterpillar", "Mealybug", "CarrotRustFly");
            this.detectedPest = pests.get(new Random().nextInt(pests.size())); // Always selects a pest
        } else {
            this.detectedPest = "None"; // No pest detected
        }

        System.out.println("Pest detected: " + detectedPest);
        logSystem.logEvent("🪳 Pest detected: " + detectedPest);

    }

    public String getDetectedPest() {
        return detectedPest;
    }

    public void setDetectedPest(String detectedPest) {
        this.detectedPest = detectedPest;
    }
}
