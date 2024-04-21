package edu.java.bot.configuration.kafka;

import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.listener.MessagesKafkaListener;
import edu.java.bot.service.UserNotifier;
import edu.java.dto.LinkUpdateRequest;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

@EnableKafka
@Configuration
@Slf4j
@ConditionalOnProperty(prefix = "app", name = "use-queue", havingValue = "true")
public class KafkaConfig {

    private static final String DLQ = ".dlq";

    @Bean
    public MessagesKafkaListener messagesKafkaListener(UserNotifier userNotifier) {
        return new MessagesKafkaListener(userNotifier);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Integer, LinkUpdateRequest> messageListenerContainerFactory(
        ApplicationConfig config,
        DefaultErrorHandler errorHandler
    ) {
        ConcurrentKafkaListenerContainerFactory<Integer, LinkUpdateRequest> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.kafka().servers(),
            ConsumerConfig.GROUP_ID_CONFIG, config.kafka().consumerGroup(),
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class,
            ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, IntegerDeserializer.class,
            ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class,
            JsonDeserializer.TRUSTED_PACKAGES, config.kafka().trustedPackages()
        )));
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    @Bean
    public NewTopic topic(ApplicationConfig config) {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, config.kafka().servers());
        String topicName = config.kafka().updatesTopic();
        return getNewTopic(props, topicName, config.kafka().partitions(), config.kafka().replicas());
    }

    @Bean
    public NewTopic topicDLQ(ApplicationConfig config) {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, config.kafka().servers());
        String topicName = config.kafka().updatesTopic() + DLQ;
        return getNewTopic(props, topicName, config.kafka().partitions(), config.kafka().replicas());
    }

    private NewTopic getNewTopic(Properties props, String topicName, Integer partitions, Integer replicas) {
        try (Admin admin = Admin.create(props)) {
            NewTopic newTopic = TopicBuilder.name(topicName)
                .partitions(partitions)
                .replicas(replicas)
                .build();
            admin.createTopics(Collections.singletonList(newTopic)).all().get();
            log.info("Topic created successfully {}", topicName);
            return newTopic;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error creating topic {}", e.getMessage());
        }
        return null;
    }

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<byte[], byte[]> kafkaTemplate) {
        DeadLetterPublishingRecoverer recover = new DeadLetterPublishingRecoverer(
            kafkaTemplate,
            (r, e) -> {
                log.error("Cannot deserialize {} with error {}", r.offset(), e);
                return new TopicPartition(r.topic() + DLQ, r.partition());
            }
        );
        return new DefaultErrorHandler(recover, new FixedBackOff(0L, 1L));
    }


    @Bean
    public KafkaTemplate<byte[], byte[]> stringMessageKafkaTemplate(
        ApplicationConfig config
    ) {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.kafka().servers(),
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class
        )));
    }
}
