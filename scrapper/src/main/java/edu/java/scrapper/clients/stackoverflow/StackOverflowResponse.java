package edu.java.scrapper.clients.stackoverflow;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.OffsetDateTime;

@JsonDeserialize(using = StackOverflowDeserializer.class)
public record StackOverflowResponse(
    OffsetDateTime lastActivityDate,

    OffsetDateTime creationDate,

    OffsetDateTime lastEditDate,

    Integer answerCount) {

}
