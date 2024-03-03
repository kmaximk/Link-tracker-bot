package edu.java.scrapper.clients.botclient;

import edu.java.dto.ApiErrorResponse;
import lombok.Getter;

@Getter
public class BotResponseException extends RuntimeException {
    private final ApiErrorResponse apiErrorResponse;

    public BotResponseException(String message, ApiErrorResponse apiErrorResponse) {
        super(message);
        this.apiErrorResponse = apiErrorResponse;
    }
}
