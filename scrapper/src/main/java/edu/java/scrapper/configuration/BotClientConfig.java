package edu.java.scrapper.configuration;

import edu.java.scrapper.clients.botclient.BotClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class BotClientConfig {
    @Bean
    public BotClient botClient(ApplicationConfig config) {
        return new BotClient(WebClient.builder(), config.botApiUri());
    }
}
