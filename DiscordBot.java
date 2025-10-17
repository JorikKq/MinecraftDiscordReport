package com.example.minecraftdiscordreports;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.awt.Color;
import java.time.Instant;
import java.util.UUID;
import javax.security.auth.login.LoginException;

public class DiscordBot {
    private final JDA jda;
    private final ConfigManager configManager;
    private final Main plugin;

    public DiscordBot(Main plugin, ConfigManager configManager, ReportManager reportManager) throws LoginException {
        this.plugin = plugin;
        this.configManager = configManager;
        this.jda = JDABuilder.createDefault(configManager.getBotToken())
                .enableIntents(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MEMBERS
                )
                .addEventListeners(new ButtonHandler(plugin, reportManager, configManager))
                .build();

        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Bot initialization interrupted", e);
        }
    }

    public JDA getJDA() {
        return jda;
    }

    public void sendReport(String reporter, String target, String reason) {
        TextChannel channel = jda.getTextChannelById(configManager.getReportChannelId());
        if (channel == null) {
            System.err.println("Канал для репортов не найден!");
            return;
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Новый репорт")
                .setColor(configManager.getPendingColor())
                .addField("Отправитель", reporter, true)
                .addField("Нарушитель", target, true)
                .addField("Причина", reason, false)
                .addField("Статус", "Ожидание", true)
                .addField("Принято", "Никем", true)
                .setFooter("ID: " + UUID.randomUUID().toString().substring(0, 6))
                .setTimestamp(Instant.now());

        ActionRow buttons = ActionRow.of(
                Button.primary("accept:" + reporter + ":" + target, "Принять")
                        .withEmoji(Emoji.fromUnicode("✅"))
        );

        channel.sendMessageEmbeds(embed.build())
                .setComponents(buttons)
                .queue();
    }

    public void notifyPlayer(String playerName, String message) {
        Player player = Bukkit.getPlayer(playerName);
        if (player != null && player.isOnline()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    public void shutdown() {
        if (jda != null) {
            jda.shutdown();
        }
    }
}