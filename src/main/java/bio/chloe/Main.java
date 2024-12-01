package bio.chloe;

import bio.chloe.configuration.Configuration;
import bio.chloe.handlers.SlashCommandHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Scanner;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            Configuration.initializeConfiguration(new File("Configuration.json"));
        } catch (IOException configurationInitializationException) {
            LOGGER.error("IOException occurred whilst initializing the configuration.");

            System.exit(-1); // TODO: Implement well-documented exit codes.
        }

        Configuration configurationInstance = Configuration.getInstance();

        try {
            JDA jdaObject = JDABuilder.createDefault(
                    configurationInstance.optString("botToken", null)
            ).build();

            jdaObject.addEventListener(new SlashCommandHandler(jdaObject));

            awaitShutdown(jdaObject);

            System.exit(0); // TODO: Implement well-documented exit codes.
        } catch (InvalidTokenException invalidTokenException) {
            LOGGER.error("InvalidTokenException occurred whilst initializing JDA.");

            System.exit(-1); // TODO: Implement well-documented exit codes.
        }
    }

    private static void awaitShutdown(JDA jdaObject) {
        Scanner terminalScanner = new Scanner(System.in);

        while (terminalScanner.hasNextLine()) {
            String terminalCommand = terminalScanner.nextLine();

            if (terminalCommand.equalsIgnoreCase("shutdown")) {
                shutdown(jdaObject);

                break; // Once JDA has shut down, return to main().
            } else {
                LOGGER.error("Unknown terminal command entered, type \"shutdown\" to shutdown.");
            }
        }
    }

    private static void shutdown(JDA jdaObject) {
        LOGGER.info("Initializing JDA graceful shutdown procedures.");

        jdaObject.shutdown();

        try {
            if (!jdaObject.awaitShutdown(Duration.ofMinutes(5))) {
                LOGGER.warn("JDA took more than five (5) minutes to shut down gracefully, forcing shutdown.");

                jdaObject.shutdownNow();

                if (jdaObject.awaitShutdown(Duration.ofMinutes(5))) {
                    LOGGER.error("JDA took more than five (5) minutes to forcefully shut down. Terminating process.");

                    System.exit(-1); // TODO: Implement well-documented exit codes.
                }
            }
        } catch (InterruptedException threadInterruptedException) {
            LOGGER.error("InterruptedException thrown whilst awaiting JDA shutdown (thread interruption).");

            System.exit(-1); // TODO: Implement well-documented exit codes.
        }

        LOGGER.info("Graceful JDA shutdown successful, exiting the JVM.");
    }
}
