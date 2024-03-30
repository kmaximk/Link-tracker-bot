package edu.java.scrapper.domain;

import edu.java.scrapper.models.LinkModel;
import java.util.List;

public interface AssignmentRepository {
    List<Long> findChatsByLink(Long linkID);

    List<LinkModel> findLinksByChat(Long chatID);

    void add(Long linkID, Long chatID);

    int remove(Long linkID, Long chatID);

    boolean linkIsTracked(LinkModel link, Long userID);

    List<LinkModel> getOutdatedLinks(Long interval);
}
