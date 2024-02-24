package edu.java.scrapper.configuration;

import edu.java.scrapper.github.GitHubClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GitHubClientConfig {
    @Bean
    public GitHubClient gitHubClient(ApplicationConfig config) {
        return new GitHubClient(WebClient.builder(), config.gitHubApiUri());
    }
}
