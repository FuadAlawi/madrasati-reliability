# Madrasati Critical System Reliability Strategy (AMIR Framework)

[![CI](https://github.com/FuadAlawi/madrasati-reliability/actions/workflows/ci.yml/badge.svg)](https://github.com/FuadAlawi/madrasati-reliability/actions/workflows/ci.yml)

This repository contains a complete reliability strategy for Saudi Arabia's Madrasati education platform, aligned to Vision 2030.

## Features

- AMIR framework report ready for PDF export
- Java prototype/simulation with fault injection and resilience patterns
- Fault trees and C4 architecture diagrams (Mermaid)
- Testing strategy, metrics, and validation plan
- Humanization checklist CLI app (Java)
- GitHub Actions CI for build and tests

## Repository Structure

- `REPORT_AMIR.md` — Main report to export as PDF
- `docs/diagrams.md` — C4 architecture and fault tree diagrams (Mermaid)
- `src/main/java/sa/edu/madrasati/reliability/` — Java sources
  - `App.java` — Entrypoint runner
  - `simulation/` — Reliability Monte Carlo and chaos injection
  - `resilience/` — Resilience patterns (Circuit Breaker, Bulkhead)
  - `humanization/` — Humanization checklist CLI
- `src/test/java/sa/edu/madrasati/reliability/` — Unit tests
- `.github/workflows/ci.yml` — Build and test pipeline
- `pom.xml` — Maven config

## Getting Started

1) Build and test

```bash
mvn -q -e -DskipTests=false clean verify
```

2) Run prototype simulation

```bash
mvn -q -DskipTests package
java -jar target/madrasati-reliability-1.0.0-jar-with-dependencies.jar simulate --runs 10000 --latency-ms 150 --failure-rate 0.02
```

3) Run humanization checklist CLI

```bash
java -jar target/madrasati-reliability-1.0.0-jar-with-dependencies.jar humanize --self-service --a11y --localization --data-privacy --teacher-support --student-safety
```

4) Export report to PDF (local)

- Recommended: `pandoc` + `wkhtmltopdf` or `weasyprint`.
- Example with Pandoc:

```bash
pandoc REPORT_AMIR.md -o REPORT_AMIR.pdf --from markdown --pdf-engine wkhtmltopdf -V margin-left=20mm -V margin-right=20mm -V margin-top=20mm -V margin-bottom=20mm
```

Alternatively, open `REPORT_AMIR.md` in VS Code and use a Markdown PDF extension.

## Example Outputs

### Humanization Checklist (example)

Command:

```bash
java -jar target/madrasati-reliability-1.0.0-jar-with-dependencies.jar humanize \
  --self-service --a11y --localization --data-privacy --teacher-support --student-safety
```

Output:

```
Humanization Checklist
----------------------
[X  ] Self-service & Clear Guidance
[X  ] Accessibility (WCAG 2.2 AA)
[X  ] Localization (Arabic-first)
[X  ] Data privacy & consent
[X  ] Teacher support & escalation
[X  ] Student online safety
Result: PASS
```

### Simulation (example)

Command:

```bash
java -jar target/madrasati-reliability-1.0.0-jar-with-dependencies.jar simulate \
  --runs 10000 --latency-ms 150 --failure-rate 0.02
```

Output:

```
Runs=10000, Availability=0.9740, P95 latency=208 ms, ErrorRate=0.0260
```

### Resilience (examples)

- Circuit Breaker (`sa.edu.madrasati.reliability.resilience.CircuitBreaker`):
  - Opens after a configurable number of failures, then transitions to HALF_OPEN after the cool-off interval.
- Bulkhead (`sa.edu.madrasati.reliability.resilience.Bulkhead`):
  - Limits concurrent calls using a semaphore; excess calls return a fallback if the timeout elapses.

For runnable examples, see `src/test/java/sa/edu/madrasati/reliability/ResilienceTest.java`.

## Java Version

- Java 17+

## Vision 2030 Alignment

See `REPORT_AMIR.md` section 4 for explicit mapping to Vision 2030 (Education, Digital Transformation, Localization, Inclusion & Ethics).

## License

MIT
