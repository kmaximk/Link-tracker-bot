package edu.java.scrapper;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import edu.java.scrapper.github.GitHubResponse;
import edu.java.scrapper.github.GitHubClient;

@WireMockTest
@SpringBootTest
public class GitHubClientTest {

    @RegisterExtension
    private static WireMockExtension wireMockExtension =
        WireMockExtension.newInstance().options(wireMockConfig().port(90)).build();

    @Autowired
    GitHubClient gitHubClient;

    @Test
    public void getUpdatesTest() {
        wireMockExtension.stubFor(get("/questions/30080855?site=stackoverflow")
            .willReturn(okJson("""
                    {
                      "id": 1296269,
                      "node_id": "MDEwOlJlcG9zaXRvcnkxMjk2MjY5",
                      "name": "Hello-World",
                      "full_name": "octocat/Hello-World",
                      "private": false,
                      "created_at": "2011-01-26T19:01:12Z",
                      "updated_at": "2024-02-23T19:58:00Z",
                      "pushed_at": "2024-02-23T12:43:59Z"
                    }""")));
        GitHubResponse response = gitHubClient.getRepositoryInfo("octocat", "Hello-World");

        assertEquals(OffsetDateTime.parse("2024-02-23T19:58:00Z"), response.updatedAt());
        assertEquals(OffsetDateTime.parse("2011-01-26T19:01:12Z"), response.createdAt());
        assertEquals(OffsetDateTime.parse("2024-02-23T12:43:59Z"), response.pushedAt());
    }
}
