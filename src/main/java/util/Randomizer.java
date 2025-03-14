package util;

import java.util.List;
import java.util.Random;

public class Randomizer {
    private static final Random RANDOM = new Random();

    public static int getRandomInt(int min, int max) {
        return RANDOM.nextInt((max - min) + 1) + min;
    }

    public static double getRandomDouble(double min, double max) {
        return min + (max - min) * RANDOM.nextDouble();
    }

    public static <T> T getRandomItem(List<T> list) {
        return list.isEmpty() ? null : list.get(RANDOM.nextInt(list.size()));
    }
}
