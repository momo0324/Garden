package model.sensors;

import java.util.Random;

public class LightingSensor extends Sensor {
    private int sunlightHours; // Hours of sunlight received
    private boolean artificialLightOn;

    public LightingSensor() {
        super("Lighting");
        this.sunlightHours = new Random().nextInt(13); // Random hours (0-12)
        this.artificialLightOn = false;
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

    public boolean isArtificialLightOn() {
        return artificialLightOn;
    }

    public void toggleArtificialLight(boolean state) {
        this.artificialLightOn = state;
        if (state) {
            System.out.println("Artificial lighting turned ON.");
        } else {
            System.out.println("Artificial lighting turned OFF.");
        }
    }
}