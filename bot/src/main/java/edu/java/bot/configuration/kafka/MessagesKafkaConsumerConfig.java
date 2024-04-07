package edu.java.bot.configuration.kafka;

import edu.java.bot.configuration.ApplicationConfig;
import edu.java.dto.LinkUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@EnableKafka
@Configuration
@Slf4j
public class MessagesKafkaConsumerConfig {
    @Bean
    public ConcurrentKafkaListenerContainerFactory<Integer, LinkUpdateRequest> messageListenerContainerFactory(
        ApplicationConfig config
        ) {
        ConcurrentKafkaListenerContainerFactory<Integer, LinkUpdateRequest> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.kafka().servers(),
            ConsumerConfig.GROUP_ID_CONFIG, config.kafka().consumerGroup(),
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
            JsonDeserializer.TRUSTED_PACKAGES, config.kafka().trustedPackages()
        )));
        return factory;
    }

    @Bean
    public NewTopic topic(ApplicationConfig config) {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        String topicName = config.kafka().updatesTopic();
        try (Admin admin = Admin.create(props)) {
            NewTopic newTopic = TopicBuilder.name(topicName)
                .partitions(3)
                .replicas(3)
                .build();
            admin.createTopics(Collections.singletonList(newTopic)).all().get();
            log.info("Topic created successfully {}", topicName);
            return newTopic;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error creating topic {}", e.getMessage());
        }
        return null;
    }
}
