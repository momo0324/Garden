package model.plants;

import java.util.Arrays;

public class Tomato extends Plant {
   public Tomato() {
      super("Tomato", 500, 1000, 8, 6, 18, 27, 6, 
            Arrays.asList("Aphid", "SpiderMite", "Caterpillar"),
            "/images/plants/tomato.png",
            "/images/plants/tomato_mature.png");
    }
}
