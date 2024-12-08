package bio.chloe.caches.objects;

import java.util.List;

public class GuildConfiguration {
    private final long guildId;

    // Events.
    private boolean logGuildMemberJoinEvents;
    private boolean logGuildMemberRemoveEvents;
    private boolean logMessageEvents;
    private boolean logAutoModEvents;

    private List<Long> beaconChannelIdList;

    private long logChannelId;

    private long lastAccessed;

    public GuildConfiguration(long guildId) {
        this.guildId = guildId;

        // Events.
        logGuildMemberJoinEvents = false;
        logGuildMemberRemoveEvents = false;

        // TODO: holy fuck this'll take a while 💀

    }

    public long getGuildId() {
        return guildId;
    }

    ////////////
    // EVENTS //
    ////////////

    public boolean getLogGuildMemberJoinEvents() {
        return logGuildMemberJoinEvents;
    }

    public boolean getLogGuildMemberRemoveEvents() {
        return logGuildMemberRemoveEvents;
    }

    public boolean getLogMessageEvents() {
        return logMessageEvents;
    }

    /////////////////
    // CHANNEL IDS //
    /////////////////

    public List<Long> getBeaconChannelIdList() {
        return beaconChannelIdList;
    }

    public long getLogChannelId() {
        return logChannelId;
    }

    ///////////////////
    // CACHE PURGING //
    ///////////////////

    // Accessor (lastAccessed).
    public long getLastAccessed() {
        return lastAccessed;
    }

    // Mutator (lastAccessed).
    public void updateLastAccessed() {
        lastAccessed = System.currentTimeMillis();
    }
}
