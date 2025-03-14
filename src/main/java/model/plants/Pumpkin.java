package model.plants;

import java.util.Arrays;
import java.util.List;

public class Pumpkin extends Plant {
    public Pumpkin() {
        super("Pumpkin", 1000, 2000, 14, 6, 15, 30, 10, 
              Arrays.asList("aphids", "squash bugs", "powdery mildew"),
              "/images/plants/pumpkin.png",
              "/images/plants/pumpkin_mature.png");
    }

    public Pumpkin(String name, int minWater, int maxWater, int growthTime, int sunlight,
                   int minTemp, int maxTemp, int survivalTime, List<String> pests,
                   String imagePath, String matureImagePath) {
        super(name, minWater, maxWater, growthTime, sunlight, minTemp, maxTemp, survivalTime, pests, imagePath, matureImagePath);
    }
}
