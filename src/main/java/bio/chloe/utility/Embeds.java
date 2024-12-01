package bio.chloe.utility;

import bio.chloe.configuration.Configuration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class Embeds {
    private static final Configuration CONFIGURATION = Configuration.getInstance();

    private static final Color INFO_COLOR = Color.decode(CONFIGURATION.optString("infoColor", "#3FFF3F"));
    private static final Color WARN_COLOR = Color.decode(CONFIGURATION.optString("warnColor", "#FFFF7F"));
    private static final Color ERROR_COLOR = Color.decode(CONFIGURATION.optString("errorColor", "#FF3F3F"));
    private static final Color DEBUG_COLOR = Color.decode(CONFIGURATION.optString("debugColor", "#3F3FFF"));

    private static MessageEmbed createEmbed(String message, Color color) {
        return new EmbedBuilder()
                .setDescription(message)
                .setColor(color)
                .build();
    }

    public static MessageEmbed infoEmbed(String infoMessage) {
        return createEmbed(infoMessage, INFO_COLOR);
    }

    public static MessageEmbed warnEmbed(String warnMessage) {
        return createEmbed(warnMessage, WARN_COLOR);
    }

    public static MessageEmbed errorEmbed(String errorMessage) {
        return createEmbed(errorMessage, ERROR_COLOR);
    }

    public static MessageEmbed debugEmbed(String debugMessage) {
        return createEmbed(debugMessage, DEBUG_COLOR);
    }
}
