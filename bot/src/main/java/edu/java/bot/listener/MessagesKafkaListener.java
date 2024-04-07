package edu.java.bot.listener;

import edu.java.bot.service.UserNotifier;
import edu.java.dto.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessagesKafkaListener {

    private final UserNotifier userNotifier;

    @KafkaListener(topics = "${app.kafka.updates-topic}",
                   groupId = "${app.kafka.consumer-group}",
                   containerFactory = "messageListenerContainerFactory",
                   concurrency = "${app.kafka.concurrency}")
    public void listenStringMessages(
        @Payload LinkUpdateRequest message,
        @Header(KafkaHeaders.RECEIVED_KEY) Integer key,
        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition
    ) {
        log.info("Received Message from partition {} with key {}: {}", partition, key, message);
        userNotifier.sendUpdateToUser(message);
    }
}
