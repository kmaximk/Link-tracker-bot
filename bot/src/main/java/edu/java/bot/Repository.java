package edu.java.bot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class Repository {
    private static final Map<Long, Set<String>> LINKS = new HashMap<>();

    public void register(Long chatId) {
        LINKS.computeIfAbsent(chatId, k -> new HashSet<>());
    }

    public void put(Long chatId, String link) {
        register(chatId);
        LINKS.get(chatId).add(link);
    }

    public boolean remove(Long chatId, String link) {
        register(chatId);
        return LINKS.get(chatId).remove(link);
    }

    public List<String> getLinks(Long chatId) {
        Set<String> linksToUser = LINKS.get(chatId);
        if (linksToUser != null) {
            return linksToUser.stream().toList();
        }
        return null;
    }
}
