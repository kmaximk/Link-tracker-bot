package edu.java.scrapper.controller.limits;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.dto.ApiErrorResponse;
import edu.java.scrapper.configuration.ApplicationConfig;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, Bucket> buckets = new HashMap<>();


    private final ApplicationConfig.RateLimit rateLimit;


    private final static int SECOND_TO_NANO = 1_000_000_000;

    private Bucket getBucket(String apiKey) {
        return buckets.computeIfAbsent(apiKey, this::addBucket);
    }

    private Bucket addBucket(String s) {
        Bandwidth bandwidth = Bandwidth.builder().capacity(
            rateLimit.tokens()).refillIntervally(rateLimit.tokens(), rateLimit.interval()).build();
        return Bucket.builder().addLimit(bandwidth).build();
    }

    @Override
    public boolean preHandle(
        @NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
        @NotNull Object handler
    ) throws IOException {
        String apiKey = request.getRemoteAddr();
        Bucket bucket = getBucket(apiKey);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            return true;
        } else {
            long waitForRefill = probe.getNanosToWaitForRefill() / SECOND_TO_NANO;
            String json = new ObjectMapper().writeValueAsString(new ApiErrorResponse(
                "You have exhausted your limits for this API",
                HttpStatus.TOO_MANY_REQUESTS.value(),
                "",
                String.format("Rate limit refill after %d seconds", waitForRefill),
                Collections.emptyList()
            ));
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().print(json);
            response.setContentType("application/json");
            return false;
        }
    }
}
