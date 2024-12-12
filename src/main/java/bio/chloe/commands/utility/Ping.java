package bio.chloe.commands.utility;

import bio.chloe.interfaces.interactions.SlashCommandInteraction;
import bio.chloe.utility.Embeds;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class Ping implements SlashCommandInteraction {
    @Override
    public void handleSlashCommandInteraction(SlashCommandInteractionEvent slashCommandInteractionEvent) {
        long gatewayPing = slashCommandInteractionEvent.getJDA().getGatewayPing();

        slashCommandInteractionEvent.getJDA().getRestPing().queue(restResponsePing ->
                slashCommandInteractionEvent.replyEmbeds(
                        Embeds.infoEmbed(
                        String.format("Gateway (**%d**ms) and REST (**%d**ms).", gatewayPing, restResponsePing)
                        )
                ).setEphemeral(true).queue()
        );
    }

    @Override
    public SlashCommandData getSlashCommandData() {
        return Commands.slash("ping", "Calculate the gateway and REST response ping in milliseconds.");
    }
}
