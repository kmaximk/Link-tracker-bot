package edu.java.scrapper.clients.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record GitHubResponse(
    @JsonProperty("created_at")
    OffsetDateTime createdAt,

    @JsonProperty("updated_at")
    OffsetDateTime updatedAt,

    @JsonProperty("pushed_at")
    OffsetDateTime pushedAt,

    @JsonProperty("open_issues_count")
    Integer openIssuesCount
) {
}
