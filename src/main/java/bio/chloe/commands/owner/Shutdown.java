package bio.chloe.commands.owner;

import bio.chloe.configuration.Configuration;
import bio.chloe.interfaces.interactions.SlashCommandInteraction;
import bio.chloe.managers.DatabaseManager;
import bio.chloe.utility.Embeds;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class Shutdown implements SlashCommandInteraction {
    // Lazily load the SLF4J logger to prevent unnecessary memory allocation for an unused logger.
    private static final Supplier<Logger> LOGGER = () -> LoggerFactory.getLogger(Configuration.class);

    @Override
    public void handleSlashCommandInteraction(SlashCommandInteractionEvent slashCommandInteractionEvent) {
        slashCommandInteractionEvent.deferReply(true).queue();

        final Configuration CONFIGURATION_INSTANCE = Configuration.getInstance();

        if (slashCommandInteractionEvent.getUser().getIdLong() != CONFIGURATION_INSTANCE.optLong("ownerUserId", 0L)) {
            slashCommandInteractionEvent.getHook().editOriginalEmbeds(Embeds.errorEmbed(
                    "You do not have permission to use this command."
            )).queue(); return;
        }

        slashCommandInteractionEvent.getHook().editOriginalEmbeds(Embeds.infoEmbed(
                "Initiating graceful shutdown procedures."
        )).queue(success -> {
            LOGGER.get().info("Initiating graceful shutdown procedures.");

            DatabaseManager.closeDatabaseManager();

            JDA jdaObject = slashCommandInteractionEvent.getJDA();

            jdaObject.shutdown();

            // TODO: What if graceful shutdown fails? awaitShutdown() causes issues because of the fact that this
            // TODO: is a command, and thus is tied to JDA itself.

            LOGGER.get().info("Graceful JDA shutdown successful, exiting the JVM.");

            System.exit(0); // TODO: Implement well-documented exit codes.
        });
    }

    @Override
    public SlashCommandData getSlashCommandData() {
        return Commands.slash("shutdown", "Initiate the Bot's shutdown procedures.")
                .setGuildOnly(true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR));
    }
}
