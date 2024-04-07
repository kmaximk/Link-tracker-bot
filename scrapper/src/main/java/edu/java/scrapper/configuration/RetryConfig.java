package edu.java.scrapper.configuration;

import edu.java.retry.LinearBackOff;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RetryConfig {

    private final ApplicationConfig.Retry retry;

    private final CodeRetryPolicy retryPolicy;


    public RetryConfig(ApplicationConfig.Retry retry) {
        this.retry = retry;
        this.retryPolicy = new CodeRetryPolicy(retry.limit(), retry.codes());
        this.retryPolicy.setClassifier();
    }

    @Bean
    public RetryTemplate retryTemplate() {
        return switch (retry.backoff()) {
            case "constant" -> RetryTemplate.builder()
                .customPolicy(retryPolicy)
                .fixedBackoff(Duration.ofSeconds(retry.interval()))
                .build();
            case "exponential" -> RetryTemplate.builder()
                .customPolicy(retryPolicy)
                .exponentialBackoff(Duration.ofSeconds(2), retry.interval(), Duration.ofSeconds(retry.maxInterval()))
                .build();
            case "linear" -> RetryTemplate.builder()
                .customPolicy(retryPolicy)
                .customBackoff(new LinearBackOff(1, retry.interval()))
                .build();
            default -> null;
        };
    }
}
