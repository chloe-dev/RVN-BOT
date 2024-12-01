package bio.chloe.interfaces;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public interface ISlashCommandInteraction {
    void handleSlashCommandInteraction(SlashCommandInteractionEvent slashCommandInteractionEvent);

    SlashCommandData getSlashCommandData();

    void handleCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent commandAutoCompleteInteractionEvent);
}
