package bio.chloe.interfaces.interactions;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.List;

public interface ButtonInteraction {
    void handleButtonInteraction(ButtonInteractionEvent buttonInteractionEvent);

    List<String> getButtonIds();
}
