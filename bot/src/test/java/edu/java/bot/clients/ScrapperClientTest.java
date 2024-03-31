package edu.java.bot.clients;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.dto.AddLinkRequest;
import edu.java.dto.LinkResponse;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WireMockTest
@SpringBootTest
public class ScrapperClientTest {

    @RegisterExtension
    private static WireMockExtension wireMockExtension =
        WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build();

    private ScrapperClient scrapperClient;

    @Autowired
    private RetryTemplate retryTemplate;

    @BeforeEach
    public void setup() {
        scrapperClient = new ScrapperClient(WebClient.builder(), wireMockExtension.baseUrl(), retryTemplate);
    }

    @Test
    public void postLinksTest() {
        Long tgChatId = 111L;
        URI uri = URI.create("http://localhost:9191");
        wireMockExtension.stubFor(post("/links").withHeader("Tg-Chat-Id", equalTo("111")).withRequestBody(
                equalToJson("""
                    {
                      "link": "http://localhost:9191"
                    }"""))
            .willReturn(okJson("""
                    {
                        "id": 111,
                        "url": "http://localhost:9191"
                      }""")));
        LinkResponse resp = scrapperClient.postLinks(tgChatId, new AddLinkRequest(uri));
        assertEquals(111, resp.id());
        assertEquals(uri, resp.url());
    }
}
