package edu.java.scrapper.service;

import edu.java.scrapper.models.LinkModel;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

public interface LinkService {
    LinkModel add(long tgChatId, URI url);

    LinkModel remove(long tgChatId, URI url);

    List<LinkModel> listAll(long tgChatId);

    void updateLink(
        LinkModel link, OffsetDateTime checkTime, OffsetDateTime updatedAt,
        Integer updatesCount
    );
}
