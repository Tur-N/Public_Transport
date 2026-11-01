# Assignment 6 — Public Transport

## Goal
A small Java 17 system demonstrates the Observer and Visitor patterns for a public transport network.

## Observer
`VehicleArrivalSource` and `TicketSaleSource` publish immutable events. Five main listeners update stop displays, the passenger app, ticket revenue, passenger statistics and delay inspections. `RidershipAggregator` is both an observer and a subject: it collects passenger counts and republishes `RidershipSummaryEvent`. Subscriptions are selected by event type and can change at runtime.

The system uses the **push** model because an event already contains the data required by listeners. The registry uses `ConcurrentHashMap` and `CopyOnWriteArrayList` so subscription changes do not lock the whole subject.

## Visitor
The Composite tree is `TransitNetwork → Route → Stop → Bus / Tram / StationFacility / TicketMachine / BicycleDock`. It has three container levels and five leaf types. `Visitor<R>` uses double dispatch. The visitors calculate operating cost, accumulate ridership, filter inaccessible elements, export nested JSON and count nodes. Visitors perform traversal, so element classes keep only their data and `accept` dispatch method.

## Integration and concurrency
A vehicle delay of five minutes or more triggers `AccessibilityFilterVisitor` on the matching stop only. The inspection result is published as `InspectionCompletedEvent`. Three producer tasks run through a configurable `ExecutorService`; shutdown waits for task completion. Shared counters and listener state use concurrent or atomic classes.

## Verification
There are 30 JUnit 5 test methods. The stress test publishes 1,000 arrival events from each of four producer threads and checks for exactly 4,000 notifications. Production sources compile with `javac --release 17`, and the demo application runs. Maven was not installed in the build environment, so `mvn test` was not executed there; run it locally with Maven to execute the suite through JUnit 5.

## Extension cost
The `NodeCountVisitor` commit adds an operation without editing element classes. The `BicycleDock` commit adds a leaf and requires updates to the Visitor interface and every visitor. The repository contains these as separate commits.
