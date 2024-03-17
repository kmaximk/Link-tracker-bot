package edu.java.scrapper.domain;

import edu.java.scrapper.models.Link;
import java.util.List;

public interface AssignmentRepository {
    List<Long> findChatsByLink(Long linkID);

    List<Link> findLinksByChat(Long chatID);

    void add(Long linkID, Long chatID);

    int remove(Long linkID, Long chatID);

    boolean linkIsTracked(Link link, Long userID);

    List<Link> getOutdatedLinks(Long interval);
}
