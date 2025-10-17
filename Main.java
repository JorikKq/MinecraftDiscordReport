package com.example.minecraftdiscordreports;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private DiscordBot discordBot;
    private ConfigManager configManager;
    private ReportManager reportManager;
    private PunishmentManager punishmentManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        reportManager = new ReportManager();

        try {
            discordBot = new DiscordBot(this, configManager, reportManager);
            punishmentManager = new PunishmentManager(this, discordBot.getJDA());

            // Регистрируем слушатель сообщений
            discordBot.getJDA().addEventListener(new MessageListener(punishmentManager));

            getLogger().info("Discord бот подключен!");
        } catch (Exception e) {
            getLogger().severe("Ошибка Discord бота: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getCommand("report").setExecutor(new ReportCommand(discordBot));
        getLogger().info("Плагин запущен!");
    }

    @Override
    public void onDisable() {
        if (discordBot != null) {
            discordBot.shutdown();
        }
        getLogger().info("Плагин отключен.");
    }

    public DiscordBot getDiscordBot() {
        return discordBot;
    }

    public PunishmentManager getPunishmentManager() {
        return punishmentManager;
    }
}