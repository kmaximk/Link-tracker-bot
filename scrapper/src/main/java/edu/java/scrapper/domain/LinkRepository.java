package edu.java.scrapper.domain;

import edu.java.scrapper.models.LinkModel;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface LinkRepository {
    List<LinkModel> findAll();

    LinkModel add(URI uri, OffsetDateTime updatedAt, Integer updatesCount);

    int remove(URI uri);

    Optional<LinkModel> findLink(URI url);

    void updateLink(Long linkID, OffsetDateTime checkTime, OffsetDateTime updatedAt, Integer updatesCount);
}
