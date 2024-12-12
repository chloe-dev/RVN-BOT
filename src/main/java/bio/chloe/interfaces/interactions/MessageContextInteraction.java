package bio.chloe.interfaces.interactions;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;

public interface MessageContextInteraction {
    void handleMessageContextInteraction(MessageContextInteractionEvent messageContextInteractionEvent);

    // TODO: Extras?
}
