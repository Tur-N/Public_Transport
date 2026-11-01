package com.publictransport.event;

import java.time.Instant;

public record VehicleArrivalEvent(String routeId, String stopId, String vehicleId,
                                  int passengers, int delayMinutes, Instant occurredAt)
        implements TransitEvent {
    public VehicleArrivalEvent {
        if (routeId == null || stopId == null || vehicleId == null || occurredAt == null
                || passengers < 0 || delayMinutes < 0) {
            throw new IllegalArgumentException("Invalid vehicle arrival event");
        }
    }

    @Override
    public EventType type() {
        return EventType.VEHICLE_ARRIVAL;
    }
}
