package model.plants;

import java.util.Arrays;

public class Rose extends Plant {
    public Rose() {
        super("Rose", 700, 1000, 6, 6, 15, 25, 8, 
              Arrays.asList("aphids", "spider mites", "mealybugs"),
              "/images/plants/rose.png",
              "/images/plants/rose_mature.png");
    }
}
