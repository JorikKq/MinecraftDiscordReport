package com.example.minecraftdiscordreports;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MessageListener extends ListenerAdapter {
    private final PunishmentManager punishmentManager;

    public MessageListener(PunishmentManager punishmentManager) {
        this.punishmentManager = punishmentManager;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (!event.isFromGuild()) return; // Игнорируем личные сообщения

        String userId = event.getAuthor().getId();
        String message = event.getMessage().getContentRaw();

        // Проверяем, есть ли ожидающее наказание для этого пользователя
        punishmentManager.processPunishment(userId, message);
    }
}