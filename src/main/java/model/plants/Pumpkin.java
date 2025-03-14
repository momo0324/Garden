package model.plants;

import java.util.Arrays;
import java.util.List;

public class Pumpkin extends Plant {
    public Pumpkin() {
        super("Pumpkin", 300, 600, 10, 6, 15, 25, 6, Arrays.asList("Aphid", "Slug", "SpiderMite"),
                "/images/plants/pumpkin.png",  // Growing Image
                "/images/plants/pumpkin-mature.png");
    }

    public Pumpkin(String name, int minWater, int maxWater, int growthTime, int sunlight,
                   int minTemp, int maxTemp, int survivalTime, List<String> pests,
                   String imagePath, String matureImagePath) {
        super(name, minWater, maxWater, growthTime, sunlight, minTemp, maxTemp, survivalTime, pests, imagePath, matureImagePath);
    }
}
