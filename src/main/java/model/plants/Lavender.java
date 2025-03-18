package model.plants;

import java.util.Arrays;
import java.util.List;

public class Lavender extends Plant {
    public Lavender() {
        super("Lavender", 200, 400, 12, 6, 10, 30, 7,
              Arrays.asList("aphids", "spider mites"),
              "/images/plants/lavender.png",
              "/images/plants/lavender-mature.png");
    }

    public Lavender(String name, int minWater, int maxWater, int growthTime, int sunlight,
                   int minTemp, int maxTemp, int survivalTime, List<String> pests,
                   String imagePath, String matureImagePath) {
        super(name, minWater, maxWater, growthTime, sunlight, minTemp, maxTemp, survivalTime, pests, imagePath, matureImagePath);
    }
}
