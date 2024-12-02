package bio.chloe.handlers;

import bio.chloe.commands.utility.Ping;
import bio.chloe.commands.utility.Uptime;
import bio.chloe.interfaces.ISlashCommandInteraction;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SlashCommandHandler extends ListenerAdapter {
    private final Map<String, ISlashCommandInteraction> slashCommandMap = new HashMap<>();

    public SlashCommandHandler(JDA jdaObject) {
        initSlashCommandMap();

        // TODO: Do not call this on startup (in the future): https://github.com/discord-jda/JDA/wiki/Interactions#slash-commands.
        jdaObject.updateCommands().addCommands(slashCommandMap.values().stream()
                .map(ISlashCommandInteraction::getSlashCommandData)
                .collect(Collectors.toList())
        ).queue();
    }

    private void registerSlashCommand(ISlashCommandInteraction slashCommandInteractionEvent) {
        slashCommandMap.put(slashCommandInteractionEvent.getSlashCommandData().getName(), slashCommandInteractionEvent);
    }

    private void initSlashCommandMap() {
        // Utility.
        registerSlashCommand(new Ping());
        registerSlashCommand(new Uptime());
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent slashCommandInteractionEvent) {
        slashCommandMap.get(slashCommandInteractionEvent.getName()).handleSlashCommandInteraction(slashCommandInteractionEvent);
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent commandAutoCompleteInteractionEvent) {
        slashCommandMap.get(commandAutoCompleteInteractionEvent.getName()).handleCommandAutoCompleteInteraction(commandAutoCompleteInteractionEvent);
    }
}
