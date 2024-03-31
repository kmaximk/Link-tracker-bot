package edu.java.scrapper.configuration.clients;

import edu.java.scrapper.clients.botclient.BotClient;
import edu.java.scrapper.configuration.ApplicationConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class BotClientConfig {
    @Bean
    public BotClient botClient(ApplicationConfig config, RetryTemplate retryTemplate) {
        return new BotClient(WebClient.builder(), config.botApiUri(), retryTemplate);
    }
}
