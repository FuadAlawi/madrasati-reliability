package sa.edu.madrasati.reliability;

import sa.edu.madrasati.reliability.humanization.HumanizationChecklist;
import sa.edu.madrasati.reliability.simulation.ReliabilitySimulator;

public class App {
    public static void main(String[] args) {
        if (args.length == 0) {
            printHelp();
            return;
        }
        switch (args[0]) {
            case "simulate":
                runSimulation(args);
                break;
            case "humanize":
                runHumanization(args);
                break;
            default:
                printHelp();
        }
    }

    private static void runSimulation(String[] args) {
        int runs = 5000;
        long latencyMs = 150;
        double failureRate = 0.02;
        for (int i = 1; i < args.length; i++) {
            if ("--runs".equals(args[i]) && i + 1 < args.length) runs = Integer.parseInt(args[++i]);
            else if ("--latency-ms".equals(args[i]) && i + 1 < args.length) latencyMs = Long.parseLong(args[++i]);
            else if ("--failure-rate".equals(args[i]) && i + 1 < args.length) failureRate = Double.parseDouble(args[++i]);
        }
        ReliabilitySimulator sim = new ReliabilitySimulator(latencyMs, failureRate);
        ReliabilitySimulator.Result r = sim.runMonteCarlo(runs);
        System.out.printf("Runs=%d, Availability=%.4f, P95 latency=%d ms, ErrorRate=%.4f\n",
                runs, r.availability, r.p95LatencyMs, r.errorRate);
    }

    private static void runHumanization(String[] args) {
        HumanizationChecklist checklist = HumanizationChecklist.fromArgs(args);
        System.out.println(checklist.renderReport());
        if (!checklist.isPassing()) {
            System.exit(2);
        }
    }

    private static void printHelp() {
        System.out.println("Usage: java -jar madrasati-reliability.jar [simulate|humanize] [options]\n" +
                " simulate --runs N --latency-ms L --failure-rate F\n" +
                " humanize --self-service --a11y --localization --data-privacy --teacher-support --student-safety\n");
    }
}
