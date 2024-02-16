package edu.java.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.response.SendResponse;
import edu.java.bot.model.Command;
import edu.java.bot.processor.UserMessageProcessor;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Repository {
    private static final Map<Long, Set<String>> links = new HashMap<>();

    public static void register(Long chatId) {
        links.computeIfAbsent(chatId, k -> new HashSet<>());
    }
    public static void put(Long chatId, String link) {
        register(chatId);
        links.get(chatId).add(link);
    }

    public static void remove(Long chatId, String link) {
        register(chatId);
        boolean res = links.get(chatId).remove(link);
    }

    public static List<String> getLinks(Long chatId) {
        Set<String> linksToUser = links.get(chatId);
        if (linksToUser != null) {
            return linksToUser.stream().toList();
        }
        return null;
    }
}
