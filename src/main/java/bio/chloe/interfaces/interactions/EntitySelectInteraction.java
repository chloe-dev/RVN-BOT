package bio.chloe.interfaces.interactions;

import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;

import java.util.List;

public interface EntitySelectInteraction {
    void handleEntitySelectInteraction(EntitySelectInteractionEvent entitySelectInteractionEvent);

    List<String> getEntityIds();
}
