# Public Transport — Observer and Visitor Patterns

Small Java 17 project for Assignment 6. It simulates public transport arrivals, ticket sales, passenger notifications and accessibility inspections.

## Run

Requirements: JDK 17+ and Maven 3.9+.

```bash
mvn test
mvn package
java -jar target/public-transport-patterns-1.0.0.jar 4
```

The argument (`4`) configures the producer thread count. At least three threads are required because the simulation runs three producer tasks.

## Design

### Observer

- `VehicleArrivalSource` and `TicketSaleSource` are two independent subjects.
- `StopDisplay`, `PassengerApp`, `DelayInspectionService` and `RidershipAggregator` listen for vehicle arrivals.
- `TicketRevenueTracker` listens for ticket sales.
- `RidershipAggregator` is both an observer and a subject: it aggregates route passenger totals and publishes `RidershipSummaryEvent` events to `RidershipSummaryDisplay`.
- `DelayInspectionService` publishes `InspectionCompletedEvent` to `InspectionReportDisplay`.
- Events are immutable Java records. Subscription is per `EventType` and event class. A listener may subscribe or unsubscribe while handling an event.

**Push model:** the event contains the arrival/sale data, so listeners do not need another call to the source. This keeps delivery simple and avoids listeners reading changing source state.

### Visitor and Composite

Tree: `TransitNetwork → Route → Stop → Bus / Tram / StationFacility / TicketMachine / BicycleDock`.

There are three container levels and five leaf types. `TransitNode.accept(visitor)` calls the overload for its concrete type (double dispatch). Visitors perform traversal, not the element classes.

Visitors:
1. `OperatingCostVisitor` — calculates total daily operating cost.
2. `RidershipVisitor` — accumulates passenger, vehicle and facility totals across a subtree.
3. `AccessibilityFilterVisitor` — returns inaccessible elements.
4. `JsonExportVisitor` — exports the tree with nested arrays and escaped strings.
5. `NodeCountVisitor` — counts nodes without changing any element class (trade-off commit).

**Traversal choice:** visitors traverse children. This keeps element classes data-focused and lets each operation decide how to walk the tree. It also makes a visitor usable on one `Stop` subtree.

### Integration

When a vehicle arrival is delayed by at least five minutes, `DelayInspectionService` runs `AccessibilityFilterVisitor` on the matching `Stop` only. It publishes the found item IDs in a new `InspectionCompletedEvent`.

### Concurrency

`EventSubject` uses `ConcurrentHashMap` and `CopyOnWriteArrayList`; it does not lock the whole subject during notification. `TransitSimulation` runs three producer tasks on a configurable `ExecutorService`, waits for their completion and shuts the executor down gracefully. Shared counters use atomic/adder classes.

## Tests

The JUnit 5 test suite covers event-type routing, runtime subscribe/unsubscribe, immutable events, visitor calculations, JSON nesting, subtree inspection, aggregation, ticket revenue and concurrency. The stress test starts four producer threads and checks the exact expected total: **4 × 1,000 = 4,000 events**.

## Trade-off proof commits

- **New visitor commit:** `Add NodeCountVisitor without changing elements`. This commit changes only `NodeCountVisitor.java`, `NodeCountVisitorTest.java` and this README. Existing element classes and the `Visitor` interface do not change. This demonstrates the low cost of adding a new operation.
- **New element commit:** `Add BicycleDock transit element`. Files changed: `BicycleDock.java`, `Visitor.java`, all five visitor implementations, `Main.java`, `BicycleDockVisitorTest.java` and `docs/class-diagram.puml`. Every visitor must define how the new element is handled. This demonstrates the cost of adding a new element to a Visitor-based design.

## UML

See [`docs/class-diagram.puml`](docs/class-diagram.puml).
