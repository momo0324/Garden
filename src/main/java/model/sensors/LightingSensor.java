package model.sensors;

import java.util.Random;

public class LightingSensor extends Sensor {
    private int sunlightHours; // Hours of sunlight received

    public LightingSensor() {
        super("Lighting");
        this.sunlightHours = new Random().nextInt(13); // Random hours (0-12)
    }

    @Override
    public void readValue() {
        System.out.println("Sunlight received: " + sunlightHours + " hours");
    }

    public int getSunlightHours() {
        return sunlightHours;
    }

    public void setSunlightHours(int sunlightHours) {
        this.sunlightHours = sunlightHours;
    }
}
