package model.plants;

import java.util.Arrays;

public class Orchid extends Plant {
    public Orchid() {
        super("Orchid", 50, 150, 18, 4, 18, 30, 10, 
              Arrays.asList("mealybugs", "spider mites", "aphids"),
              "/images/plants/orchid.png",
              "/images/plants/orchid_mature.png");
    }
}
