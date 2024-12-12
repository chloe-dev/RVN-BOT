package bio.chloe.interfaces.interactions;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;

public interface CommandAutoCompleteInteraction extends SlashCommandInteraction {
    void handleCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent commandAutoCompleteInteractionEvent);
}
