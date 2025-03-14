package model.plants;

import java.util.Arrays;
import java.util.List;

public class Eggplant extends Plant {
    public Eggplant() {
        super("Eggplant",
                5,
                20,
                Integer.MAX_VALUE,
                4,
                18,
                32,
                12,
                Arrays.asList("Mealybug", "SpiderMite"),
                "/images/plants/EggPlant.png",  // Growing Image
                "/images/plants/Eggplant-mature.png");
    }

    @Override
    public void grow(int hours, int sunlightHours) {
        // Eggplant does not fully mature within the simulation
        System.out.println(getName() + " is growing slowly but will not fully mature.");
    }

    public Eggplant(String name, int minWater, int maxWater, int growthTime, int sunlight,
                   int minTemp, int maxTemp, int survivalTime, List<String> pests,
                   String imagePath, String matureImagePath) {
        super(name, minWater, maxWater, growthTime, sunlight, minTemp, maxTemp, survivalTime, pests, imagePath, matureImagePath);
    }
}
