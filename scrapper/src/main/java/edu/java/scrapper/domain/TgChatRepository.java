package edu.java.scrapper.domain;

public interface TgChatRepository {
    boolean containsChat(Long tgChatID);

    void add(long chatID);

    int remove(long chatID);

    void removeLinksByChat(long chatID);
}
