package edu.java.dto;

import java.util.List;

public record ApiErrorResponse(
    String description,

    Integer code,

    String exceptionName,

    String exceptionMessage,

    List<String> stacktrace
) {
}
