package com.publictransport.event;

import java.time.Instant;
import java.util.List;

public record InspectionCompletedEvent(String routeId, String stopId, int delayMinutes,
                                        List<String> inaccessibleItems, Instant occurredAt)
        implements TransitEvent {
    public InspectionCompletedEvent {
        if (routeId == null || stopId == null || occurredAt == null || delayMinutes < 0) {
            throw new IllegalArgumentException("Invalid inspection event");
        }
        inaccessibleItems = List.copyOf(inaccessibleItems);
    }

    @Override
    public EventType type() {
        return EventType.INSPECTION_COMPLETED;
    }
}
