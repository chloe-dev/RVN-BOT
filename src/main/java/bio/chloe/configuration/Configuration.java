package bio.chloe.configuration;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

// TODO: Documentation (though, this one's fairly readable).

public class Configuration {
    private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);

    private static volatile Configuration instance;
    private final JSONObject configurationData;

    private Configuration(File jsonConfigurationFile) throws IOException {
        Objects.requireNonNull(jsonConfigurationFile, "JSON configuration file cannot be null.");

        if (!jsonConfigurationFile.exists()) {
            LOGGER.error("JSON configuration file does not exist at {}.", jsonConfigurationFile.getAbsolutePath());

            throw new IOException("JSON Configuration file does not exist at " + jsonConfigurationFile.getAbsolutePath() + ".");
        }

        try {
            String jsonContent = new String(Files.readAllBytes(jsonConfigurationFile.toPath()));

            this.configurationData = new JSONObject(jsonContent);

            LOGGER.info("Successfully loaded configuration from {}.", jsonConfigurationFile.getAbsolutePath());
        } catch (Exception generalException) { // TODO: Catch and handle individual exceptions.
            LOGGER.error("Failed to load the configuration file: {}.", generalException.getMessage());

            throw new IOException("Failed to load the configuration file: " + generalException.getMessage() + ".", generalException);
        }
    }

    /* TODO: Combine initializeConfiguration and getInstance in a manner that's less annoying.
     * Ideally, once the configuration is initialized, there should be no need to call the
     * initializeConfiguration function (in fact, it would do nothing), however, the fact
     * that it's still call-able is an issue in itself since it's a one time use function.
     */

    public static void initializeConfiguration(File jsonConfigurationFile) throws IOException {
        if (instance == null) {
            instance = new Configuration(jsonConfigurationFile);
        }
    }

    public static Configuration getInstance() {
        return instance;
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