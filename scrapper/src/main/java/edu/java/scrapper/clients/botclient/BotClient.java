package edu.java.scrapper.clients.botclient;

import edu.java.dto.ApiErrorResponse;
import edu.java.dto.LinkUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class BotClient {

    private final WebClient webClient;

    public BotClient(WebClient.Builder webClientBuilder, String botApiUri) {
        this.webClient = webClientBuilder.baseUrl(botApiUri).build();
    }

    public ResponseEntity<Void> sendUpdates(LinkUpdateRequest linkUpdateRequest) {
        return this.webClient.post()
            .uri("/updates")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(linkUpdateRequest)
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).flatMap(
                    apiErrorResponse -> Mono.error(new BotResponseException("Send updates error", apiErrorResponse))
                )
            ).toBodilessEntity().block();
    }
}
