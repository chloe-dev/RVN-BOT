package bio.chloe.interfaces.interactions;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public interface SlashCommandInteraction {
    void handleSlashCommandInteraction(SlashCommandInteractionEvent slashCommandInteractionEvent);

    SlashCommandData getSlashCommandData();
}
