package edu.java.scrapper.configuration;

import edu.java.dto.LinkUpdateRequest;
import edu.java.scrapper.clients.botclient.BotClient;
import edu.java.scrapper.service.sender.ScrapperQueueProducer;
import edu.java.scrapper.service.sender.UpdateSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class UpdateSenderConfig {

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "use-queue", havingValue = "true")
    public UpdateSender scrapperQueueProducer(
        KafkaTemplate<Integer,
            LinkUpdateRequest> kafkaTemplate,
        ApplicationConfig config
    ) {
        return new ScrapperQueueProducer(kafkaTemplate, config.kafka());
    }

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "use-queue", havingValue = "false")
    public UpdateSender botClient(ApplicationConfig config, RetryTemplate retryTemplate) {
        return new BotClient(WebClient.builder(), config.botApiUri(), retryTemplate);
    }
}
