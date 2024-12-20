package bio.chloe.caches.objects;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Provide a thread-safe caching object containing all relevant data related to Guild configurations.
 */

public class GuildConfiguration {
    private final long GUILD_ID;

    private final boolean LOG_GUILD_CHANNEL_EVENTS;    // ChannelCreateEvent, ChannelDeleteEvent
    private final boolean LOG_GUILD_EXPRESSION_EVENTS; // EmojiAddedEvent, EmojiRemovedEvent, GuildStickerAddedEvent, GuildStickerRemovedEvent
    private final boolean LOG_GUILD_MEMBER_EVENTS;     // GuildMemberJoinEvent, GuildMemberRemoveEvent, GuildMemberRoleAddEvent, GuildMemberRoleRemoveEvent, GuildMemberUpdateNicknameEvent
    private final boolean LOG_GUILD_MESSAGE_EVENTS;    // MessageDeleteEvent, MessageUpdateEvent, MessageBulkDeleteEvent
    private final boolean LOG_GUILD_MODERATION_EVENTS; // GuildBanEvent, GuildUnbanEvent, GuildMemberRemoveEvent, GuildVoiceGuildDeafenEvent, GuildVoiceGuildMuteEvent, GuildMemberUpdateTimeOutEvent
    private final boolean LOG_GUILD_ROLE_EVENTS;       // RoleCreateEvent, RoleDeleteEvent

    private final long LOG_CHANNEL_ID;

    private final List<Long> BEACON_CHANNEL_IDS;
    private final List<Long> VOIP_CATEGORY_IDS;

    private final AtomicLong lastAccessed = new AtomicLong(System.currentTimeMillis());

    private GuildConfiguration(Builder guildConfigurationBuilder) {
        this.GUILD_ID = guildConfigurationBuilder.GUILD_ID;

        this.LOG_GUILD_CHANNEL_EVENTS = guildConfigurationBuilder.logGuildChannelEvents;
        this.LOG_GUILD_EXPRESSION_EVENTS = guildConfigurationBuilder.logGuildExpressionEvents;
        this.LOG_GUILD_MEMBER_EVENTS = guildConfigurationBuilder.logGuildMemberEvents;
        this.LOG_GUILD_MESSAGE_EVENTS = guildConfigurationBuilder.logGuildMessageEvents;
        this.LOG_GUILD_MODERATION_EVENTS = guildConfigurationBuilder.logGuildModerationEvents;
        this.LOG_GUILD_ROLE_EVENTS = guildConfigurationBuilder.logGuildRoleEvents;

        this.LOG_CHANNEL_ID = guildConfigurationBuilder.logChannelId;

        this.BEACON_CHANNEL_IDS = guildConfigurationBuilder.beaconChannelIds;
        this.VOIP_CATEGORY_IDS = guildConfigurationBuilder.voipCategoryIds;
    }

    public long getGuildId() { updateLastAccessed(); return this.GUILD_ID; }

    public boolean getLogGuildChannelEvents() { updateLastAccessed(); return this.LOG_GUILD_CHANNEL_EVENTS; }
    public boolean getLogGuildExpressionEvents() { updateLastAccessed(); return this.LOG_GUILD_EXPRESSION_EVENTS; }
    public boolean getLogGuildMemberEvents() { updateLastAccessed(); return this.LOG_GUILD_MEMBER_EVENTS; }
    public boolean getLogGuildMessageEvents() { updateLastAccessed(); return this.LOG_GUILD_MESSAGE_EVENTS; }
    public boolean getLogGuildModerationEvents() { updateLastAccessed(); return this.LOG_GUILD_MODERATION_EVENTS; }
    public boolean getLogGuildRoleEvents() { updateLastAccessed(); return this.LOG_GUILD_ROLE_EVENTS; }

    public long getLogChannelId() { updateLastAccessed(); return this.LOG_CHANNEL_ID; }

    public List<Long> getBeaconChannelIds() { updateLastAccessed(); return this.BEACON_CHANNEL_IDS; }
    public List<Long> getVoipCategoryIds() { updateLastAccessed(); return this.VOIP_CATEGORY_IDS; }

    public long getLastAccessed() {
        return lastAccessed.get();
    }

    private void updateLastAccessed() {
        lastAccessed.set(System.currentTimeMillis());
    }

    public static class Builder {
        private final long GUILD_ID;

        private boolean logGuildChannelEvents = false;
        private boolean logGuildExpressionEvents = false;
        private boolean logGuildMemberEvents = false;
        private boolean logGuildMessageEvents = false;
        private boolean logGuildModerationEvents = false;
        private boolean logGuildRoleEvents = false;

        private long logChannelId = 0L;

        private List<Long> beaconChannelIds = Collections.emptyList();
        private List<Long> voipCategoryIds = Collections.emptyList();

        public Builder(long guildId) {
            this.GUILD_ID = guildId;
        }

        public static Builder from(GuildConfiguration originalGuildConfiguration) {
            return new Builder(originalGuildConfiguration.GUILD_ID)
                    .setLogGuildChannelEvents(originalGuildConfiguration.LOG_GUILD_CHANNEL_EVENTS)
                    .setLogGuildExpressionEvents(originalGuildConfiguration.LOG_GUILD_EXPRESSION_EVENTS)
                    .setLogGuildMemberEvents(originalGuildConfiguration.LOG_GUILD_MEMBER_EVENTS)
                    .setLogGuildMessageEvents(originalGuildConfiguration.LOG_GUILD_MESSAGE_EVENTS)
                    .setLogGuildModerationEvents(originalGuildConfiguration.LOG_GUILD_MODERATION_EVENTS)
                    .setLogGuildRoleEvents(originalGuildConfiguration.LOG_GUILD_ROLE_EVENTS)
                    .setLogChannelId(originalGuildConfiguration.LOG_CHANNEL_ID)
                    .setBeaconChannelIds(originalGuildConfiguration.BEACON_CHANNEL_IDS)
                    .setVoipCategoryIds(originalGuildConfiguration.VOIP_CATEGORY_IDS);
        }

        public Builder setLogGuildChannelEvents(boolean logGuildChannelEvents) {
            this.logGuildChannelEvents = logGuildChannelEvents; return this;
        }

        public Builder setLogGuildExpressionEvents(boolean logGuildExpressionEvents) {
            this.logGuildExpressionEvents = logGuildExpressionEvents; return this;
        }

        public Builder setLogGuildMemberEvents(boolean logGuildMemberEvents) {
            this.logGuildMemberEvents = logGuildMemberEvents; return this;
        }

        public Builder setLogGuildMessageEvents(boolean logGuildMessageEvents) {
            this.logGuildMessageEvents = logGuildMessageEvents; return this;
        }

        public Builder setLogGuildModerationEvents(boolean logGuildModerationEvents) {
            this.logGuildModerationEvents = logGuildModerationEvents; return this;
        }

        public Builder setLogGuildRoleEvents(boolean logGuildRoleEvents) {
            this.logGuildRoleEvents = logGuildRoleEvents; return this;
        }

        public Builder setLogChannelId(long logChannelId) {
            this.logChannelId = logChannelId; return this;
        }

        public Builder setBeaconChannelIds(List<Long> beaconChannelIds) {
            this.beaconChannelIds = beaconChannelIds; return this;
        }

        public Builder setVoipCategoryIds(List<Long> voipCategoryIds) {
            this.voipCategoryIds = voipCategoryIds; return this;
        }

        public GuildConfiguration build() {
            return new GuildConfiguration(this);
        }
    }
}
