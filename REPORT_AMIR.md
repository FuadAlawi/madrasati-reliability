# Madrasati Critical System Reliability Strategy (AMIR)

Author: Reliability Architecture Team
Date: 2025-09-27

---

## Phase 1 — Current State Analysis and Fault Identification (2–3 pages)

- Context: Madrasati serves millions of daily active students and teachers across KSA. Critical periods include morning sign-ins, class streaming, exams, and nationwide announcements.
- SLOs (initial proposal):
  - Availability: 99.95% monthly
  - Latency (P95): < 250 ms for core APIs
  - Error rate: < 0.1% for student auth and attendance
  - Data durability: 11 nines for critical records (attendance/exam)
- Observability baseline:
  - Centralized logging (JSON structured), tracing (OpenTelemetry), metrics (RED/USE)
  - Gap: limited business SLIs (attendance success, exam completion)
- Dependencies and risks:
  - External IdP for some schools, SMS/Email OTP providers, CDN, payment for premium content
  - Regional outages (ISP, DC zone), sudden load spikes (back-to-school, national exams)
- Major fault classes:
  - Infra: zone/regional outage, network partition, DNS misconfig, TLS cert expiry
  - App: cascading failures from synchronous chains, cache stampede, thread pool exhaustion
  - Data: hot partition on attendance table, replication lag, migration errors
  - Security/safety: DDoS, brute-force on login, abusive bots during exams
  - Human: config push error, feature flag mis-rollout, runbook gaps

## Phase 2 — Architecture Design, Code Samples, Fault Trees (3–4 pages)

### Target Architecture (High Level)

See `docs/diagrams.md` for C4 diagrams. Key patterns:

- Multi-AZ active-active with read-write split. Regional DR in Riyadh/Jeddah.
- API gateway + service mesh. Zero-trust mTLS. Rate-limiting, WAF.
- Idempotent, retry-safe commands, outbox/inbox for at-least-once delivery.
- CQRS for attendance/exam submissions; append-only event store for audit.
- Caching: request coalescing, TTL jitter, circuit breakers around cache backend.
- Backpressure: bulkheads on thread pools, bounded queues, adaptive concurrency.
- Graceful degradation: static content fallback, queue submissions during outages.

### Java Code Samples (Resilience)

- Circuit Breaker and Bulkhead shown in `src/main/java/sa/edu/madrasati/reliability/resilience/`.
- Chaos injection and reliability Monte Carlo in `simulation/`.

### Fault Trees

- `docs/diagrams.md` includes Mermaid-based fault trees for:
  - System Unavailability
  - Attendance Loss Event

## Phase 3 — Testing Strategy, Metrics, Validation (2–3 pages)

- Testing layers:
  - Unit: resilience primitives, deterministic chaos toggles
  - Integration: contract tests for IdP, OTP, CDN; timeouts and retries verified
  - Load/Soak: traffic patterns (ramp, burst, diurnal), warm cache vs cold cache
  - Chaos/Resilience: steady-state hypothesis, fault injection in staging prod-like
  - DR Exercises: AZ failover, regional failover, backup restore drills
- Metrics and SLIs/SLOs:
  - SLIs: Availability, P95/P99 latency, error rate, queue age, exam completion rate
  - Error budgets: alert on burn rates (2h and 24h windows)
  - Synthetic probes: login, attendance submit, exam start/finish
- Validation plan:
  - Define steady state, inject fault, measure deviation, rollback/mitigate
  - Track MTTR, change fail rate, deployment frequency, lead time

## Phase 4 — Vision 2030 Alignment, Scalability Roadmap, Ethics (1–2 pages)

- Vision 2030 pillars:
  - Education & Human Capability Development: reliable digital learning platform
  - Digital Government & Localization: Arabic-first UX, regional hosting, data sovereignty
  - Economic diversification: localization of engineering skills, vendor-agnostic designs
- Scalability Roadmap:
  - Phase A: Business SLIs, capacity modeling, autoscaling SLO-aware
  - Phase B: Regional DR active-passive -> active-active; data sharding for hot entities
  - Phase C: Multi-cloud portability, FIPS crypto modules, edge compute for exams
- Ethical considerations:
  - Equity: low-bandwidth modes, accessibility (WCAG 2.2 AA), offline-first for key flows
  - Safety: anti-cheat with privacy, consent for data processing, minimal data retention
  - Human-in-the-loop: teacher/parent oversight, appeal flows, transparent incident comms

---

## How to Use This Repo

- Build: `mvn clean verify`
- Run simulation: `java -jar target/madrasati-reliability-1.0.0.jar simulate`
- Run humanization checklist: `... jar ... humanize [flags]`
- Export PDF: `pandoc REPORT_AMIR.md -o REPORT_AMIR.pdf`
