package com.publictransport.event;

import java.time.Instant;

public record TicketSaleEvent(String routeId, int quantity, double unitPrice, Instant occurredAt)
        implements TransitEvent {
    public TicketSaleEvent {
        if (routeId == null || occurredAt == null || quantity < 1 || unitPrice < 0) {
            throw new IllegalArgumentException("Invalid ticket sale event");
        }
    }

    @Override
    public EventType type() {
        return EventType.TICKET_SALE;
    }
}
