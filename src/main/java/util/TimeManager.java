package util;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;
import model.Garden;

public class TimeManager {
    private static double realSecondsPerSimHour = 1.0; // Default speed
    private static Timeline simulationTimeline;
    private static int simulatedHour = 0;
    private static Garden garden;
    private static int simulatedDay = 1;

    @FXML
    private Label dayHourLabel; // Reference to FXML label for displaying time

    public static void startSimulation(Garden gardenInstance) {
        garden = gardenInstance;
        restartSimulation();
    }

    public static void restartSimulation() {
        if (simulationTimeline != null) {
            simulationTimeline.stop();
        }

        simulationTimeline = new Timeline(new KeyFrame(Duration.seconds(realSecondsPerSimHour), event -> {
            simulatedHour++;
            updateGardenState();
        }));
        simulationTimeline.setCycleCount(Timeline.INDEFINITE);
        simulationTimeline.play();
    }

    private static void updateGardenState() {
        System.out.println("Simulated Hour: " + simulatedHour);

        // ✅ Ensure all systems take action at the correct times
        garden.evaporateWater();

        if (simulatedHour == 7) {
            garden.applyLighting(); // Sunlight starts
        } else if (simulatedHour == 19) {
            garden.applyLighting(); // Sunlight ends
        }

        garden.applyWatering();
        garden.applyPestControl();
        garden.applyHeating();
        garden.applyLighting();
        garden.checkPlantHealth();
        garden.harvestPlants();

        // ✅ Ensure pests spawn dynamically
        if (simulatedHour % 6 == 0) { // Pests spawn every 6 hours
            garden.spawnRandomPests();
        }
    }

    public static void stopSimulation() {
        if (simulationTimeline != null) {
            simulationTimeline.stop();
        }
    }

    public static int getSimulatedHour() {
        return simulatedHour;
    }

    // ✅ NEW METHOD: Adjusts simulation speed dynamically
    public static void setSimulationSpeed(double speedFactor) {
        realSecondsPerSimHour = 1.0 / speedFactor; // Faster speeds = smaller duration
        restartSimulation(); // Restart timeline with new speed
    }
}
