package model.plants;

import java.util.Arrays;
import java.util.List;

public class Corn extends Plant {
    public Corn() {
        super("Corn", 1500, 2500, 12, 8, 18, 30, 8, 
              Arrays.asList("aphids", "corn borers"),
              "/images/plants/corn.png",
              "/images/plants/corn-mature.png");
    }

    public Corn(String name, int minWater, int maxWater, int growthTime, int sunlight,
                   int minTemp, int maxTemp, int survivalTime, List<String> pests,
                   String imagePath, String matureImagePath) {
        super(name, minWater, maxWater, growthTime, sunlight, minTemp, maxTemp, survivalTime, pests, imagePath, matureImagePath);
    }
}
