package bio.chloe;

import bio.chloe.configuration.Configuration;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

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
        } catch (InvalidTokenException invalidTokenException) {
            LOGGER.error("InvalidTokenException occurred whilst initializing JDA.");

            System.exit(-1); // TODO: Implement well-documented exit codes.
        }
    }
}
