package model.plants;

import java.util.Arrays;

public class Sunflower extends Plant {
    public Sunflower() {
        super("Sunflower", 700, 1500, 10, 8, 18, 30, 8, 
              Arrays.asList("aphids", "caterpillars"),
              "/images/plants/sunflower.png",
              "/images/plants/sunflower_mature.png");
    }
}