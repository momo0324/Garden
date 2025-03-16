package model.plants;

import java.util.Arrays;
import java.util.List;

public class Eggplant extends Plant {
    public Eggplant() {
        super("Eggplant",
                5,
                20,
                72, // 3天（72小时）后成熟
                4,
                18,
                32,
                12,
                Arrays.asList("Mealybug", "SpiderMite"),
                "/images/plants/eggplant.png",  // Growing Image
                "/images/plants/eggplant-mature.png");  // 注意这里使用连字符而不是下划线
    }

    @Override
    public void grow(int hours, int sunlightHours) {
        if (sunlightHours < sunlightNeeded) {
            java.lang.System.out.println(name + " is not getting enough sunlight.");
            return;
        }

        currentGrowthHours += hours;
        
        // 设置一个合理的成熟时间，而不是Integer.MAX_VALUE
        if (currentGrowthHours >= 72) { // 3天（72小时）后成熟
            isFullyGrown = true;
            java.lang.System.out.println(name + " has fully grown.");
        } else {
            java.lang.System.out.println(name + " is growing. Current hours: " + currentGrowthHours + "/72");
        }
    }

    public Eggplant(String name, int minWater, int maxWater, int growthTime, int sunlight,
                   int minTemp, int maxTemp, int survivalTime, List<String> pests,
                   String imagePath, String matureImagePath) {
        super(name, minWater, maxWater, growthTime, sunlight, minTemp, maxTemp, survivalTime, pests, imagePath, matureImagePath);
    }
}
