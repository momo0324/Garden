package model.plants;

import java.util.Arrays;
import java.util.List;

public class Lettuce extends Plant {
    public Lettuce() {
        super("Lettuce", 
        300, 
        500, 
        6, 
        4, 
        10, 
        18, 4,
              Arrays.asList("aphids", "slugs", "caterpillars"),
              "/images/plants/lettuce.png",
              "/images/plants/lettuce-mature.png");
    }
    // ✅ New constructor that matches the expected parameters
    public Lettuce(String name, int minWater, int maxWater, int growthTime, int sunlight,
                   int minTemp, int maxTemp, int survivalTime, List<String> pests,
                   String imagePath, String matureImagePath) {
        super(name, minWater, maxWater, growthTime, sunlight, minTemp, maxTemp, survivalTime, pests, imagePath, matureImagePath);
    }
}
