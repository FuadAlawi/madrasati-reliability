package sa.edu.madrasati.reliability.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ReliabilitySimulator {
    private final long baseLatencyMs;
    private final double baseFailureRate;
    private final Random rnd = new Random(42);

    public ReliabilitySimulator(long baseLatencyMs, double baseFailureRate) {
        this.baseLatencyMs = baseLatencyMs;
        this.baseFailureRate = baseFailureRate;
    }

    public static class Result {
        public final double availability;
        public final int p95LatencyMs;
        public final double errorRate;
        public Result(double availability, int p95LatencyMs, double errorRate) {
            this.availability = availability;
            this.p95LatencyMs = p95LatencyMs;
            this.errorRate = errorRate;
        }
    }

    public Result runMonteCarlo(int runs) {
        List<Integer> latencies = new ArrayList<>(runs);
        int errors = 0;
        for (int i = 0; i < runs; i++) {
            boolean fault = ChaosInjector.faultOccurs(rnd, baseFailureRate);
            if (fault) {
                errors++;
                // inflate latency on errors to simulate retries/timeouts
                latencies.add((int)(baseLatencyMs + 800 + rnd.nextInt(400)));
            } else {
                // normal latency with jitter
                latencies.add((int)(baseLatencyMs + rnd.nextInt(60)));
            }
        }
        Collections.sort(latencies);
        int p95 = latencies.get((int)(runs * 0.95) - 1);
        double errorRate = (double) errors / runs;
        double availability = 1.0 - errorRate;
        return new Result(availability, p95, errorRate);
    }
}
