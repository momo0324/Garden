package model.plants;

import java.util.Arrays;
import java.util.List;

public class Watermelon extends Plant {
    public Watermelon() {
        super("Watermelon", 50, 150, 18, 4, 18, 30, 10,
              Arrays.asList("mealybugs", "spider mites", "aphids"),
              "/images/plants/watermelon.png",
              "/images/plants/watermelon-mature.png");
    }
    public Watermelon(String name, int minWater, int maxWater, int growthTime, int sunlight,
                  int minTemp, int maxTemp, int survivalTime, List<String> pests,
                  String imagePath, String matureImagePath) {
        super(name, minWater, maxWater, growthTime, sunlight, minTemp, maxTemp, survivalTime, pests, imagePath, matureImagePath);
    }
}
