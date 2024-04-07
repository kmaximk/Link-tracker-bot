package edu.java.scrapper.service.sender;

import edu.java.dto.LinkUpdateRequest;
import java.util.concurrent.CompletableFuture;
import edu.java.scrapper.configuration.ApplicationConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

@RequiredArgsConstructor
public class ScrapperQueueProducer implements UpdateSender{

    private final KafkaTemplate<Integer, LinkUpdateRequest> kafkaTemplate;

    private final ApplicationConfig.KafkaConfig kafkaConfig;


    @Override
    public void sendUpdates(LinkUpdateRequest update) {
        CompletableFuture<SendResult<Integer, LinkUpdateRequest>> send = kafkaTemplate.send(kafkaConfig.updatesTopic(),
            update.id().intValue(), update);
        try {
            System.out.println(send.get());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
