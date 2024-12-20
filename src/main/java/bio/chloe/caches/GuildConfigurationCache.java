package bio.chloe.caches;

import bio.chloe.caches.objects.GuildConfiguration;
import bio.chloe.managers.DatabaseManager;
import bio.chloe.managers.GuildConfigurationManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GuildConfigurationCache {
    private final long PURGE_PERIOD = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1);

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
            return guildConfigurationMap.get(guildId);
        } else {
            GuildConfiguration guildConfiguration = GuildConfigurationManager.getGuildConfiguration(guildId);

            if (guildConfiguration == null) {
                if (!GuildConfigurationManager.createDefaultGuildConfiguration(guildId)) {
                    return null;
                } else {
                    guildConfiguration = GuildConfigurationManager.getGuildConfiguration(guildId);
                }
            }

            return guildConfiguration;
        }
    }

    public void updateGuildConfiguration(long guildId, GuildConfiguration guildConfiguration) {
        if (guildConfigurationMap.containsKey(guildId)) {
           guildConfigurationMap.replace(guildId, guildConfiguration);
        } else {
            guildConfigurationMap.put(guildId, guildConfiguration);
        }
    }

    public void removeGuildConfiguration(long guildId) {
        guildConfigurationMap.remove(guildId);
    }

    private void initializeScheduledExecutorService() {
        final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.scheduleAtFixedRate(this::purgeCache, 15, 15, TimeUnit.MINUTES);
    }

    private void purgeCache() {
        guildConfigurationMap.entrySet().removeIf(guildConfiguration ->
            guildConfiguration.getValue().getLastAccessed() < PURGE_PERIOD
        );
    }
}
