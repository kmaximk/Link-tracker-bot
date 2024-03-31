package edu.java.bot.clients;

import edu.java.dto.AddLinkRequest;
import edu.java.dto.ApiErrorResponse;
import edu.java.dto.LinkResponse;
import edu.java.dto.ListLinksResponse;
import edu.java.dto.RemoveLinkRequest;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class ScrapperClient {

    private static final String LINKS = "/links";

    private static final String TG_CHAT_ID = "Tg-Chat-Id";

    private final WebClient webClient;

    private final RetryTemplate retryTemplate;

    public ScrapperClient(WebClient.Builder webClientBuilder, String scrapperBaseUrl, RetryTemplate retryTemplate) {
        this.retryTemplate = retryTemplate;
        this.webClient = webClientBuilder.baseUrl(scrapperBaseUrl).build();

    }

    public LinkResponse deleteLinks(Long tgChatId, RemoveLinkRequest removeLinkRequest) {
        return requestWithHeader(HttpMethod.DELETE, tgChatId, removeLinkRequest);
    }

    public LinkResponse postLinks(Long tgChatId, AddLinkRequest addLinkRequest) {
        return requestWithHeader(HttpMethod.POST, tgChatId, addLinkRequest);
    }

    public ListLinksResponse getLinks(Long tgChatId) {
        return executeWithRetry(() -> webClient.method(HttpMethod.GET)
            .uri(LINKS)
            .header(TG_CHAT_ID, tgChatId.toString())
            .accept(MediaType.APPLICATION_JSON)
            .exchangeToMono(
                formResponse("Get links got bad response", ListLinksResponse.class))
            .block());
    }

    public ListLinksResponse deleteTgChat(Long tgChatId) {
        return requestWithPathParameter(HttpMethod.DELETE, tgChatId, ListLinksResponse.class);
    }

    public void registerTgChat(Long tgChatId) {
        requestWithPathParameter(HttpMethod.POST, tgChatId, Void.class);
    }

    private <T> T requestWithPathParameter(HttpMethod method, Long tgChatId, Class<T> responseClass) {
        return executeWithRetry(() -> webClient.method(method)
            .uri("/tg-chat/{id}", tgChatId)
            .accept(MediaType.APPLICATION_JSON)
            .exchangeToMono(
                formResponse("Register Tg chat got bad response", responseClass))
            .block());
    }

    private <T> LinkResponse requestWithHeader(HttpMethod method, Long tgChatId, T request) {
        return executeWithRetry(() -> webClient.method(method)
            .uri(LINKS)
            .header(TG_CHAT_ID, tgChatId.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .accept(MediaType.APPLICATION_JSON)
            .exchangeToMono(
                formResponse("Post links got bad response", LinkResponse.class))
            .block());
    }

    private <T> Function<ClientResponse, Mono<T>> formResponse(String message, Class<T> responseClass) {
        return response -> {
            if (!response.statusCode().equals(HttpStatus.OK)) {
                return response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(apiErrorResponse -> Mono.error(new ScrapperResponseException(
                        message,
                        apiErrorResponse
                    )));
            }
            return response.bodyToMono(responseClass);
        };
    }

    private <T> T executeWithRetry(Supplier<T> task) {
        return retryTemplate.execute(context -> task.get());
    }
}
