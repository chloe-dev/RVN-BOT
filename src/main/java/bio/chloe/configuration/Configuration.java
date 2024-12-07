package bio.chloe.configuration;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.function.Supplier;

public class Configuration {
    // Lazily load the SLF4J logger to prevent unnecessary memory allocation for an unused logger.
    private static final Supplier<Logger> LOGGER = () -> LoggerFactory.getLogger(Configuration.class);

    private static volatile Configuration configurationInstance;
    private final JSONObject configurationData;

    private Configuration(JSONObject configurationData) {
        this.configurationData = configurationData;
    }

    public static Configuration initializeConfiguration(File jsonConfigurationFile) {
        Objects.requireNonNull(jsonConfigurationFile, "JSON configuration file cannot be null.");

        if (configurationInstance == null) {
            synchronized (Configuration.class) {
                if (configurationInstance == null) {
                    if (!jsonConfigurationFile.exists()) {
                        LOGGER.get().error("JSON configuration file does not exist at {}.", jsonConfigurationFile.getAbsolutePath()); return configurationInstance;
                    }

                    if (!jsonConfigurationFile.canRead()) {
                        LOGGER.get().error("JSON configuration file at {} cannot be read.", jsonConfigurationFile.getAbsolutePath()); return configurationInstance;
                    }

                    try {
                        // Create a new String object containing the JSON data, constructing a JSONObject and passing that to the Configuration constructor.
                        configurationInstance = new Configuration(new JSONObject(new String(Files.readAllBytes(jsonConfigurationFile.toPath()))));
                    } catch (IOException ioException) {
                        LOGGER.get().error("IOException occurred whilst reading from {}.", jsonConfigurationFile.getAbsolutePath());
                    }
                }
            }
        }

        return configurationInstance;
    }

    public static Configuration getInstance() {
        if (configurationInstance == null) {
            LOGGER.get().error("Configuration.getInstance() called before instance initialization.");
            throw new IllegalStateException("Configuration instance is not initialized.");
        }

        return configurationInstance;
    }

    public String optString(String key, String defaultValue) {
        return configurationData.optString(key, defaultValue);
    }

    public int optInt(String key, int defaultValue) {
        return configurationData.optInt(key, defaultValue);
    }

    public float optFloat(String key, float defaultValue) {
        return configurationData.optFloat(key, defaultValue);
    }

    public double optDouble(String key, double defaultValue) {
        return configurationData.optDouble(key, defaultValue);
    }

    public boolean optBoolean(String key, boolean defaultValue) {
        return configurationData.optBoolean(key, defaultValue);
    }

    public JSONObject optJsonObject(String key, JSONObject defaultValue) {
        return configurationData.optJSONObject(key, defaultValue);
    }

    public JSONArray optJsonArray(String key, JSONArray defaultValue) {
        return configurationData.optJSONArray(key, defaultValue);
    }

    public boolean has(String key) {
        return configurationData.has(key);
    }

    public boolean isNull(String key) {
        return configurationData.isNull(key);
    }
}
