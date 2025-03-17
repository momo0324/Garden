package model.plants;

import java.util.Arrays;
import java.util.List;

public class Eggplant extends Plant {
    public Eggplant() {
        super("Eggplant",
                5,
                20,
                23, // 23 hour 后成熟
                4,
                18,
                32,
                12,
                Arrays.asList("Mealybug", "SpiderMite"),
                "/images/plants/eggplant.png",  // Growing Image
                "/images/plants/eggplant-mature.png");  // 注意这里使用连字符而不是下划线
    }

    public Eggplant(String name, int minWater, int maxWater, int growthTime, int sunlight,
                   int minTemp, int maxTemp, int survivalTime, List<String> pests,
                   String imagePath, String matureImagePath) {
        super(name, minWater, maxWater, growthTime, sunlight, minTemp, maxTemp, survivalTime, pests, imagePath, matureImagePath);
    }
}
