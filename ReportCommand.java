package com.example.minecraftdiscordreports;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportCommand implements CommandExecutor {
    private final DiscordBot discordBot;

    public ReportCommand(DiscordBot discordBot) {
        this.discordBot = discordBot;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Только игроки могут использовать эту команду!");
            return true;
        }

        Player player = (Player) sender;
        if (args.length < 2) {
            player.sendMessage("Использование: /report <ник> <причина>");
            return true;
        }

        String target = args[0];
        String reason = String.join(" ", args).substring(target.length() + 1);

        discordBot.sendReport(player.getName(), target, reason);
        player.sendMessage("Ваш репорт отправлен!");
        return true;
    }
}