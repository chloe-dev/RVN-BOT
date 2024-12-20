package bio.chloe.handlers;

import bio.chloe.caches.GuildConfigurationCache;
import bio.chloe.caches.GuildMessageCache;
import bio.chloe.caches.objects.GuildMessage;
import bio.chloe.managers.GuildConfigurationManager;
import bio.chloe.utility.Embeds;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
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

    private static final GuildConfigurationCache GUILD_CONFIGURATION_CACHE = GuildConfigurationCache.getInstance();

    @Override
    public void onGuildJoin(GuildJoinEvent guildJoinEvent) {
        if (GuildConfigurationManager.createDefaultGuildConfiguration(guildJoinEvent.getGuild().getIdLong())) {
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
        GuildConfigurationManager.deleteGuildConfiguration(guildLeaveEvent.getGuild().getIdLong());

        GUILD_CONFIGURATION_CACHE.removeGuildConfiguration(guildLeaveEvent.getGuild().getIdLong());
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent messageReceivedEvent) {
        if (!messageReceivedEvent.isFromGuild()) return;
        if (messageReceivedEvent.getAuthor().isBot()) return;

        // TODO: Check if the Guild the MessageReceivedEvent occurred in chooses to log message edits and deletions.
        // GuildConfiguration guildConfiguration = GUILD_CONFIGURATION_CACHE.getGuildConfiguration(messageDeleteEvent.getGuild().getIdLong());

        GuildMessage guildMessage = new GuildMessage.Builder(
                messageReceivedEvent.getGuild().getIdLong(),
                messageReceivedEvent.getChannel().getIdLong(),
                messageReceivedEvent.getMessageIdLong(),
                messageReceivedEvent.getAuthor().getIdLong(),
                messageReceivedEvent.getMember()
        )
                .setMessageContent(messageReceivedEvent.getMessage().getContentRaw())
                .setMessageAttachmentList(messageReceivedEvent.getMessage().getAttachments())
                .build();

        GuildMessageCache.getInstance().updateGuildMessage(messageReceivedEvent.getMessageIdLong(), guildMessage);
    }

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent messageDeleteEvent) {
        if (!messageDeleteEvent.isFromGuild()) return;

        // TODO: Check if the Guild the MessageReceivedEvent occurred in chooses to log message edits and deletions.
        // GuildConfiguration guildConfiguration = GUILD_CONFIGURATION_CACHE.getGuildConfiguration(messageDeleteEvent.getGuild().getIdLong());

        GuildMessage guildMessage = GuildMessageCache.getInstance().getGuildMessage(messageDeleteEvent.getMessageIdLong());

        if (guildMessage == null) return;

        GuildChannel guildChannel = messageDeleteEvent.getGuild().getGuildChannelById(1315265738835230750L /* guildConfiguration.getLogChannelId() */);

        if (guildChannel instanceof MessageChannel messageChannel) {
            EmbedBuilder deletedMessageEmbedBuilder = new EmbedBuilder()
                    .setTitle("A message was deleted!")
                    .setDescription(guildMessage.getMessageContent())
                    .addField("Author", guildMessage.getAuthor().getAsMention(), true)
                    .addField("Channel", "<#" + guildMessage.getChannelId() + ">", true)
                    .setThumbnail(guildMessage.getAuthor().getUser().getEffectiveAvatarUrl())
                    .setColor(Color.decode("#BF3F3F"));

            if (!guildMessage.getMessageAttachmentList().isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder();

                guildMessage.getMessageAttachmentList().forEach(attachment -> {
                    String attachmentString = "[" + attachment.getFileName() + "](" + attachment.getUrl() + ")\n";

                    stringBuilder.append(attachmentString);
                });

                if (stringBuilder.toString().length() > 1024) {
                    stringBuilder.delete(0, stringBuilder.length());

                    guildMessage.getMessageAttachmentList().forEach(attachment -> {
                        String attachmentString = attachment.getFileName() + "\n";

                        stringBuilder.append(attachmentString);
                    });

                    if (stringBuilder.toString().length() > 1024) {
                        stringBuilder.delete(1025, stringBuilder.length());
                    }
                }

                deletedMessageEmbedBuilder.addField("Attachments", stringBuilder.toString(), false);
            }

            messageChannel.sendMessageEmbeds(deletedMessageEmbedBuilder.build()).queue();
        } else {
            LOGGER.warn("Message {} in Guild {} was deleted, but no suitable log channel was found!", guildMessage.getMessageId(), messageDeleteEvent.getGuild().getIdLong());
        }

        GuildMessageCache.getInstance().deleteGuildMessage(messageDeleteEvent.getMessageIdLong());
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent messageUpdateEvent) {
        if (!messageUpdateEvent.isFromGuild()) return;
        if (messageUpdateEvent.getAuthor().isBot()) return;

        // TODO: Check if the Guild the MessageReceivedEvent occurred in chooses to log message edits and deletions.
        // GuildConfiguration guildConfiguration = GUILD_CONFIGURATION_CACHE.getGuildConfiguration(messageDeleteEvent.getGuild().getIdLong());

        GuildMessage guildMessage = GuildMessageCache.getInstance().getGuildMessage(messageUpdateEvent.getMessageIdLong());

        if (guildMessage == null) return;

        if (messageUpdateEvent.getMessage().getContentRaw().equals(guildMessage.getMessageContent())
                && messageUpdateEvent.getMessage().getAttachments().equals(guildMessage.getMessageAttachmentList())
        ) return;

        GuildChannel guildChannel = messageUpdateEvent.getGuild().getGuildChannelById(1315265738835230750L /* guildConfiguration.getLogChannelId() */);

        if (guildChannel instanceof MessageChannel messageChannel) {
            EmbedBuilder editedMessageEmbedBuilder = new EmbedBuilder()
                    .setTitle("A message was edited!", messageUpdateEvent.getMessage().getJumpUrl())
                    .addField("Author", guildMessage.getAuthor().getAsMention(), true)
                    .addField("Channel", "<#" + guildMessage.getChannelId() + ">", true)
                    .setThumbnail(messageUpdateEvent.getMember().getEffectiveAvatarUrl())
                    .setColor(Color.decode("#3F3FBF"));

            if (!messageUpdateEvent.getMessage().getContentRaw().equals(guildMessage.getMessageContent())) {
                if (messageUpdateEvent.getMessage().getContentRaw().length() + guildMessage.getMessageContent().length() < 5954) {
                    editedMessageEmbedBuilder.setDescription(
                            "**Original Message**\n\n" + guildMessage.getMessageContent() +
                            "\n\n**Updated Message**\n\n" + messageUpdateEvent.getMessage().getContentRaw()
                    );
                }
            }

            if (!messageUpdateEvent.getMessage().getAttachments().equals(guildMessage.getMessageAttachmentList())) {
                StringBuilder stringBuilder = new StringBuilder();

                guildMessage.getMessageAttachmentList().forEach(attachment -> {
                    String attachmentString = "[" + attachment.getFileName() + "](" + attachment.getUrl() + ")\n";

                    stringBuilder.append(attachmentString);
                });

                if (stringBuilder.toString().length() > 1024) {
                    stringBuilder.delete(0, stringBuilder.length());

                    guildMessage.getMessageAttachmentList().forEach(attachment -> {
                        String attachmentString = attachment.getFileName() + "\n";

                        stringBuilder.append(attachmentString);
                    });

                    if (stringBuilder.toString().length() > 1024) {
                        stringBuilder.delete(1025, stringBuilder.length());
                    }
                }

                editedMessageEmbedBuilder.addField("Original Attachments", stringBuilder.toString(), false);

                stringBuilder.delete(0, stringBuilder.length());

                if (!messageUpdateEvent.getMessage().getAttachments().isEmpty()) {
                    messageUpdateEvent.getMessage().getAttachments().forEach(attachment -> {
                        String attachmentString = "[" + attachment.getFileName() + "](" + attachment.getUrl() + ")\n";

                        stringBuilder.append(attachmentString);
                    });

                    if (stringBuilder.toString().length() > 1024) {
                        stringBuilder.delete(0, stringBuilder.length());

                        messageUpdateEvent.getMessage().getAttachments().forEach(attachment -> {
                            String attachmentString = attachment.getFileName() + "\n";

                            stringBuilder.append(attachmentString);
                        });

                        if (stringBuilder.toString().length() > 1024) {
                            stringBuilder.delete(1025, stringBuilder.length());
                        }
                    }
                } else {
                    stringBuilder.append("None");
                }

                editedMessageEmbedBuilder.addField("Updated Attachments", stringBuilder.toString(), false);
            }

            messageChannel.sendMessageEmbeds(editedMessageEmbedBuilder.build()).queue();

            GuildMessageCache.getInstance().updateGuildMessage(
                    messageUpdateEvent.getMessageIdLong(),
                    GuildMessage.Builder.from(guildMessage)
                            .setMessageContent(messageUpdateEvent.getMessage().getContentRaw())
                            .setMessageAttachmentList(messageUpdateEvent.getMessage().getAttachments())
                            .build()
            );
        } else {
            LOGGER.warn("Message {} in Guild {} was edited, but no suitable log channel was found!", guildMessage.getMessageId(), messageUpdateEvent.getGuild().getIdLong());
        }
    }
}
