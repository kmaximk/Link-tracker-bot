package edu.java.scrapper.domain;

import java.net.URI;
import java.util.List;

public interface TgChatRepository {
    List<Long> findAll();

    void add(long chatID);

    int remove(long chatID);
}
