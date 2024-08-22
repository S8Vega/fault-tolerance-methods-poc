package co.com.the_chaos_company.consumer;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerEvent;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CircuitBreakerEventLogger {
    private static final Logger logger = LoggerFactory.getLogger(CircuitBreakerEventLogger.class);

    public CircuitBreakerEventLogger(CircuitBreakerRegistry circuitBreakerRegistry) {
        circuitBreakerRegistry.getAllCircuitBreakers().forEach(circuitBreaker ->
                circuitBreaker.getEventPublisher().onError(this::logError));
    }

    private void logError(CircuitBreakerEvent event) {
        if (event instanceof CircuitBreakerOnErrorEvent errorEvent) {
            logger.error("CircuitBreaker '{}' recorded an error: {}",
                    errorEvent.getCircuitBreakerName(),
                    errorEvent.getThrowable().getMessage());
        }
    }
}
