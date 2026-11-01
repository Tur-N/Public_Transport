package com.publictransport.event;

import java.time.Instant;

public interface TransitEvent {
    EventType type();
    Instant occurredAt();
}
