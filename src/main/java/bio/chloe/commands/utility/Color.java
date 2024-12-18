package bio.chloe.commands.utility;

import bio.chloe.interfaces.interactions.SlashCommandInteraction;
import bio.chloe.utility.Embeds;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.FileUpload;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

public class Color implements SlashCommandInteraction {
    @Override
    public void handleSlashCommandInteraction(SlashCommandInteractionEvent slashCommandInteractionEvent) {
        slashCommandInteractionEvent.deferReply(true).queue();

        final String HEXADECIMAL_COLOR = slashCommandInteractionEvent.getOption("hex-color", "", OptionMapping::getAsString);

        if (HEXADECIMAL_COLOR.matches("^#([A-Fa-f0-9]{6})$")) {
            final java.awt.Color COLOR = java.awt.Color.decode(HEXADECIMAL_COLOR);

            BufferedImage bufferedImage = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);

            Graphics2D graphics2D = bufferedImage.createGraphics();

            graphics2D.setColor(COLOR);
            graphics2D.fillRect(0, 0, 128, 128);
            graphics2D.dispose();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            try {
                ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
            } catch(IOException ioException) {
                slashCommandInteractionEvent.getHook().editOriginalEmbeds(Embeds.errorEmbed(String.format(
                        "Hexadecimal color code `%s` could not be properly generated.", HEXADECIMAL_COLOR
                ))).queue();

                return;
            }

            byte[] imageDataByteArray = byteArrayOutputStream.toByteArray();

            String fileName = HEXADECIMAL_COLOR.substring(1).toUpperCase(Locale.ENGLISH) + ".png";

            MessageEmbed colorMessageEmbed = new EmbedBuilder()
                    .setTitle(HEXADECIMAL_COLOR.toUpperCase())
                    .setDescription(String.format("**RGB (%d, %d, %d)**", COLOR.getRed(), COLOR.getGreen(), COLOR.getBlue()))
                    .setImage("attachment://" + fileName)
                    .setColor(COLOR)
                    .build();

            // Send the embed and file in a single message payload
            slashCommandInteractionEvent.getHook()
                    .sendFiles(FileUpload.fromData(imageDataByteArray, fileName))
                    .addEmbeds(colorMessageEmbed)
                    .queue();

        } else {
            slashCommandInteractionEvent.getHook().editOriginalEmbeds(Embeds.errorEmbed(String.format(
                    "Hexadecimal color code `%s` is invalid or malformed.", HEXADECIMAL_COLOR
            ))).queue();
        }
    }

    @Override
    public SlashCommandData getSlashCommandData() {
        final OptionData HEX_CODE = new OptionData(OptionType.STRING, "hex-color", "A hexadecimal color code representing RGB (0-255) (e.g., #ABC123).")
                .setMinLength(7).setMaxLength(7).setRequired(true).setAutoComplete(false);

        return Commands.slash("color", "Generate a color swathe based on a hexadecimal color code.")
                .addOptions(HEX_CODE);
    }
}
