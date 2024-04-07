package edu.java.scrapper.configuration;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotNull
    Scheduler scheduler,

    @DefaultValue("https://api.github.com")
    String gitHubApiUri,

    @DefaultValue("https://api.stackexchange.com/2.3")
    String stackOverflowApiUri,

    @DefaultValue("http://localhost:8090")
    String botApiUri,

    @NotNull
    AccessType databaseAccessType,

    @NotNull
    RateLimit limit,

    @NotNull
    Retry retry,

    @NotNull
    Boolean useQueue,

    @NotNull
    KafkaConfig kafka
) {
    @Bean
    private long schedulerDelay() {
        return scheduler.interval.toMillis();
    }

    @Bean
    public Retry retry() {
        return retry;
    }

    @Bean
    public RateLimit rateLimit() {
        return this.limit;
    }

    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
    }

    public record RateLimit(Integer tokens, @NotNull Duration interval) {

    }

    public enum AccessType {
        JDBC, JPA,
        JOOQ
    }

    public record Retry(@NotEmpty String backoff, @NotNull Integer limit, @NotNull Integer interval,
                        List<Integer> codes,
                        Integer maxInterval) {

    }

    public record KafkaConfig(@NotEmpty String servers,
                              @NotEmpty String trustedPackages,
                              @NotEmpty String consumerGroup,
                              @NotEmpty String updatesTopic) {

    }
}
