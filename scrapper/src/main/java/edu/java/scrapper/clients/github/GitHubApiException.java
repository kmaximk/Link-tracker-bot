package edu.java.scrapper.clients.github;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;

@RequiredArgsConstructor
@Getter
public class GitHubApiException extends RuntimeException {
    private final HttpStatusCode statusCode;
}
