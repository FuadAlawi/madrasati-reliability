package sa.edu.madrasati.reliability.simulation;

import java.util.Random;

public class ChaosInjector {
    public static boolean faultOccurs(Random rnd, double baseFailureRate) {
        // Randomly inflate failure rate to simulate bursts (e.g., cache or DB issues)
        double burstMultiplier = rnd.nextDouble() < 0.05 ? 5.0 : 1.0;
        double effective = Math.min(0.9, baseFailureRate * burstMultiplier);
        return rnd.nextDouble() < effective;
    }
}
