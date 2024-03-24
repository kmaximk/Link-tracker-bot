package edu.java.scrapper.service;

import java.util.List;

public interface TgChatService {
    void register(long tgChatId);

    void unregister(long tgChatId);

    List<Long> getChatsByLink(Long linkID);
}
