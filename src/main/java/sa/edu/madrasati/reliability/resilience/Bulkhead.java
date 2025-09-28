package sa.edu.madrasati.reliability.resilience;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class Bulkhead {
    private final Semaphore semaphore;
    private final long timeoutMs;

    public Bulkhead(int maxConcurrent, long timeoutMs) {
        this.semaphore = new Semaphore(maxConcurrent);
        this.timeoutMs = timeoutMs;
    }

    public <T> T execute(Supplier<T> supplier, T fallback) {
        boolean acquired = false;
        try {
            acquired = semaphore.tryAcquire(timeoutMs, TimeUnit.MILLISECONDS);
            if (!acquired) return fallback;
            return supplier.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return fallback;
        } finally {
            if (acquired) semaphore.release();
        }
    }
}
