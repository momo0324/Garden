package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import model.Garden;

public class SimulationController {
    private Garden garden;
    private Timeline simulationTimeline;

    @FXML
    private TextArea logArea;

    public void initialize() {
        this.garden = Garden.getInstance();
        startSimulation();
    }

    private void startSimulation() {
        simulationTimeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> runSimulationStep()));
        simulationTimeline.setCycleCount(Timeline.INDEFINITE);
        simulationTimeline.play();
    }

    private void runSimulationStep() {
        garden.applyWatering();
        garden.applyPestControl();
        garden.applyHeating();
        garden.applyLighting();
//        garden.growPlants();
        garden.harvestPlants();
        logArea.appendText("Simulation step completed.\n");
    }

    @FXML
    private void stopSimulation() {
        simulationTimeline.stop();
        logArea.appendText("Simulation stopped.\n");
    }
}
