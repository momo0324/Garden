package model.plants;

import java.util.Arrays;
import java.util.List;

public class Carrot extends Plant {
    public Carrot() {
        super("Carrot", 400, 700, 7, 6, 10, 25, 6,
                Arrays.asList("aphids", "carrot rust fly"),
                "/images/plants/carrot.png",  // Growing Image
                "/images/plants/carrot-mature.png" // 使用连字符而不是下划线
        );

    }

    public Carrot(String name, int minWater, int maxWater, int growthTime, int sunlight,
                   int minTemp, int maxTemp, int survivalTime, List<String> pests,
                   String imagePath, String matureImagePath) {
        super(name, minWater, maxWater, growthTime, sunlight, minTemp, maxTemp, survivalTime, pests, imagePath, matureImagePath);
    }
}
