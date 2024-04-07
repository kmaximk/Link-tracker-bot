package edu.java.scrapper.configuration.clients;

import edu.java.scrapper.clients.stackoverflow.StackOverflowClient;
import edu.java.scrapper.configuration.ApplicationConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class StackOverflowClientConfig {
    @Bean
    public StackOverflowClient stackOverflowClient(ApplicationConfig config, RetryTemplate retryTemplate) {
        return new StackOverflowClient(WebClient.builder(), config.stackOverflowApiUri(), retryTemplate);
    }
}
