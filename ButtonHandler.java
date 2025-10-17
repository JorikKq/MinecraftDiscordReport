package com.example.minecraftdiscordreports;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.jetbrains.annotations.NotNull;
import java.awt.Color;
import java.util.Collections;

public class ButtonHandler extends ListenerAdapter {
    private final Main plugin;
    private final ReportManager reportManager;
    private final ConfigManager configManager;

    public ButtonHandler(Main plugin, ReportManager reportManager, ConfigManager configManager) {
        this.plugin = plugin;
        this.reportManager = reportManager;
        this.configManager = configManager;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String[] buttonData = event.getButton().getId().split(":");
        String action = buttonData[0];
        String reporter = buttonData[1];
        String target = buttonData[2];

        MessageEmbed originalEmbed = event.getMessage().getEmbeds().get(0);
        EmbedBuilder embed = new EmbedBuilder(originalEmbed);
        String moderatorTag = event.getUser().getAsTag();

        switch (action) {
            case "accept":
                handleAccept(event, embed, reporter, target, moderatorTag);
                break;

            case "deny":
                handleDeny(event, embed, reporter, target, moderatorTag);
                break;

            case "approve":
                handleApprove(event, embed, reporter, target, moderatorTag);
                break;

            case "mute":
            case "ban":
                handlePunishment(event, action, reporter, target, moderatorTag);
                break;
        }
    }

    private void handleAccept(ButtonInteractionEvent event, EmbedBuilder embed, String reporter, String target, String moderatorTag) {
        embed.getFields().set(3, new MessageEmbed.Field("Статус", "В процессе", true));
        embed.getFields().set(4, new MessageEmbed.Field("Принято", moderatorTag, true));
        embed.setColor(configManager.getProgressColor());

        // Кнопки для наказаний
        ActionRow punishmentButtons = ActionRow.of(
                Button.secondary("mute:" + reporter + ":" + target + ":" + moderatorTag, "Мут")
                        .withEmoji(Emoji.fromUnicode("🔇")),
                Button.danger("ban:" + reporter + ":" + target + ":" + moderatorTag, "Бан")
                        .withEmoji(Emoji.fromUnicode("🔨")),
                Button.primary("approve:" + reporter + ":" + target + ":" + moderatorTag, "Только одобрить")
                        .withEmoji(Emoji.fromUnicode("✅"))
        );

        event.editMessageEmbeds(embed.build())
                .setComponents(punishmentButtons)
                .queue();

        // Уведомление игроку
        plugin.getDiscordBot().notifyPlayer(reporter, configManager.getInProgressMessage(moderatorTag));
    }

    private void handleDeny(ButtonInteractionEvent event, EmbedBuilder embed, String reporter, String target, String moderatorTag) {
        embed.getFields().set(3, new MessageEmbed.Field("Статус", "Отклонено", true));
        embed.getFields().set(4, new MessageEmbed.Field("Принято", moderatorTag, true));
        embed.setColor(configManager.getDeniedColor());

        event.editMessageEmbeds(embed.build())
                .setComponents(Collections.emptyList())
                .queue();

        // Уведомление игроку
        plugin.getDiscordBot().notifyPlayer(reporter, configManager.getDeniedMessage(moderatorTag));
    }

    private void handleApprove(ButtonInteractionEvent event, EmbedBuilder embed, String reporter, String target, String moderatorTag) {
        embed.getFields().set(3, new MessageEmbed.Field("Статус", "Одобрено", true));
        embed.getFields().set(4, new MessageEmbed.Field("Принято", moderatorTag, true));
        embed.setColor(configManager.getApprovedColor());

        event.editMessageEmbeds(embed.build())
                .setComponents(Collections.emptyList())
                .queue();

        // Уведомление игроку
        plugin.getDiscordBot().notifyPlayer(reporter, configManager.getApprovedMessage(moderatorTag));
    }

    private void handlePunishment(ButtonInteractionEvent event, String action, String reporter, String target, String moderatorTag) {
        // Отправляем сообщение модератору с инструкцией в личные сообщения
        event.getUser().openPrivateChannel().queue(channel -> {
            channel.sendMessage("**Введите параметры наказания:**\n" +
                    "Формат: `[время] [причина]`\n" +
                    "Пример: `30m Капс`\n" +
                    "Наказание: " + (action.equals("mute") ? "мут" : "бан") + " для " + target +
                    "\n\n*Сообщение будет отправлено от имени консоли*").queue();
        });

        // Сохраняем данные для обработки текстового сообщения
        plugin.getPunishmentManager().setPendingPunishment(
                event.getUser().getId(),
                action,
                target,
                moderatorTag
        );

        event.reply("✅ Инструкции отправлены в личные сообщения!")
                .setEphemeral(true)
                .queue();
    }
}