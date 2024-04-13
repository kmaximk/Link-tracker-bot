package edu.java.scrapper.service.sender;

import edu.java.dto.LinkUpdateRequest;
import edu.java.scrapper.configuration.ApplicationConfig;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

@RequiredArgsConstructor
@Slf4j
public class ScrapperQueueProducer implements UpdateSender {

    private final KafkaTemplate<Integer, LinkUpdateRequest> kafkaTemplate;

    private final ApplicationConfig.KafkaConfig kafkaConfig;

    @Override
    public void sendUpdates(LinkUpdateRequest update) {
        CompletableFuture<SendResult<Integer, LinkUpdateRequest>> send = kafkaTemplate.send(kafkaConfig.updatesTopic(),
            update.id().intValue(), update
        );
        send.whenComplete((sendResult, error) -> {
            if (error != null) {
                log.error("Error occurred when sending update using Kafka", error);
            } else {
                log.info("Update sent to partition {}", sendResult.getRecordMetadata().partition());
            }
        });
    }
}
