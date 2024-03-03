package edu.java.bot.clients;

import edu.java.dto.ApiErrorResponse;
import lombok.Getter;

@Getter
public class ScrapperResponseException extends RuntimeException {
    private final ApiErrorResponse apiErrorResponse;

    public ScrapperResponseException(String message, ApiErrorResponse apiErrorResponse) {
        super(message);
        this.apiErrorResponse = apiErrorResponse;
    }
}
