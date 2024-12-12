package bio.chloe.interfaces.interactions;

import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

import java.util.List;

public interface StringSelectInteraction {
    void handleStringSelectInteraction(StringSelectInteractionEvent stringSelectInteractionEvent);

    List<String> getStringIds();
}
