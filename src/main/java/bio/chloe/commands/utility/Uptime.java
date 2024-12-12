package bio.chloe.commands.utility;

import bio.chloe.interfaces.interactions.SlashCommandInteraction;
import bio.chloe.utility.Embeds;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Uptime implements SlashCommandInteraction {
    @Override
    public void handleSlashCommandInteraction(SlashCommandInteractionEvent slashCommandInteractionEvent) {
        slashCommandInteractionEvent.deferReply(true).queue();

        String formattedUptime = getFormattedUptime();

        slashCommandInteractionEvent.getHook().editOriginalEmbeds(
                Embeds.infoEmbed(
                        String.format("Raven has been online for %s.", formattedUptime)
                )
        ).queue();
    }

    @Override
    public SlashCommandData getSlashCommandData() {
        return Commands.slash("uptime", "Displays Raven's current uptime.");
    }

    private String getFormattedUptime() {
        List<String> timeUnitStrings = getTimeUnitStrings();

        final int listSize = timeUnitStrings.size();

        if (listSize == 1) {
            return timeUnitStrings.get(0);
        }

        if (listSize == 2) {
            return String.format("%s and %s", timeUnitStrings.get(0), timeUnitStrings.get(1));
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (int idx = 0; idx < listSize; ++idx) {
            stringBuilder.append(timeUnitStrings.get(idx));

            if (idx < (listSize - 1)) {
                stringBuilder.append(idx == (listSize - 2) ? ", and " : ", ");
            }
        }

        return stringBuilder.toString();
    }

    private List<String> getTimeUnitStrings() {
        // NOTE: this function returns the uptime of the JVM itself!
        Duration uptimeDuration = Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime());

        long days = uptimeDuration.toDays();
        long hours = uptimeDuration.toHours() % 24;
        long minutes = uptimeDuration.toMinutes() % 60;
        long seconds = uptimeDuration.toSeconds() % 60;

        return Stream.of(
                days > 0 ? days + " day" + (days > 1 ? "s" : "") : null,
                hours > 0 ? hours + " hour" + (hours > 1 ? "s" : "") : null,
                minutes > 0 ? minutes + " minute" + (minutes > 1 ? "s" : "") : null,
                seconds > 0 ? seconds + " second" + (seconds > 1 ? "s" : "") : null
        ).filter(Objects::nonNull).toList();
    }
}
