package bio.chloe.managers;

import bio.chloe.caches.objects.GuildConfiguration;
import bio.chloe.configuration.Configuration;
import org.intellij.lang.annotations.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class GuildConfigurationManager {
    // Lazily load the SLF4J logger to prevent unnecessary memory allocation for an unused logger.
    private static final Supplier<Logger> LOGGER = () -> LoggerFactory.getLogger(Configuration.class);

    private static final DatabaseManager DATABASE_MANAGER = DatabaseManager.getInstance();

    public static boolean createDefaultGuildConfiguration(long guildId) {
        @Language("SQL")
        String sqlStatement =
                "CREATE TABLE guild_configurations (" +
                        "guild_id BIGINT PRIMARY KEY," +
                        "log_guild_channel_events BOOLEAN NOT NULL DEFAULT FALSE" +
                        ")"; // TODO: Finish this.

        return false;
    }

    public static GuildConfiguration getGuildConfiguration(long guildId) {
        // Get the guild configuration data from the database.
        // Use the GuildConfiguration#Builder to build a GuildConfiguration using this data.
        // Return the GuildConfiguration.

        return null;
    }

    public static boolean updateGuildConfiguration(GuildConfiguration guildConfiguration) {
        // Use the GuildConfiguration's data to set the database values.
        // If successful, return true.

        return false;
    }

    public static boolean deleteGuildConfiguration(long guildId) {
        // Delete the guild configuration associated with a certain guild ID.

        return false;
    }
}
