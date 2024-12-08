package bio.chloe.caches.objects;

import java.util.Collections;
import java.util.List;

public class GuildConfiguration {
    /////////////////////
    // — INFORMATION — //
    /////////////////////

    private final long GUILD_ID;

    ////////////////
    // — EVENTS — //
    ////////////////

    private boolean logGuildChannelEvents; // ChannelCreateEvent, ChannelDeleteEvent
    private boolean logGuildExpressionEvents; // EmojiAddedEvent, EmojiRemovedEvent, GuildStickerAddedEvent, GuildStickerRemovedEvent
    private boolean logGuildMemberEvents; // GuildMemberJoinEvent, GuildMemberRemoveEvent, GuildMemberRoleAddEvent, GuildMemberRoleRemoveEvent, GuildMemberUpdateNicknameEvent
    private boolean logGuildMessageEvents; // MessageDeleteEvent, MessageUpdateEvent, MessageBulkDeleteEvent
    private boolean logGuildModerationEvents; // GuildBanEvent, GuildUnbanEvent, GuildMemberRemoveEvent, GuildVoiceGuildDeafenEvent, GuildVoiceGuildMuteEvent, GuildMemberUpdateTimeOutEvent
    private boolean logGuildRoleEvents; // RoleCreateEvent, RoleDeleteEvent

    //////////////////
    // — CHANNELS — //
    //////////////////

    private long logChannelId; // Channel in which all logXEvents are logged within.

    private List<Long> beaconChannelIds; // Channels in which beacons may be posted.

    private List<Long> voipCategoryIds; // Categories in which VOIP channels may be created.

    ///////////////////////
    // — CACHE PURGING — //
    ///////////////////////

    private long lastAccessed; // Last time this GuildConfiguration was accessed.

    /////////////////////
    // — CONSTRUCTOR — //
    /////////////////////

    public GuildConfiguration(long guildId) {
        /////////////////////
        // — INFORMATION — //
        /////////////////////

        this.GUILD_ID = guildId;

        ////////////////
        // — EVENTS — //
        ////////////////

        this.logGuildChannelEvents = false;
        this.logGuildExpressionEvents = false;
        this.logGuildMemberEvents = false;
        this.logGuildMessageEvents = false;
        this.logGuildModerationEvents = false;
        this.logGuildRoleEvents = false;

        //////////////////
        // — CHANNELS — //
        //////////////////

        this.logChannelId = 0L;

        this.beaconChannelIds = Collections.emptyList();

        this.voipCategoryIds = Collections.emptyList();

        ///////////////////////
        // — CACHE PURGING — //
        ///////////////////////

        this.lastAccessed = System.currentTimeMillis();
    }

    /////////////////////
    // — INFORMATION — //
    /////////////////////

    // Accessor (GUILD_ID).
    public long getGuildId() {
        return GUILD_ID;
    }

    ////////////////
    // — EVENTS — //
    ////////////////

    // Accessor (logGuildChannelEvents).
    public boolean getLogGuildChannelEvents() {
        return logGuildChannelEvents;
    }

    // Mutator (logGuildChannelEvents).
    public void setLogGuildChannelEvents(boolean logGuildChannelEvents) {
        this.logGuildChannelEvents = logGuildChannelEvents;
    }

    // Accessor (logGuildExpressionEvents).
    public boolean getLogGuildExpressionEvents() {
        return logGuildExpressionEvents;
    }

    // Mutator (logGuildExpressionEvents).
    public void setLogGuildExpressionEvents(boolean logGuildExpressionEvents) {
        this.logGuildExpressionEvents = logGuildExpressionEvents;
    }

    // Accessor (logGuildMemberEvents).
    public boolean getLogGuildMemberEvents() {
        return logGuildMemberEvents;
    }

    // Mutator (logGuildMemberEvents).
    public void setLogGuildMemberEvents(boolean logGuildMemberEvents) {
        this.logGuildMemberEvents = logGuildMemberEvents;
    }

    // Accessor (logGuildMessageEvents).
    public boolean getLogGuildMessageEvents() {
        return logGuildMessageEvents;
    }

    // Mutator (logGuildChannelEvents).
    public void setLogGuildMessageEvents(boolean logGuildMessageEvents) {
        this.logGuildMessageEvents = logGuildMessageEvents;
    }

    // Accessor (logGuildModerationEvents).
    public boolean getLogGuildModerationEvents() {
        return logGuildModerationEvents;
    }

    // Mutator (logGuildModerationEvents).
    public void setLogGuildModerationEvents(boolean logGuildModerationEvents) {
        this.logGuildModerationEvents = logGuildModerationEvents;
    }

    // Accessor (logGuildRoleEvents).
    public boolean getLogGuildRoleEvents() {
        return logGuildRoleEvents;
    }

    // Mutator (logGuildRoleEvents).
    public void setLogGuildRoleEvents(boolean logGuildRoleEvents) {
        this.logGuildRoleEvents = logGuildRoleEvents;
    }

    //////////////////
    // — CHANNELS — //
    //////////////////

    // Accessor (logChannelId).
    public long getLogChannelId() {
        return logChannelId;
    }

    // Mutator (logChannelId).
    public void setLogChannelId(long logChannelId) {
        this.logChannelId = logChannelId;
    }

    // Accessor (beaconChannelIds).
    public List<Long> getBeaconChannelIds() {
        return beaconChannelIds;
    }

    // Mutator (beaconChannelIds).
    public void setBeaconChannelIds(List<Long> beaconChannelIds) {
        this.beaconChannelIds = beaconChannelIds;
    }

    // Accessor (voipCategoryIds).
    public List<Long> getVoipCategoryIds() {
        return voipCategoryIds;
    }

    // Mutator (voipCategoryIds).
    public void setVoipCategoryIds(List<Long> voipCategoryIds) {
        this.voipCategoryIds = voipCategoryIds;
    }

    ///////////////////////
    // — CACHE PURGING — //
    ///////////////////////

    // Accessor (lastAccessed).
    public long getLastAccessed() {
        return lastAccessed;
    }

    // Mutator (lastAccessed).
    public void updateLastAccessed() {
        lastAccessed = System.currentTimeMillis();
    }
}
