package model.plants;

import java.util.Arrays;
import java.util.List;

public class Strawberry extends Plant {
    public Strawberry() {
        super("Strawberry", 700, 1500, 10, 8, 18, 30, 8,
              Arrays.asList("aphids", "caterpillars"),
              "/images/plants/strawberry.png",
              "/images/plants/strawberry-mature.png");
    }
    public Strawberry(String name, int minWater, int maxWater, int growthTime, int sunlight,
                  int minTemp, int maxTemp, int survivalTime, List<String> pests,
                  String imagePath, String matureImagePath) {
        super(name, minWater, maxWater, growthTime, sunlight, minTemp, maxTemp, survivalTime, pests, imagePath, matureImagePath);
    }
}