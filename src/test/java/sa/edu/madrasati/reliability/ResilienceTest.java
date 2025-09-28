package sa.edu.madrasati.reliability;

import org.junit.jupiter.api.Test;
import sa.edu.madrasati.reliability.resilience.Bulkhead;
import sa.edu.madrasati.reliability.resilience.CircuitBreaker;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

public class ResilienceTest {
    @Test
    void circuitBreakerOpensOnFailures() {
        CircuitBreaker cb = new CircuitBreaker(3, Duration.ofMillis(50));
        AtomicInteger calls = new AtomicInteger();
        for (int i = 0; i < 5; i++) {
            cb.call(() -> { calls.incrementAndGet(); throw new RuntimeException("fail"); }, "fallback");
        }
        assertEquals(CircuitBreaker.State.OPEN, cb.getState());
    }

    @Test
    void bulkheadLimitsConcurrency() {
        Bulkhead bh = new Bulkhead(1, 10);
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);

        ExecutorService exec = Executors.newSingleThreadExecutor();
        try {
            // First task acquires the single permit and holds it for 50ms
            exec.submit(() -> {
                started.countDown();
                return bh.execute(() -> {
                    try { Thread.sleep(50); } catch (InterruptedException ignored) {}
                    return "ok";
                }, "fallback");
            });

            // Ensure the first task started and likely holds the permit
            try { started.await(); } catch (InterruptedException ignored) {}

            // Second call should time out quickly and return fallback
            String r2 = bh.execute(() -> "ok2", "fallback");
            assertEquals("fallback", r2);
        } finally {
            exec.shutdownNow();
        }
    }
}
