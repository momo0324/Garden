package model.plants;

import java.util.Arrays;
import java.util.List;

public class Tomato extends Plant {
   public Tomato() {
      super("Tomato", 500, 1000, 8, 6, 18, 27, 6, 
            Arrays.asList("Aphid", "SpiderMite", "Caterpillar"),
            "/images/plants/tomato.png",
            "/images/plants/tomato-mature.png");
    }
    public Tomato(String name, int minWater, int maxWater, int growthTime, int sunlight,
                  int minTemp, int maxTemp, int survivalTime, List<String> pests,
                  String imagePath, String matureImagePath) {
        super(name, minWater, maxWater, growthTime, sunlight, minTemp, maxTemp, survivalTime, pests, imagePath, matureImagePath);
    }
}
