package bio.chloe.handlers;

import bio.chloe.commands.starcitizen.Beacon;
import bio.chloe.commands.utility.Ping;
import bio.chloe.commands.utility.Uptime;
import bio.chloe.interfaces.interactions.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class InteractionHandler extends ListenerAdapter {
    // TODO: See TODOs below about the inclusion of a Logger in this class.
    private static final Logger LOGGER = LoggerFactory.getLogger(InteractionHandler.class);

    // Commands.
    private final Map<String, SlashCommandInteraction> slashCommandInteractionMap = new HashMap<>();
    private final Map<String, CommandAutoCompleteInteraction> commandAutoCompleteInteractionMap = new HashMap<>();

    private final Map<String, MessageContextInteraction> messageContextInteractionMap = new HashMap<>();
    private final Map<String, UserContextInteraction> userContextInteractionMap = new HashMap<>();

    // Components.

    private final Map<String, ButtonInteraction> buttonInteractionMap = new HashMap<>();

    private final Map<String, EntitySelectInteraction> entitySelectInteractionMap = new HashMap<>();
    private final Map<String, StringSelectInteraction> stringSelectInteractionMap = new HashMap<>();

    // Modals.

    private final Map<String, ModalInteraction> modalInteractionEventMap = new HashMap<>();

    public InteractionHandler(JDA jdaObject) {
        initializeInteractionMaps();

        synchronizeGlobalCommands(jdaObject);
    }

    private void initializeInteractionMaps() {
        // Commands.

        initializeSlashCommandInteractionMap();
        initializeCommandAutoCompleteInteractionMap();

        initializeMessageContextInteractionMap();
        initializeUserContextInteractionMap();

        // Components.

        initializeButtonInteractionMap();
        initializeEntitySelectInteractionMap();
        initializeStringSelectInteractionMap();

        // Modals.

        initializeModalInteractionMap();
    }

    private void synchronizeGlobalCommands(JDA jdaObject) {
        jdaObject.retrieveCommands().queue(existingSlashCommandList -> {
            boolean requiresSynchronization = existingSlashCommandList.size() != slashCommandInteractionMap.size();

            if (!requiresSynchronization) {
                requiresSynchronization = existingSlashCommandList.stream()
                        .noneMatch(existingSlashCommand -> slashCommandInteractionMap.values().stream()
                                .anyMatch(slashCommandInteraction ->
                                        existingSlashCommand.getName().equals(slashCommandInteraction.getSlashCommandData().getName()) &&
                                                existingSlashCommand.getDescription().equals(slashCommandInteraction.getSlashCommandData().getDescription())
                                )
                        );
            }

            if (requiresSynchronization) {
                jdaObject.updateCommands().addCommands(slashCommandInteractionMap.values().stream()
                        .map(SlashCommandInteraction::getSlashCommandData)
                        .toList()
                ).queue(
                        success -> LOGGER.info("Successfully synchronized GLOBAL slash commands."), // TODO: Realistically, these are debug statements and should not be present in "Release" candidates.
                        failure -> LOGGER.error("Failed to synchronize GLOBAL slash commands.") // TODO: Realistically, these are debug statements and should not be present in "Release" candidates.
                );
            } else {
                LOGGER.info("GLOBAL slash commands did not require synchronization."); // TODO: Realistically, these are debug statements and should not be present in "Release" candidates.
            }
        }, failure -> {
            // TODO: If retrieveCommands() fails, the bot should probably initiate shutdown.
        });
    }

    // Commands.

    private void registerSlashCommandInteraction(SlashCommandInteraction slashCommandInteraction) {
        slashCommandInteractionMap.put(slashCommandInteraction.getSlashCommandData().getName(), slashCommandInteraction);
    }

    private void initializeSlashCommandInteractionMap() {
        registerSlashCommandInteraction(new Ping());
        registerSlashCommandInteraction(new Uptime());

        registerSlashCommandInteraction(new Beacon());
    }

    private void registerCommandAutoCompleteInteraction(CommandAutoCompleteInteraction commandAutoCompleteInteraction) {
        commandAutoCompleteInteractionMap.put(commandAutoCompleteInteraction.getSlashCommandData().getName(), commandAutoCompleteInteraction);
    }

    private void initializeCommandAutoCompleteInteractionMap() {
        registerCommandAutoCompleteInteraction(new Beacon());
    }

    private void registerMessageContextInteraction(MessageContextInteraction messageContextInteraction) {
        // TODO: Handle MessageContextInteraction.
    }

    private void initializeMessageContextInteractionMap() {
        // TODO: Implement MessageContextInteractions.
    }

    private void registerUserContextInteraction(UserContextInteraction userContextInteraction) {
        // TODO: Handle UserContextInteraction.
    }

    private void initializeUserContextInteractionMap() {
        // TODO: Implement UserContextInteractions.
    }

    // Components.

    private void registerButtonInteraction(ButtonInteraction buttonInteraction) {
        buttonInteraction.getButtonIds().forEach(buttonId ->
            buttonInteractionMap.put(buttonId, buttonInteraction)
        );
    }

    private void initializeButtonInteractionMap() {
        registerButtonInteraction(new Beacon());
    }

    private void registerEntitySelectInteraction(EntitySelectInteraction entitySelectInteraction) {
        entitySelectInteraction.getEntityIds().forEach(entityId ->
                entitySelectInteractionMap.put(entityId, entitySelectInteraction)
        );
    }

    private void initializeEntitySelectInteractionMap() {
        // TODO: Implement EntitySelectInteractions.
    }

    private void registerStringSelectInteraction(StringSelectInteraction stringSelectInteraction) {
        stringSelectInteraction.getStringIds().forEach(stringId ->
                stringSelectInteractionMap.put(stringId, stringSelectInteraction)
        );
    }

    private void initializeStringSelectInteractionMap() {
        // TODO: Implement StringSelectInteractions.
    }

    // Modals.

    private void registerModalInteraction(ModalInteraction modalInteraction) {
        modalInteraction.getModalIds().forEach(modalId ->
            modalInteractionEventMap.put(modalId, modalInteraction)
        );
    }

    private void initializeModalInteractionMap() {
        // TODO: Implement ModalInteractions.
    }

    // Commands.

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent slashCommandInteractionEvent) {
        slashCommandInteractionMap.get(slashCommandInteractionEvent.getName()).handleSlashCommandInteraction(slashCommandInteractionEvent);
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent commandAutoCompleteInteractionEvent) {
        commandAutoCompleteInteractionMap.get(commandAutoCompleteInteractionEvent.getName()).handleCommandAutoCompleteInteraction(commandAutoCompleteInteractionEvent);
    }

    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent messageContextInteractionEvent) {
        // TODO: Handle MessageContextInteractionEvents.
    }

    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent userContextInteractionEvent) {
        // TODO: Handle UserContextInteractionEvents.
    }

    // Components.

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent buttonInteractionEvent) {
        buttonInteractionMap.get(buttonInteractionEvent.getComponentId()).handleButtonInteraction(buttonInteractionEvent);
    }

    @Override
    public void onEntitySelectInteraction(@NotNull EntitySelectInteractionEvent entitySelectInteractionEvent) {
        entitySelectInteractionMap.get(entitySelectInteractionEvent.getComponentId()).handleEntitySelectInteraction(entitySelectInteractionEvent);
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent stringSelectInteractionEvent) {
        stringSelectInteractionMap.get(stringSelectInteractionEvent.getComponentId()).handleStringSelectInteraction(stringSelectInteractionEvent);
    }

    // Modals.

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent modalInteractionEvent) {
        modalInteractionEventMap.get(modalInteractionEvent.getModalId()).handleModalInteraction(modalInteractionEvent);
    }
}
