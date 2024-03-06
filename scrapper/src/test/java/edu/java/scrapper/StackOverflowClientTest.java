package edu.java.scrapper;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import edu.java.scrapper.clients.stackoverflow.StackOverflowClient;
import edu.java.scrapper.clients.stackoverflow.StackOverflowResponse;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.web.reactive.function.client.WebClient;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StackOverflowClientTest {

    @RegisterExtension
    private static WireMockExtension wireMockExtension =
        WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build();

    private StackOverflowClient stackOverflowClient;
    @BeforeEach
    public void setup() {
        stackOverflowClient = new StackOverflowClient(WebClient.builder(), wireMockExtension.baseUrl());
    }

    @Test
    public void getUpdatesTest() {
        wireMockExtension.stubFor(get("/questions/30080855?site=stackoverflow")
            .willReturn(okJson("""
                {
                  "items": [
                    {
                      "tags": [
                        "spring",
                        "spring-boot",
                        "rest"
                      ],
                      "owner": {
                        "account_id": 4810852,
                        "reputation": 505,
                        "user_id": 3884401,
                        "user_type": "registered",
                        "accept_rate": 50,
                        "profile_image": "https://www.gravatar.com/avatar/c0a84baff139fda62a9bdb979e9d38df?s=256&d=identicon&r=PG&f=y&so-version=2",
                        "display_name": "Cheps",
                        "link": "https://stackoverflow.com/users/3884401/cheps"
                      },
                      "is_answered": true,
                      "view_count": 28086,
                      "accepted_answer_id": 30081247,
                      "answer_count": 6,
                      "score": 36,
                      "last_activity_date": 1661178318,
                      "creation_date": 1430925349,
                      "last_edit_date": 1616369850,
                      "question_id": 30080855,
                      "content_license": "CC BY-SA 4.0",
                      "link": "https://stackoverflow.com/questions/30080855/difference-between-spring-and-spring-boot",
                      "title": "Difference between Spring and Spring Boot"
                    }
                  ],
                  "has_more": false,
                  "quota_max": 10000,
                  "quota_remaining": 9900
                }""")));
        StackOverflowResponse response = stackOverflowClient.getQuestionUpdate("30080855");

        assertEquals(6, response.answerCount());
        assertEquals(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1430925349), ZoneOffset.UTC), response.creationDate());
        assertEquals(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1661178318), ZoneOffset.UTC), response.lastActivityDate());
        assertEquals(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1616369850), ZoneOffset.UTC), response.lastEditDate());

    }
}
