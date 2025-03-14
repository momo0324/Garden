package model.sensors;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PestSensor extends Sensor {
    private String detectedPest;
    private MoistureSensor moistureSensor;
    private TemperatureSensor temperatureSensor;

    public PestSensor(MoistureSensor moistureSensor, TemperatureSensor temperatureSensor) {
        super("Pest");
        this.detectedPest = "None";
        this.moistureSensor = moistureSensor;
        this.temperatureSensor = temperatureSensor;
    }

    @Override
    public void readValue() {
        System.out.println("Detected pest: " + detectedPest);
    }

    /** ✅ Pest spawn probability increases with warmth and moisture **/
    public void scanForPests() {
        int pestChance = 5; // Base chance out of 100
        if (moistureSensor.getSoilMoistureLevel() > 50) pestChance += 15;
        if (temperatureSensor.getTemperature() > 25) pestChance += 10;

        List<String> pests = Arrays.asList("Aphid", "Slug", "SpiderMite", "Caterpillar", "Mealybug", "CarrotRustFly", "None");
        this.detectedPest = (new Random().nextInt(100) < pestChance) ? pests.get(new Random().nextInt(6)) : "None";

        System.out.println("Pest detected: " + detectedPest);
    }

    public String getDetectedPest() {
        return detectedPest;
    }

    public void setDetectedPest(String detectedPest) {
        this.detectedPest = detectedPest;
    }
}
