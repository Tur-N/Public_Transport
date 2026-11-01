package com.publictransport.source;

import com.publictransport.event.*;

import java.time.Instant;

public final class TicketSaleSource implements Subject<TransitEvent> {
    private final EventSubject<TransitEvent> events = new EventSubject<>();

    public void sell(String routeId, int quantity, double unitPrice) {
        publish(new TicketSaleEvent(routeId, quantity, unitPrice, Instant.now()));
    }

    @Override
    public <T extends TransitEvent> void subscribe(EventType type, Class<T> eventClass, Observer<? super T> observer) {
        events.subscribe(type, eventClass, observer);
    }

    @Override
    public <T extends TransitEvent> void unsubscribe(EventType type, Class<T> eventClass, Observer<? super T> observer) {
        events.unsubscribe(type, eventClass, observer);
    }

    @Override
    public void publish(TransitEvent event) {
        events.publish(event);
    }
}
