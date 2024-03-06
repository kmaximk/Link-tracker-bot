package edu.java.scrapper.configuration;

import edu.java.scrapper.clients.stackoverflow.StackOverflowClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class StackOverflowClientConfig {
    @Bean
    public StackOverflowClient stackOverflowClient(ApplicationConfig config) {
        return new StackOverflowClient(WebClient.builder(), config.stackOverflowApiUri());
    }
}
