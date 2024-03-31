package edu.java.scrapper.clients.github;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.reactive.function.client.WebClient;

public class GitHubClient {

    private final WebClient webClient;

    private final RetryTemplate retryTemplate;

    public GitHubClient(WebClient.Builder webClientBuilder, String githubBaseUrl, RetryTemplate retryTemplate) {
        this.webClient = webClientBuilder.baseUrl(githubBaseUrl).build();
        this.retryTemplate = retryTemplate;
    }

    public GitHubResponse getRepositoryInfo(String owner, String repository) {
        return retryTemplate.execute(context -> this.webClient.get()
            .uri("/repos/{owner}/{repository}", owner, repository)
            .accept(MediaType.APPLICATION_JSON)
            .exchangeToMono(response -> {
                if (!response.statusCode().equals(HttpStatus.OK)) {
                    throw new GitHubApiException(response.statusCode());
                }
                return response.bodyToMono(GitHubResponse.class);
            }).block());
    }
}
