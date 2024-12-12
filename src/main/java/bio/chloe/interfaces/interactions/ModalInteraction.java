package bio.chloe.interfaces.interactions;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

import java.util.List;

public interface ModalInteraction {
    void handleModalInteraction(ModalInteractionEvent modalInteractionEvent);

    List<String> getModalIds();
}
