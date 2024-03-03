package edu.java.scrapper.clients.stackoverflow;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.time.OffsetDateTime;

public class StackOverflowDeserializer extends JsonDeserializer<StackOverflowResponse> {
    @Override
    public StackOverflowResponse deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
        throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        JsonNode root = (JsonNode) jsonParser.getCodec().readTree(jsonParser).get("items");
        JsonNode node = root.get(0);
        OffsetDateTime lastActivityDate = mapper.convertValue(node.get("last_activity_date"), OffsetDateTime.class);
        OffsetDateTime creationDate = mapper.convertValue(node.get("creation_date"), OffsetDateTime.class);
        OffsetDateTime lastEditDate = mapper.convertValue(node.get("last_edit_date"), OffsetDateTime.class);
        Integer answerCount = mapper.convertValue(node.get("answer_count"), Integer.class);
        return new StackOverflowResponse(lastActivityDate, creationDate, lastEditDate, answerCount);
    }
}
