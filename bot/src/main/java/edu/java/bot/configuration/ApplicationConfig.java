package edu.java.bot.configuration;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotEmpty
    String telegramToken,

    @DefaultValue("http://localhost:9191")
    String scrapperApiUri,

    @NotNull
    Retry retry,

    @NotNull
    KafkaConfig kafka

) {

    @Bean
    public Retry retry() {
        return retry;
    }

    public record Retry(@NotEmpty String backoff, @NotNull Integer limit, @NotNull Integer interval,
                        List<Integer> codes,
                        Integer maxInterval) {

    }

    public record KafkaConfig(@NotEmpty String servers,
                              @NotEmpty String trustedPackages,
                              @NotEmpty String consumerGroup,
                              @NotEmpty String updatesTopic,
                              @NotNull Integer concurrency,
                              @NotNull Integer partitions,
                              @NotNull Integer replicas) {

    }
}
