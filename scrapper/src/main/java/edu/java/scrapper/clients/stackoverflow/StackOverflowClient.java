package edu.java.scrapper.clients.stackoverflow;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.reactive.function.client.WebClient;

public class StackOverflowClient {
    private final WebClient webClient;

    private final RetryTemplate retryTemplate;

    public StackOverflowClient(WebClient.Builder webClientBuilder, String baseUrl, RetryTemplate retryTemplate) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.retryTemplate = retryTemplate;
    }

    public StackOverflowResponse getQuestionUpdate(String id) {
        return retryTemplate.execute(context -> webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/questions/{id}").queryParam("site", "stackoverflow").build(id))
            .accept(MediaType.APPLICATION_JSON)
            .exchangeToMono(response -> {
                    if (!response.statusCode().equals(HttpStatus.OK)) {
                        throw new StackOverflowApiException(response.statusCode());
                    }
                    return response.bodyToMono(StackOverflowResponse.class);
                }
            ).block());
    }
}
