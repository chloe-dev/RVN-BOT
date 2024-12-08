package bio.chloe.caches;

import bio.chloe.caches.objects.GuildConfiguration;
import bio.chloe.managers.DatabaseManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GuildConfigurationCache {
    private final long PURGE_PERIOD = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24);

    private final Map<Long, GuildConfiguration> guildConfigurationMap;

    private static volatile GuildConfigurationCache instance;

    private GuildConfigurationCache() {
        this.guildConfigurationMap = new ConcurrentHashMap<>();

        initializeScheduledExecutorService();

        instance = this;
    }

    /**
     * Returns an instance of GuildConfigurationCache. Note that dispatched slash command handlers invoking this method
     * <b>must</b> use SlashCommandInteractionEvent#deferReply, lest they risk not responding to Discord within the
     * allotted three seconds if the GuildConfigurationCache has not yet been initialized.
     *
     * @return an instance of GuildConfigurationCache.
     */

    public static GuildConfigurationCache getInstance() {
        if (instance == null) {
            instance = new GuildConfigurationCache();
        }

        return instance;
    }

    /**
     * Returns a GuildConfiguration object immediately if one is found in the guildConfigurationMap. If not, the
     * configuration is pulled from the database and placed into the guildConfigurationMap before being returned to the
     * invoker. If the database does not contain a GuildConfiguration for the specified guild identifier, one will be
     * generated using the default settings, written to the database, placed into the guildConfigurationMap, and
     * returned to the invoker.
     * <p>
     * As with GuildConfigurationCache#getInstance, dispatched slash command handlers invoking this method <b>must</b>
     * use SlashCommandInteractionEvent#deferReply, lest they risk not responding to Discord within the allotted three
     * seconds if database interactions must be performed.
     * </p>
     *
     * @param guildId a Discord guild identifier as a long.
     * @return a GuildConfiguration object.
     */

    public GuildConfiguration getGuildConfiguration(long guildId) {
        if (guildConfigurationMap.containsKey(guildId)) {
            GuildConfiguration guildConfiguration = guildConfigurationMap.get(guildId);

            guildConfiguration.updateLastAccessed();

            return guildConfiguration;
        } else {
            // Get the configuration from the database (if it's fallen out of cache).

            // If there's nothing in the database, create the default configuration.
            DatabaseManager databaseManager = DatabaseManager.getInstance();

            databaseManager.createDefaultGuildConfiguration(guildId);

            // Get the configuration from the database.

            // Return the guild configuration.

            return null; // TODO: Get the configuration from the database (if it's fallen out of cache).
                                     // TODO: If there's nothing in the database, create the default configuration
                                     // TODO: for that specific guild, store it in the database, cache it, and
                                     // TODO: return the cached GuildConfiguration from the guildConfigurationMap.
        }
    }

    public void updateGuildConfiguration(long guildId, GuildConfiguration guildConfiguration) {
        if (guildConfigurationMap.containsKey(guildId)) {
           guildConfigurationMap.replace(guildId, guildConfiguration);
        } else {
            guildConfigurationMap.put(guildId, guildConfiguration);
        }
    }

    private void initializeScheduledExecutorService() {
        final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.scheduleAtFixedRate(this::purgeCache, 12, 12, TimeUnit.HOURS);
    }

    private void purgeCache() {
        guildConfigurationMap.entrySet().removeIf(guildConfiguration ->
            guildConfiguration.getValue().getLastAccessed() < PURGE_PERIOD
        );
    }
}
