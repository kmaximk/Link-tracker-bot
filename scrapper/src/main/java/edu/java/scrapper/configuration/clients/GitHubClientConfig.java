package edu.java.scrapper.configuration.clients;

import edu.java.scrapper.clients.github.GitHubClient;
import edu.java.scrapper.configuration.ApplicationConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GitHubClientConfig {
    @Bean
    public GitHubClient gitHubClient(ApplicationConfig config, RetryTemplate retryTemplate) {
        return new GitHubClient(WebClient.builder(), config.gitHubApiUri(), retryTemplate);
    }
}
