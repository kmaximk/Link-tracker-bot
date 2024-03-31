package edu.java.bot.configuration;

import edu.java.bot.clients.ScrapperResponseException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.classify.Classifier;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;

@RequiredArgsConstructor
public class CodeRetryPolicy extends ExceptionClassifierRetryPolicy {

    private final Integer maxAttempts;

    private final List<Integer> codes;

    public void setClassifier() {
        final SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(maxAttempts);
        this.setExceptionClassifier((Classifier<Throwable, RetryPolicy>) classifiable -> {
            if (classifiable instanceof ScrapperResponseException exception) {
                if (codes.contains(exception.getApiErrorResponse().code())) {
                    return simpleRetryPolicy;
                }
            }
            return new NeverRetryPolicy();
        });
    }
}
