package edu.java.scrapper.clients.stackoverflow;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;

@RequiredArgsConstructor
@Getter
public class StackOverflowApiException extends RuntimeException {
    private final HttpStatusCode statusCode;
}
