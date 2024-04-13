package edu.java.bot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.dto.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserNotifier {

    private final TelegramBot bot;

    public void sendUpdateToUser(LinkUpdateRequest linkUpdate) {
        linkUpdate.tgChatIds().forEach(chat -> bot.execute(new SendMessage(chat, linkUpdate.description())));
    }
}
