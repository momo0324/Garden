package model.plants;

import java.util.Arrays;
import java.util.List;

public class Grape extends Plant {
    public Grape() {
        super("Grape", 700, 1000, 6, 6, 15, 25, 8,
              Arrays.asList("aphids", "spider mites", "mealybugs"),
              "/images/plants/grape.png",
              "/images/plants/grape-mature.png");
    }
    public Grape(String name, int minWater, int maxWater, int growthTime, int sunlight,
                  int minTemp, int maxTemp, int survivalTime, List<String> pests,
                  String imagePath, String matureImagePath) {
        super(name, minWater, maxWater, growthTime, sunlight, minTemp, maxTemp, survivalTime, pests, imagePath, matureImagePath);
    }
}
