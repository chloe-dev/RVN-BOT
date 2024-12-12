package bio.chloe.interfaces.interactions;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

public interface UserContextInteraction {
    void handleUserContextInteraction(UserContextInteractionEvent userContextInteractionEvent);

    // TODO: Extras?
}
