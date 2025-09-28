package sa.edu.madrasati.reliability.humanization;

import java.util.LinkedHashMap;
import java.util.Map;

public class HumanizationChecklist {
    private final Map<String, Boolean> items = new LinkedHashMap<>();

    private HumanizationChecklist() {
        items.put("Self-service & Clear Guidance", false);
        items.put("Accessibility (WCAG 2.2 AA)", false);
        items.put("Localization (Arabic-first)", false);
        items.put("Data privacy & consent", false);
        items.put("Teacher support & escalation", false);
        items.put("Student online safety", false);
    }

    public static HumanizationChecklist fromArgs(String[] args) {
        HumanizationChecklist c = new HumanizationChecklist();
        for (String arg : args) {
            switch (arg) {
                case "--self-service": c.set("Self-service & Clear Guidance", true); break;
                case "--a11y": c.set("Accessibility (WCAG 2.2 AA)", true); break;
                case "--localization": c.set("Localization (Arabic-first)", true); break;
                case "--data-privacy": c.set("Data privacy & consent", true); break;
                case "--teacher-support": c.set("Teacher support & escalation", true); break;
                case "--student-safety": c.set("Student online safety", true); break;
                default: break;
            }
        }
        return c;
    }

    public void set(String key, boolean value) { items.put(key, value); }

    public boolean isPassing() { return items.values().stream().allMatch(v -> v); }

    public String renderReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("Humanization Checklist\n");
        sb.append("----------------------\n");
        items.forEach((k, v) -> sb.append(String.format("[%-3s] %s\n", v ? "X" : " ", k)));
        sb.append("Result: ").append(isPassing() ? "PASS" : "IMPROVEMENTS REQUIRED").append('\n');
        return sb.toString();
    }
}
