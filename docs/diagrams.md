# Diagrams

## C4 System Context

```mermaid
C4Context
title Madrasati Platform - System Context
Person(student, "Student", "Learns and submits attendance/exams")
Person(teacher, "Teacher", "Manages classes and assessments")
System_Boundary(madrasati, "Madrasati"){
  System(web, "Web/App Frontend", "React/Native")
  System(api, "API Gateway", "AuthZ, routing")
  System(services, "Microservices", "Attendance, Exams, Content")
  SystemDb(db, "Datastores", "RDBMS + Cache + Object Storage")
}
System_Ext(idp, "External IdP", "SSO")
System_Ext(otp, "OTP Provider", "SMS/Email")
System_Ext(cdn, "CDN", "Edge caching")
Rel(student, web, "Uses")
Rel(teacher, web, "Uses")
Rel(web, api, "HTTPS mTLS")
Rel(api, services, "mTLS + mesh")
Rel(services, db, "SQL/NoSQL")
Rel(api, cdn, "Static")
Rel(api, idp, "OIDC/SAML")
Rel(api, otp, "OTP/Email")
```

## Fault Tree: System Unavailability

```mermaid
flowchart TD
  A[Platform Unavailable During Exam]
  A --> B{Network Layer Failure}
  A --> C{Application Layer Failure}
  A --> D{Data Layer Failure}
  A --> E{Human/Process Errors}

  B --> B1[Cloud provider outage]
  B --> B2[DDoS attack on front-end services]

  C --> C1[Exam microservice crash due to unhandled exceptions]
  C --> C2[Overloaded assignment submission service (peak traffic)]

  D --> D1[Database server crash (single point of failure)]
  D --> D2[Inconsistent replication between database nodes]

  E --> E1[Incorrect system configuration before exam period]
  E --> E2[Late patching introducing new bugs]
```

## Fault Tree: Attendance Loss Event

```mermaid
flowchart TD
A[Attendance Lost] --> B{Write path fails}
A --> C{Replay mechanism fails}
B --> B1[DB outage]
B --> B2[Idempotency key collision]
C --> C1[Outbox not drained]
C --> C2[DLQ not monitored]
```
