package edu.java.scrapper.configuration;

import edu.java.scrapper.stackoverflow.StackOverflowClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class StackOverflowClientConfig {
    @Value("${app.stackoverflow-api-uri:https://api.stackexchange.com/2.3}")
    private String stackOverflowApiUri;

    @Bean
    public StackOverflowClient stackOverflowClient() {
        return new StackOverflowClient(WebClient.builder(), stackOverflowApiUri);
    }
}
