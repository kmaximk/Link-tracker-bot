package edu.java.scrapper.clients.github;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

public class GitHubClient {

    private final WebClient webClient;

    public GitHubClient(WebClient.Builder webClientBuilder, String githubBaseUrl) {
        this.webClient = webClientBuilder.baseUrl(githubBaseUrl).build();
    }

    public GitHubResponse getRepositoryInfo(String owner, String repository) {
        return this.webClient.get()
            .uri("/repos/{owner}/{repository}", owner, repository)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve().bodyToMono(GitHubResponse.class).block();
    }
}
