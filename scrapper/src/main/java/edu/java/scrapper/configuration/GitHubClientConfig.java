package edu.java.scrapper.configuration;

import edu.java.scrapper.github.GitHubClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GitHubClientConfig {
    @Value("${app.github-api-uri:https://api.github.com}")
    private String gitHubUri;

    @Bean
    public GitHubClient gitHubClient() {
        return new GitHubClient(WebClient.builder(), gitHubUri);
    }
}
