package bio.chloe.commands.starcitizen;

import bio.chloe.interfaces.interactions.ButtonInteraction;
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
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public class Beacon implements SlashCommandInteraction, CommandAutoCompleteInteraction, ButtonInteraction {
    private final long BEACON_CHANNEL_ID = 1311442357664747670L; // TODO: HARD-CODED FOR DEBUG PURPOSES.

    @Override
    public void handleSlashCommandInteraction(SlashCommandInteractionEvent slashCommandInteractionEvent) {
        slashCommandInteractionEvent.deferReply(true).queue();

        final String DESCRIPTION = slashCommandInteractionEvent.getOption("description", "", OptionMapping::getAsString);
        final String PARTY_LEADER = slashCommandInteractionEvent.getOption("party-leader", "", OptionMapping::getAsString);
        final String PRIORITY = slashCommandInteractionEvent.getOption("priority", "", OptionMapping::getAsString);
        final boolean PUBLISH = slashCommandInteractionEvent.getOption("publish", false, OptionMapping::getAsBoolean);
        final String CHANNEL_ID = slashCommandInteractionEvent.getOption("channel", "", OptionMapping::getAsString);

        // TODO: Check whether the channel ID provided matches any of the permitted channel IDs for that member.

        MessageEmbed beaconMessageEmbed = new EmbedBuilder()
                .setTitle("**REQUESTING ADDITIONAL ASSISTANCE**")
                .setDescription(DESCRIPTION)
                .addField("Party Leader", PARTY_LEADER, true)
                .addField("Priority Level", PRIORITY, true)
                .addField("Published", Boolean.toString(PUBLISH), true)
                .setThumbnail(slashCommandInteractionEvent.getUser().getAvatarUrl())
                .setFooter("Submitted by " + slashCommandInteractionEvent.getUser().getName())
                .setTimestamp(OffsetDateTime.now())
                .setColor(Color.decode("#A3A3FF"))
                .build();

        GuildChannel guildChannel = slashCommandInteractionEvent.getGuild().getGuildChannelById(CHANNEL_ID);

        if (guildChannel instanceof GuildMessageChannel guildMessageChannel) {
            guildMessageChannel.sendMessageEmbeds(beaconMessageEmbed)
                    .addActionRow(
                            Button.success("complete", "Mark as Complete"),
                            Button.danger("cancel", "Cancel")
                    ).queue(guildMessage -> {
                        if (PUBLISH && guildMessageChannel instanceof NewsChannel newsChannel) {
                            newsChannel.crosspostMessageById(guildMessage.getIdLong()).queue(
                                success -> slashCommandInteractionEvent.getHook().editOriginalEmbeds(Embeds.infoEmbed(
                                        "Submitted a Beacon to <#" + CHANNEL_ID + "> and published it to following servers."
                                )).queue(),
                                failure -> slashCommandInteractionEvent.getHook().editOriginalEmbeds(Embeds.warnEmbed(
                                        "Submitted a Beacon to <#" + CHANNEL_ID + ">, but failed to publish it to following servers."
                                )).queue()
                            );
                        } else if (PUBLISH) {
                            slashCommandInteractionEvent.getHook().editOriginalEmbeds(Embeds.warnEmbed(
                                    "Submitted a Beacon to <#" + CHANNEL_ID + ">, however, publishing failed as it is not a News channel."
                            )).queue();
                        } else {
                            slashCommandInteractionEvent.getHook().editOriginalEmbeds(Embeds.infoEmbed(
                                    "Submitted a Beacon to <#" + CHANNEL_ID + ">."
                            )).queue();
                        }
                    });
        } else {
            slashCommandInteractionEvent.getHook().editOriginalEmbeds(Embeds.errorEmbed(
                    "Failed to submit a Beacon to <#" + CHANNEL_ID + ">."
            )).queue();
        }
    }

    @Override
    public SlashCommandData getSlashCommandData() {
        final OptionData DESCRIPTION_OPTION_DATA = new OptionData(
                OptionType.STRING, "description", "A description of the ongoing situation."
        ).setMinLength(1).setMaxLength(1024).setRequired(true);

        final OptionData PARTY_LEADER_OPTION_DATA = new OptionData(
                OptionType.STRING, "party-leader", "The RSI handle of the party leader (or oneself, if alone)."
        ).setMinLength(3).setMaxLength(30).setRequired(true);

        final OptionData PRIORITY_OPTION_DATA = new OptionData(
                OptionType.STRING, "priority", "The priority level of the beacon."
        ).addChoice("Critical", "Critical")
         .addChoice("High", "High")
         .addChoice("Moderate", "Moderate")
         .addChoice("Low", "Low")
         .setRequired(true);

        final OptionData PUBLISH_OPTION_DATA = new OptionData(
                OptionType.BOOLEAN, "publish", "Publish this beacon to servers following the channel?"
        ).setRequired(true);

        // WARNING: This option blindly takes a String! It MUST be compared against the channel IDs of
        // guild-configured beacon channels, otherwise it WILL post the message in any channel (if the
        // ID is provided as a value for this option). This is unfortunately required as it is the only
        // way to show a suggested list of filtered channels (using a CommandAutoCompleteInteraction).

        final OptionData CHANNEL_OPTION_DATA = new OptionData(
                OptionType.STRING, "channel", "Select the channel for the beacon to be broadcast in."
        ).setRequired(true).setAutoComplete(true);

        return Commands.slash("beacon", "Place an emergency beacon in a guild-specified channel.")
                .addOptions(List.of(
                        DESCRIPTION_OPTION_DATA,
                        PARTY_LEADER_OPTION_DATA,
                        PRIORITY_OPTION_DATA,
                        PUBLISH_OPTION_DATA,
                        CHANNEL_OPTION_DATA
                )).setGuildOnly(true);
    }

    @Override
    public void handleCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent commandAutoCompleteInteractionEvent) {
        if (!commandAutoCompleteInteractionEvent.getFocusedOption().getName().equals("channel")) return;

        // GuildConfiguration guildConfiguration = GuildConfiguration.getInstance.getGuildConfiguration(commandAutoCompleteInteractionEvent.getGuild().getIdLong());

        GuildChannel guildChannel = commandAutoCompleteInteractionEvent.getGuild().getGuildChannelById(BEACON_CHANNEL_ID); // TODO: HARD-CODED FOR DEBUG PURPOSES.

        List<GuildChannel> guildChannelList = List.of(guildChannel); // TODO: HARD-CODED FOR DEBUG PURPOSES.

        Member guildMember = commandAutoCompleteInteractionEvent.getMember();

        List<Command.Choice> guildChannelChoiceList = guildChannelList.stream()
                .filter(permittedGuildChannel -> guildMember.hasPermission(permittedGuildChannel, Permission.VIEW_CHANNEL))
                .map(guildChannelChoice -> new Command.Choice("#" + guildChannelChoice.getName(), guildChannelChoice.getId()))
                .toList();

        commandAutoCompleteInteractionEvent.replyChoices(
                guildChannelChoiceList.stream().limit(25).toList()
        ).queue();
    }

    @Override
    public void handleButtonInteraction(ButtonInteractionEvent buttonInteractionEvent) {
        Optional<MessageEmbed> originalEmbedOptional = buttonInteractionEvent.getMessage().getEmbeds().stream().findFirst();

        if (originalEmbedOptional.isEmpty()) {
            buttonInteractionEvent.replyEmbeds(Embeds.errorEmbed(
                "No suitable embed found to edit."
            )).setEphemeral(true).queue();

            return;
        }

        MessageEmbed originalEmbed = originalEmbedOptional.get();

        String originalDescription = originalEmbed.getDescription();
        String originalThumbnail = originalEmbed.getThumbnail().getUrl() + "?size=64"; // TODO: Resolve potential NPE.

        switch (buttonInteractionEvent.getComponentId()) {
            case "complete": {
                MessageEmbed completedMessageEmbed = new EmbedBuilder()
                        .setTitle("**Request for Assistance Completed**")
                        .setDescription(originalDescription)
                        .setThumbnail(originalThumbnail)
                        .setFooter("Marked as Completed by " + buttonInteractionEvent.getUser().getName())
                        .setTimestamp(OffsetDateTime.now())
                        .setColor(Color.decode("#3FBF3F"))
                        .build();

                buttonInteractionEvent
                        .editMessageEmbeds(completedMessageEmbed)
                        .setComponents()
                        .queue();

                break;
            } case "cancel": {
                MessageEmbed cancelledMessageEmbed = new EmbedBuilder()
                        .setTitle("**Request for Assistance Cancelled**")
                        .setDescription(originalDescription)
                        .setThumbnail(originalThumbnail)
                        .setFooter("Marked as Cancelled by " + buttonInteractionEvent.getUser().getName())
                        .setTimestamp(OffsetDateTime.now())
                        .setColor(Color.decode("#BF3F3F"))
                        .build();

                buttonInteractionEvent
                        .editMessageEmbeds(cancelledMessageEmbed)
                        .setComponents()
                        .queue();

                break;
            } default: {
                buttonInteractionEvent.replyEmbeds(Embeds.errorEmbed(
                        "An unknown button was pressed, check the Button IDs for this component."
                )).setEphemeral(true).queue();
            }
        }
    }

    @Override
    public List<String> getButtonIds() {
        return List.of("complete", "cancel");
    }
}
