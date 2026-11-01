package com.publictransport.source;

import com.publictransport.event.*;

import java.time.Instant;

public final class VehicleArrivalSource implements Subject<TransitEvent> {
    private final EventSubject<TransitEvent> events = new EventSubject<>();

    public void reportArrival(String routeId, String stopId, String vehicleId, int passengers, int delayMinutes) {
        publish(new VehicleArrivalEvent(routeId, stopId, vehicleId, passengers, delayMinutes, Instant.now()));
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
