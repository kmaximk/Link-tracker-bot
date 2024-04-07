package edu.java.scrapper.configuration;

import edu.java.scrapper.clients.botclient.BotResponseException;
import edu.java.scrapper.clients.github.GitHubApiException;
import edu.java.scrapper.clients.stackoverflow.StackOverflowApiException;
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
        final SimpleRetryPolicy simpleRetryPolicy = simpleRetryPolicy();
        this.setExceptionClassifier((Classifier<Throwable, RetryPolicy>) classifiable -> {
            Integer statusCode = getStatusCode(classifiable);
            if (statusCode == null || !codes.contains(statusCode)) {
                return new NeverRetryPolicy();
            } else {
                return simpleRetryPolicy;
            }
        });
    }

    private Integer getStatusCode(Throwable classifiable) {
        return switch (classifiable) {
            case GitHubApiException e -> e.getStatusCode().value();
            case StackOverflowApiException e -> e.getStatusCode().value();
            case BotResponseException e -> e.getApiErrorResponse().code();
            default -> null;
        };
    }

    private SimpleRetryPolicy simpleRetryPolicy() {
        final SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(maxAttempts);
        return simpleRetryPolicy;
    }
}
