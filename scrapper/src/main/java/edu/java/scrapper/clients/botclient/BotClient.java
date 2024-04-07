package edu.java.scrapper.clients.botclient;

import edu.java.dto.ApiErrorResponse;
import edu.java.dto.LinkUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class BotClient {

    private final WebClient webClient;

    private final RetryTemplate retryTemplate;

    public BotClient(WebClient.Builder webClientBuilder, String botApiUri, RetryTemplate retryTemplate) {
        this.webClient = webClientBuilder.baseUrl(botApiUri).build();
        this.retryTemplate = retryTemplate;
    }

    public void sendUpdates(LinkUpdateRequest linkUpdateRequest) {
        retryTemplate.execute(context -> this.webClient.post()
            .uri("/updates")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(linkUpdateRequest)
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).flatMap(
                    apiErrorResponse -> Mono.error(new BotResponseException("Send updates error", apiErrorResponse))
                )
            ).toBodilessEntity().block());
    }
}
