package bio.chloe.handlers;

import bio.chloe.caches.GuildConfigurationCache;
import bio.chloe.caches.GuildMessageCache;
import bio.chloe.caches.objects.GuildConfiguration;
import bio.chloe.caches.objects.GuildMessage;
import bio.chloe.managers.DatabaseManager;
import bio.chloe.utility.Embeds;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * This is a <b>large</b> class that handles <b>all</b> events related to Guilds and their configurations.
 */

public class GuildEventHandler extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuildEventHandler.class);

    private static final DatabaseManager DATABASE_MANAGER = DatabaseManager.getInstance();

    private static final GuildConfigurationCache GUILD_CONFIGURATION_CACHE = GuildConfigurationCache.getInstance();

    @Override
    public void onGuildJoin(GuildJoinEvent guildJoinEvent) {
        if (DATABASE_MANAGER.createDefaultGuildConfiguration(guildJoinEvent.getGuild().getIdLong())) {
            // Handle.
        } else {
            // Handle.
        }

        List<TextChannel> guildChannelList = guildJoinEvent.getGuild().getTextChannels();

        if (!guildChannelList.isEmpty()) {
            TextChannel textChannel = guildChannelList.get(0);

            textChannel.sendMessageEmbeds(Embeds.infoEmbed(String.format(
                    "Thanks for inviting **%s**, use `/config` to begin configuration!",
                    guildJoinEvent.getGuild().getSelfMember().getAsMention())
            )).queue();

            LOGGER.info("Joined Guild {} and successfully sent a welcome message.", guildJoinEvent.getGuild().getId());
        } else {
            LOGGER.warn("Joined Guild {}, however, no welcome message was sent due to a lack of valid TextChannels.", guildJoinEvent.getGuild().getId());
        }
    }

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent guildLeaveEvent) {
        DATABASE_MANAGER.deleteGuildConfiguration(guildLeaveEvent.getGuild().getIdLong());
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent messageReceivedEvent) {
        if (messageReceivedEvent.getAuthor().isBot()) return;

        if (messageReceivedEvent.isFromGuild()) {
            // GuildConfiguration guildConfiguration = GUILD_CONFIGURATION_CACHE.getGuildConfiguration(messageReceivedEvent.getGuild().getIdLong());

            if (/* guildConfiguration.getLogMessageEvents() */ true) { // TODO: Set temporarily for hard-coded requirements.
                GuildMessage guildMessage = new GuildMessage(
                        messageReceivedEvent.getGuild().getIdLong(),
                        messageReceivedEvent.getMessageIdLong(),
                        messageReceivedEvent.getMember(),
                        messageReceivedEvent.getMessage().getContentRaw()
                );

                GuildMessageCache guildMessageCache = GuildMessageCache.getInstance();

                guildMessageCache.updateGuildMessage(messageReceivedEvent.getMessageIdLong(), guildMessage);
            }
        }
    }

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent messageDeleteEvent) {
        // GuildConfiguration guildConfiguration = GUILD_CONFIGURATION_CACHE.getGuildConfiguration(messageDeleteEvent.getGuild().getIdLong());

        if (/* guildConfiguration.getLogMessageEvents() */ true) { // TODO: Set temporarily for hard-coded requirements.
            GuildMessageCache guildMessageCache = GuildMessageCache.getInstance();

            GuildMessage guildMessage = guildMessageCache.getGuildMessage(messageDeleteEvent.getMessageIdLong());

            if (guildMessage != null) {
                GuildChannel guildChannel = messageDeleteEvent.getGuild().getGuildChannelById(1315265738835230750L /* guildConfiguration.getLogChannelId() */);

                if (guildChannel instanceof MessageChannel messageChannel) {
                    MessageEmbed deletedMessageEmbed = new EmbedBuilder()
                            .setTitle("A message was deleted!")
                            .setDescription(guildMessage.getMessageContent())
                            .setFooter(
                                    "Authored by " + guildMessage.getMessageAuthor().getEffectiveName(),
                                    guildMessage.getMessageAuthor().getEffectiveAvatarUrl()
                            ).setTimestamp(OffsetDateTime.now())
                            .setColor(Color.decode("#BF3F3F"))
                            .build();

                    messageChannel.sendMessageEmbeds(deletedMessageEmbed).queue();
                } else {
                    LOGGER.warn("Message {} in Guild {} was deleted, though no suitable log channel was found", guildMessage.getMessageId(), messageDeleteEvent.getGuild().getIdLong());
                }

                guildMessageCache.deleteGuildMessage(guildMessage.getMessageId());
            }
        }
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent messageUpdateEvent) {
        if (messageUpdateEvent.getAuthor().isBot()) return;

        // GuildConfiguration guildConfiguration = GUILD_CONFIGURATION_CACHE.getGuildConfiguration(messageUpdateEvent.getGuild().getIdLong());

        if (/* guildConfiguration.getLogMessageEvents() */ true) { // TODO: Set temporarily for hard-coded requirements.
            GuildMessageCache guildMessageCache = GuildMessageCache.getInstance();

            GuildMessage guildMessage = guildMessageCache.getGuildMessage(messageUpdateEvent.getMessageIdLong());

            if (guildMessage != null) {
                GuildChannel guildChannel = messageUpdateEvent.getGuild().getGuildChannelById(1315265738835230750L /* guildConfiguration.getLogChannelId() */);

                if (guildChannel instanceof MessageChannel messageChannel) {
                    String messageUrl = String.format(
                            "https://discord.com/channels/%s/%s/%s",
                            messageUpdateEvent.getGuild().getId(),
                            messageUpdateEvent.getChannel().getId(),
                            guildMessage.getMessageId()
                    );

                    MessageEmbed deletedMessageEmbed = new EmbedBuilder()
                            .setTitle("A message was edited!")
                            .setUrl(messageUrl)
                            .setDescription(guildMessage.getMessageContent())
                            .setFooter(
                                    "Authored by " + guildMessage.getMessageAuthor().getEffectiveName(),
                                    guildMessage.getMessageAuthor().getEffectiveAvatarUrl()
                            ).setTimestamp(OffsetDateTime.now())
                            .setColor(Color.decode("#3F3FBF"))
                            .build();

                    messageChannel.sendMessageEmbeds(deletedMessageEmbed).queue();
                } else {
                    LOGGER.warn("Message {} in Guild {} was deleted, though no suitable log channel was found", guildMessage.getMessageId(), messageUpdateEvent.getGuild().getIdLong());
                }

                guildMessage.setMessageContent(messageUpdateEvent.getMessage().getContentRaw());
            }
        }
    }
}
