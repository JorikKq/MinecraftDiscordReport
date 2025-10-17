package com.example.minecraftdiscordreports;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.awt.Color;

public class ConfigManager {
    private final JavaPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        reloadConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public String getBotToken() {
        return config.getString("discord.bot-token", "").trim();
    }

    public String getGuildId() {
        return config.getString("discord.guild-id", "").trim();
    }

    public String getReportChannelId() {
        return config.getString("discord.report-channel-id", "").trim();
    }

    public Color getPendingColor() {
        return Color.decode(config.getString("colors.pending", "#FFA500"));
    }

    public Color getProgressColor() {
        return Color.decode(config.getString("colors.progress", "#0000FF"));
    }

    public Color getDeniedColor() {
        return Color.decode(config.getString("colors.denied", "#FF0000"));
    }

    public Color getApprovedColor() {
        return Color.decode(config.getString("colors.approved", "#00FF00"));
    }

    public String getInProgressMessage(String moderator) {
        return config.getString("messages.in-progress", "&8[&6⌛&8] &eЖалоба была взята на рассмотрения, ожидайте ответа.")
                .replace("%moderator%", moderator);
    }

    public String getApprovedMessage(String moderator) {
        return config.getString("messages.approved", "&8[&a✔&8] &2Жалоба была принята модератором&a: %moderator%")
                .replace("%moderator%", moderator);
    }

    public String getDeniedMessage(String moderator) {
        return config.getString("messages.denied", "&8[&c✖&8] &4Жалоба была отказана модератором&c: %moderator%")
                .replace("%moderator%", moderator);
    }
}