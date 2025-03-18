package model.systems;

import model.LogSystem;
import model.sensors.PestSensor;
import model.Garden;
import model.plants.Plant;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PestControlSystem extends SystemAbs {
    private PestSensor pestSensor;
    private Garden garden;
    private Map<String, Integer> activePests;
    private LogSystem logSystem=LogSystem.getInstance();

    public PestControlSystem(PestSensor sensor, Garden garden) {
        super("Pest Control");
        this.pestSensor = sensor;
        this.garden = garden;
        this.activePests = new HashMap<>();
    }

    @Override
    public void operate() {
        if (!isActive) {
            System.out.println("Pest control system is OFF.");
            return;
        }

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                Plant plant = garden.getPlantAt(row, col);
                String detectedPest = pestSensor.getDetectedPest();

                if (plant != null && !detectedPest.equals("None")) {
                    if (!activePests.containsKey(detectedPest)) {
                        activePests.put(detectedPest, 2);
                        garden.getLogSystem().logEvent("Pest control initiated at (" + row + "," + col + ") for " + plant.getClass().getSimpleName() + ". Pest detected: " + detectedPest);
                    }
                }
            }
        }

        Iterator<Map.Entry<String, Integer>> iterator = activePests.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            String pest = entry.getKey();
            int remainingTime = entry.getValue();

            if (remainingTime > 1) {
                activePests.put(pest, remainingTime - 1);
                System.out.println("Pest: " + pest + " will be removed in " + (remainingTime - 1) + " hour(s).");
                logSystem.logEvent("🪳 Pest: " + pest + " will be removed in " + (remainingTime - 1) + " hour(s).");
            } else {
                iterator.remove();
                System.out.println("🪳 Pest: " + pest + " has been eliminated.");
                logSystem.logEvent("🪳 Pest: " + pest + " has been eliminated.");
                pestSensor.setDetectedPest("None");
            }
        }
    }
}
