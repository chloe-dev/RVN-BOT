package bio.chloe.caches;

import bio.chloe.caches.objects.GuildMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GuildMessageCache {
    private final long PURGE_PERIOD = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1);

    private final Map<Long, GuildMessage> guildMessageMap;

    private static volatile GuildMessageCache instance;

    private GuildMessageCache() {
        this.guildMessageMap = new ConcurrentHashMap<>();

        initializeScheduledExecutorService();
    }

    public static GuildMessageCache getInstance() {
        if (instance == null) {
            instance = new GuildMessageCache();
        }

        return instance;
    }

    public GuildMessage getGuildMessage(long messageId) {
        return guildMessageMap.getOrDefault(messageId, null);
    }

    public void updateGuildMessage(long messageId, GuildMessage guildMessage) {
        if (guildMessageMap.containsKey(messageId)) {
            guildMessageMap.replace(messageId, guildMessage);
        } else {
            guildMessageMap.put(messageId, guildMessage);
        }
    }

    public void deleteGuildMessage(long messageId) {
        guildMessageMap.remove(messageId);
    }

    private void initializeScheduledExecutorService() {
        final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.scheduleAtFixedRate(this::purgeCache, 15, 15, TimeUnit.MINUTES);
    }

    private void purgeCache() {
        guildMessageMap.entrySet().removeIf(guildConfiguration ->
                guildConfiguration.getValue().getLastAccessed() < PURGE_PERIOD
        );
    }
}
