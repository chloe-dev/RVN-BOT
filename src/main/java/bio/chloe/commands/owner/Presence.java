package bio.chloe.commands.owner;

import bio.chloe.configuration.Configuration;
import bio.chloe.interfaces.interactions.SlashCommandInteraction;
import bio.chloe.utility.Embeds;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;

public class Presence implements SlashCommandInteraction {
    @Override
    public void handleSlashCommandInteraction(SlashCommandInteractionEvent slashCommandInteractionEvent) {
        slashCommandInteractionEvent.deferReply(true).queue();

        final Configuration CONFIGURATION_INSTANCE = Configuration.getInstance();

        if (slashCommandInteractionEvent.getUser().getIdLong() != CONFIGURATION_INSTANCE.optLong("ownerUserId", 0L)) {
            slashCommandInteractionEvent.getHook().editOriginalEmbeds(Embeds.errorEmbed(
                    "You do not have permission to access this command."
            )).queue(); return;
        }

        final String ONLINE_STATUS_INPUT = slashCommandInteractionEvent.getOption("online-status", "", OptionMapping::getAsString);
        final String ACTIVITY_INPUT = slashCommandInteractionEvent.getOption("activity", "", OptionMapping::getAsString);
        final String STATUS_MESSAGE_INPUT = slashCommandInteractionEvent.getOption("status-message", "", OptionMapping::getAsString);
        final String STREAM_URL_INPUT = slashCommandInteractionEvent.getOption("stream-url", "", OptionMapping::getAsString);

        OnlineStatus onlineStatus = null;

        switch (ONLINE_STATUS_INPUT) {
            case "ONLINE": {
                onlineStatus = OnlineStatus.ONLINE; break;
            } case "IDLE": {
                onlineStatus = OnlineStatus.IDLE; break;
            } case "DO_NOT_DISTURB": {
                onlineStatus = OnlineStatus.DO_NOT_DISTURB; break;
            }
        }

        if (onlineStatus == null) {
            slashCommandInteractionEvent.getHook().editOriginalEmbeds(Embeds.errorEmbed(
                    "An invalid online status was provided, or none was provided at all."
            )).queue(); return;
        }

        if (ACTIVITY_INPUT.isEmpty() && STATUS_MESSAGE_INPUT.isEmpty()) {
            slashCommandInteractionEvent.getJDA().getPresence().setPresence(
                    onlineStatus, false
            );

            slashCommandInteractionEvent.getJDA().getPresence().setActivity(null);

            slashCommandInteractionEvent.getHook().editOriginalEmbeds(Embeds.infoEmbed(
                    "Successfully updated the Bot's presence."
            )).queue();

            return;
        }

        if (ACTIVITY_INPUT.isEmpty()) {
            slashCommandInteractionEvent.getJDA().getPresence().setPresence(
                    onlineStatus, Activity.customStatus(STATUS_MESSAGE_INPUT)
            );

            slashCommandInteractionEvent.getHook().editOriginalEmbeds(Embeds.infoEmbed(
                    "Successfully updated the Bot's presence."
            )).queue();

            return;
        }

        if (STATUS_MESSAGE_INPUT.isEmpty()) {
            slashCommandInteractionEvent.getHook().editOriginalEmbeds(Embeds.errorEmbed(
                    "An activity was provided, but without a status message."
            )).queue(); return;
        }

        Activity activity = null;

        switch (ACTIVITY_INPUT) {
            case "COMPETING": {
                activity = Activity.competing(STATUS_MESSAGE_INPUT); break;
            } case "LISTENING": {
                activity = Activity.listening(STATUS_MESSAGE_INPUT); break;
            } case "PLAYING": {
                activity = Activity.playing(STATUS_MESSAGE_INPUT); break;
            } case "STREAMING": {
                if (STREAM_URL_INPUT.isEmpty() || !Activity.isValidStreamingUrl(STREAM_URL_INPUT)) {
                    slashCommandInteractionEvent.getHook().editOriginalEmbeds(Embeds.errorEmbed(
                            "The Streaming activity was selected, but no valid stream URL was provided."
                    )).queue(); return;
                }

                activity = Activity.streaming(STATUS_MESSAGE_INPUT, STREAM_URL_INPUT);

                break;
            } case "WATCHING": {
                activity = Activity.watching(STATUS_MESSAGE_INPUT); break;
            }
        }

        if (activity == null) {
            slashCommandInteractionEvent.getHook().editOriginalEmbeds(Embeds.errorEmbed(
                    "An invalid activity was selected, or no activity was provided when it was required."
            )).queue(); return;
        }

        slashCommandInteractionEvent.getJDA().getPresence().setPresence(onlineStatus, activity);

        slashCommandInteractionEvent.getHook().editOriginalEmbeds(Embeds.infoEmbed(
                "Successfully updated the Bot's presence."
        )).queue();
    }

    @Override
    public SlashCommandData getSlashCommandData() {
        final OptionData ONLINE_STATUS_OPTION_DATA = new OptionData(OptionType.STRING, "online-status", "Set the Bot's online status.")
                .addChoice("Online", "ONLINE")
                .addChoice("Idle (Away)", "IDLE")
                .addChoice("Do Not Disturb", "DO_NOT_DISTURB")
                // NOTE: Invisible/Offline is intentionally not present.
                .setRequired(true);

        final OptionData ACTIVITY_OPTION_DATA = new OptionData(OptionType.STRING, "activity", "Set the Bot's activity.")
                .addChoice("Competing", "COMPETING")
                .addChoice("Listening", "LISTENING")
                .addChoice("Playing", "PLAYING")
                .addChoice("Streaming", "STREAMING")
                .addChoice("Watching", "WATCHING")
                .setRequired(false);

        final OptionData STATUS_MESSAGE_OPTION_DATA = new OptionData(OptionType.STRING, "status-message", "Set the Bot's status message.")
                .setRequiredLength(1, 128)
                .setRequired(false);

        final OptionData STREAM_URL_OPTION_DATA = new OptionData(OptionType.STRING, "stream-url", "Set the Bot's stream URL if using the Streaming activity.")
                .setRequiredLength(1, 256)
                .setRequired(false);

        List<OptionData> optionDataList = List.of(ONLINE_STATUS_OPTION_DATA, ACTIVITY_OPTION_DATA, STATUS_MESSAGE_OPTION_DATA, STREAM_URL_OPTION_DATA);

        return Commands.slash("presence", "Set the Bot's online status, status message, and activity.")
                .setGuildOnly(true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
                .addOptions(optionDataList);
    }
}
