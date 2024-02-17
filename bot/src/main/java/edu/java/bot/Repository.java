package edu.java.bot;

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

    public static boolean remove(Long chatId, String link) {
        register(chatId);
        return links.get(chatId).remove(link);
    }

    public static List<String> getLinks(Long chatId) {
        Set<String> linksToUser = links.get(chatId);
        if (linksToUser != null) {
            return linksToUser.stream().toList();
        }
        return null;
    }
}
