package edu.java.scrapper.configuration;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
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
    AccessType databaseAccessType
) {
    @Bean
    private long schedulerDelay() {
        return scheduler.interval.toMillis();
    }

    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
    }

    public enum AccessType {
        JDBC, JPA,
        JOOQ
    }
}
