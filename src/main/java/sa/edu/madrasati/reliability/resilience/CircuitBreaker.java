package sa.edu.madrasati.reliability.resilience;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;

public class CircuitBreaker {
    public enum State { CLOSED, OPEN, HALF_OPEN }

    private State state = State.CLOSED;
    private int failures = 0;
    private final int failureThreshold;
    private final Duration openInterval;
    private Instant openedAt = Instant.MIN;

    public CircuitBreaker(int failureThreshold, Duration openInterval) {
        this.failureThreshold = failureThreshold;
        this.openInterval = openInterval;
    }

    public synchronized <T> T call(Callable<T> action, T fallback) {
        try {
            if (state == State.OPEN) {
                if (Instant.now().isAfter(openedAt.plus(openInterval))) {
                    state = State.HALF_OPEN;
                } else {
                    return fallback;
                }
            }
            T result = action.call();
            onSuccess();
            return result;
        } catch (Exception ex) {
            onFailure();
            return fallback;
        }
    }

    private void onSuccess() {
        failures = 0;
        state = State.CLOSED;
    }

    private void onFailure() {
        failures++;
        if (failures >= failureThreshold) {
            state = State.OPEN;
            openedAt = Instant.now();
        }
    }

    public State getState() { return state; }
}
