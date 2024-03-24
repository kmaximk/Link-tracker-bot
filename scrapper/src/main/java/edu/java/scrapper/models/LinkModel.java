package edu.java.scrapper.models;

import java.net.URI;
import java.time.OffsetDateTime;

public record LinkModel(
    Long id,
    URI url,
    OffsetDateTime lastCheckTime,
    OffsetDateTime updatedAt,

    Integer updatesCount
) {
}
