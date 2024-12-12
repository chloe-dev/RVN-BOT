package bio.chloe.commands.starcitizen;

import bio.chloe.interfaces.interactions.CommandAutoCompleteInteraction;
import bio.chloe.interfaces.interactions.SlashCommandInteraction;
import bio.chloe.utility.Embeds;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;

public class Beacon implements SlashCommandInteraction, CommandAutoCompleteInteraction {
    final long BEACON_CHANNEL_ID = 1311442357664747670L; // guildConfiguration.getBeaconChannelId();

    @Override
    public void handleSlashCommandInteraction(SlashCommandInteractionEvent slashCommandInteractionEvent) {
        slashCommandInteractionEvent.deferReply().setEphemeral(true).queue();

        String description = slashCommandInteractionEvent.getOption("description", "", OptionMapping::getAsString);
        String partyLeader = slashCommandInteractionEvent.getOption("party-leader", "", OptionMapping::getAsString);
        String priority = slashCommandInteractionEvent.getOption("priority", "", OptionMapping::getAsString);
        boolean publish = slashCommandInteractionEvent.getOption("publish", false, OptionMapping::getAsBoolean);
        long channelId = slashCommandInteractionEvent.getOption("channel", null, OptionMapping::getAsLong);

        MessageEmbed beaconEmbed = new EmbedBuilder()
                .setTitle("**REQUESTING ASSISTANCE**")
                .setDescription(description)
                .addField("Party Leader", partyLeader, true)
                .addField("Priority", priority, true)
                .addField("Published", String.valueOf(publish).toUpperCase(Locale.ENGLISH), true)
                .setThumbnail(slashCommandInteractionEvent.getUser().getAvatarUrl())
                .setFooter("Submitted")
                .setTimestamp(OffsetDateTime.now())
                .setColor(Color.decode("#1F1FFF"))
                .build();

        GuildChannel guildChannel = slashCommandInteractionEvent.getGuild().getGuildChannelById(channelId);

        if (guildChannel instanceof GuildMessageChannel guildMessageChannel) {
            guildMessageChannel.sendMessageEmbeds(beaconEmbed).queue(message -> {
                if (publish && guildMessageChannel instanceof NewsChannel newsChannel) {
                    newsChannel.crosspostMessageById(message.getIdLong()).queue(success -> {
                        slashCommandInteractionEvent.getHook().editOriginalEmbeds(Embeds.infoEmbed(
                                "Successfully submitted a Beacon to <#" + BEACON_CHANNEL_ID + "> and published it to following servers."
                        )).queue();
                    }, failure -> {
                        slashCommandInteractionEvent.getHook().editOriginalEmbeds(Embeds.warnEmbed(
                                "Successfully submitted a Beacon to <#" + BEACON_CHANNEL_ID + ">, but failed to publish it to following servers."
                        )).queue();
                    });
                } else if (publish) {
                    slashCommandInteractionEvent.getHook().editOriginalEmbeds(Embeds.warnEmbed(
                            "Successfully submitted a Beacon to <#" + BEACON_CHANNEL_ID + ">, but failed to publish it to following servers as it is not a News channel."
                    )).queue();
                } else {
                    slashCommandInteractionEvent.getInteraction().getHook().editOriginalEmbeds(
                            Embeds.infoEmbed("Successfully submitted a beacon request to <#" + BEACON_CHANNEL_ID + ">.")
                    ).queue();
                }
            });
        } else {
            slashCommandInteractionEvent.getInteraction().getHook().editOriginalEmbeds(
                    Embeds.errorEmbed("Failed to submit a beacon request to <#" + BEACON_CHANNEL_ID + ">.")
            ).queue();
        }
    }

    @Override
    public SlashCommandData getSlashCommandData() {
        OptionData descriptionOptionData = new OptionData(OptionType.STRING, "description", "Briefly describe the ongoing situation.")
                .setMinLength(1)
                .setMaxLength(500)
                .setRequired(true).setAutoComplete(false);

        OptionData partyLeaderOptionData = new OptionData(OptionType.STRING, "party-leader", "RSI handle of the party leader (or yourself, if alone).")
                .setMinLength(3)
                .setMaxLength(30)
                .setRequired(true).setAutoComplete(false);

        OptionData priortyOptionData = new OptionData(OptionType.STRING, "priority", "Select the response priority level.")
                .setRequired(true).setAutoComplete(false)
                .addChoice("Critical", "Critical")
                .addChoice("High", "High")
                .addChoice("Moderate", "Moderate")
                .addChoice("Low", "Low");

        OptionData publishOptionData = new OptionData(OptionType.BOOLEAN, "publish", "Publish the beacon to servers following the channel designated by the configuration?")
                .setRequired(true).setAutoComplete(false);

        OptionData channelOptionData = new OptionData(OptionType.STRING, "channel", "Select the channel for the beacon to be broadcast in.")
                .setRequired(true).setAutoComplete(true);

        List<OptionData> optionsList = List.of(
                descriptionOptionData, partyLeaderOptionData, priortyOptionData, publishOptionData, channelOptionData
        );

        return Commands.slash("beacon", "Place a beacon in a channel designated by the configuration.")
                .addOptions(optionsList)
                .setGuildOnly(true);
    }

    @Override
    public void handleCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent commandAutoCompleteInteractionEvent) {
        if (!commandAutoCompleteInteractionEvent.getFocusedOption().getName().equals("channel")) return;

        // GuildConfiguration guildConfiguration = GuildConfigurationCache.getInstance().getGuildConfiguration(commandAutoCompleteInteractionEvent.getGuild().getIdLong());

        GuildChannel guildChannel = commandAutoCompleteInteractionEvent.getGuild().getGuildChannelById(BEACON_CHANNEL_ID); // Temporary.

        List<GuildChannel> guildChannels = List.of(guildChannel); // Temporary.

        Member member = commandAutoCompleteInteractionEvent.getMember();

        if (member == null)
            return;

        List<Command.Choice> channelChoices = guildChannels.stream()
                .filter(channel -> member.hasPermission(channel,Permission.VIEW_CHANNEL))
                .map(channel -> new Command.Choice(
                    "#" + channel.getName(), channel.getId()
                )).toList();

        commandAutoCompleteInteractionEvent.replyChoices(
                channelChoices.stream().limit(25).toList()
        ).queue();
    }
}
