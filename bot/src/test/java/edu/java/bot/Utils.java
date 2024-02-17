package edu.java.bot;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import static org.mockito.Mockito.when;

public class Utils {

    public static void fillMockChatId(Update mockUpdate, Message mockMessage, Chat mockChat, Long chatId) {
        when(mockUpdate.message()).thenReturn(mockMessage);
        when(mockMessage.chat()).thenReturn(mockChat);
        when(mockChat.id()).thenReturn(chatId);
    }

    public static void fillMockText(Update mockUpdate, Message mockMessage, String text) {
        when(mockUpdate.message()).thenReturn(mockMessage);
        when(mockMessage.text()).thenReturn(text);
    }
}
