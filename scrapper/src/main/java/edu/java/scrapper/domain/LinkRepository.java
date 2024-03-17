package edu.java.scrapper.domain;

import edu.java.scrapper.models.Link;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface LinkRepository {
    List<Link> findAll();

    Link add(URI uri);

    int remove(URI uri);

    Optional<Link> findLink(URI url);

    void updateLink(Long linkID, OffsetDateTime checkTime, OffsetDateTime updatedAt);
}
