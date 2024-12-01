package bio.chloe.commands;

import bio.chloe.interfaces.ISlashCommandInteraction;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.*;

public class Ping implements ISlashCommandInteraction {
    @Override
    public void handleSlashCommandInteraction(SlashCommandInteractionEvent slashCommandInteractionEvent) {
        long gatewayPing = slashCommandInteractionEvent.getJDA().getGatewayPing();

        slashCommandInteractionEvent.getJDA().getRestPing().queue(restResponsePing -> {
           MessageEmbed responseEmbed = new EmbedBuilder()
                   .setDescription(String.format(
                           "Gateway response took **%d** milliseconds, with a REST response of **%d** milliseconds.", gatewayPing, restResponsePing
                   ))
                   .setColor(Color.CYAN)
                   .build();

           slashCommandInteractionEvent.replyEmbeds(responseEmbed).setEphemeral(true).queue();
        });
    }

    @Override
    public SlashCommandData getSlashCommandData() {
        return Commands.slash("ping", "Calculate the gateway and REST response ping in milliseconds.");
    }

    @Override
    public void handleCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent commandAutoCompleteInteractionEvent) {
        throw new UnsupportedOperationException("This command does not support CommandAutoCompleteInteractionEvent(s).");
    }
}
