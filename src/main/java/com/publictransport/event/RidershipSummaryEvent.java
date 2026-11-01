package com.publictransport.event;

import java.time.Instant;

public record RidershipSummaryEvent(String routeId, long totalPassengers, long arrivalCount,
                                    Instant occurredAt) implements TransitEvent {
    public RidershipSummaryEvent {
        if (routeId == null || occurredAt == null || totalPassengers < 0 || arrivalCount < 0) {
            throw new IllegalArgumentException("Invalid ridership summary event");
        }
    }

    @Override
    public EventType type() {
        return EventType.RIDERSHIP_SUMMARY;
    }
}
