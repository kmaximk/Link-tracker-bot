package edu.java.retry;

import java.time.Duration;
import lombok.Setter;
import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.BackOffContext;
import org.springframework.retry.backoff.BackOffInterruptedException;
import org.springframework.retry.backoff.BackOffPolicy;

@Setter
public class LinearBackOff implements BackOffPolicy {

    private final long multiplier;

    private final long interval;

    public LinearBackOff(long multiplier, long interval) {
        this.multiplier = multiplier;
        this.interval = interval;
    }

    @Override
    public BackOffContext start(RetryContext context) {
        return new LinearBackOffContext(multiplier, interval);
    }

    @Override
    public void backOff(BackOffContext backOffContext) throws BackOffInterruptedException {
        LinearBackOffContext context = (LinearBackOffContext) backOffContext;
        try {
            Thread.sleep(Duration.ofSeconds(context.multiplier * context.interval));
        } catch (InterruptedException e) {
            throw new BackOffInterruptedException("Linear back off was interrupted", e);
        }
        context.multiplier++;
    }

    static class LinearBackOffContext implements BackOffContext {
        private long multiplier;

        private final long interval;

        LinearBackOffContext(long multiplier, long interval) {
            this.multiplier = multiplier;
            this.interval = interval;
        }
    }
}

