package util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileReader;
import java.io.IOException;

public class ConfigLoader {
    private static final String CONFIG_FILE = "resources/config.json";

    public static JsonObject loadConfig() {
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            return new Gson().fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            System.out.println("Error loading config file: " + e.getMessage());
            return null;
        }
    }

    public static int getIntConfig(JsonObject config, String key, int defaultValue) {
        return config != null && config.has(key) ? config.get(key).getAsInt() : defaultValue;
    }

    public static String getStringConfig(JsonObject config, String key, String defaultValue) {
        return config != null && config.has(key) ? config.get(key).getAsString() : defaultValue;
    }
}
