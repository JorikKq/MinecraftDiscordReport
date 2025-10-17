package com.example.minecraftdiscordreports;

import net.dv8tion.jda.api.JDA;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.HashMap;
import java.util.Map;

public class PunishmentManager {
    private final Main plugin;
    private final JDA jda;
    private final Map<String, PunishmentData> pendingPunishments = new HashMap<>();

    public PunishmentManager(Main plugin, JDA jda) {
        this.plugin = plugin;
        this.jda = jda;
    }

    public void setPendingPunishment(String userId, String action, String target, String moderator) {
        pendingPunishments.put(userId, new PunishmentData(action, target, moderator));
    }

    public void processPunishment(String userId, String message) {
        PunishmentData data = pendingPunishments.get(userId);
        if (data == null) return;

        String[] parts = message.split(" ", 2);
        if (parts.length < 2) {
            jda.retrieveUserById(userId).queue(user -> {
                user.openPrivateChannel().queue(channel -> {
                    channel.sendMessage("❌ Неверный формат! Используйте: `[время] [причина]`").queue();
                });
            });
            return;
        }

        String time = parts[0];
        String reason = parts[1] + " &7&lby " + data.moderator;

        String command;
        if (data.action.equals("mute")) {
            command = "mute " + data.target + " " + time + " " + reason;
        } else {
            command = "ban " + data.target + " " + time + " " + reason;
        }

        final String finalCommand = ChatColor.translateAlternateColorCodes('&', command);

        // Выполняем команду в синхронном потоке Minecraft
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
            }
        }.runTask(plugin);

        // Уведомляем модератора
        jda.retrieveUserById(userId).queue(user -> {
            user.openPrivateChannel().queue(channel -> {
                channel.sendMessage("✅ Наказание успешно применено: `" + finalCommand + "`").queue();
            });
        });

        pendingPunishments.remove(userId);
    }

    private static class PunishmentData {
        public final String action;
        public final String target;
        public final String moderator;

        public PunishmentData(String action, String target, String moderator) {
            this.action = action;
            this.target = target;
            this.moderator = moderator;
        }
    }
}