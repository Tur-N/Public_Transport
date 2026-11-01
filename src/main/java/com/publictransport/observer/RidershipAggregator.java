package com.publictransport.observer;

import com.publictransport.event.*;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

public final class RidershipAggregator implements Observer<VehicleArrivalEvent>, Subject<TransitEvent> {
    private final EventSubject<TransitEvent> summaryEvents = new EventSubject<>();
    private final ConcurrentHashMap<String, RouteTotals> totalsByRoute = new ConcurrentHashMap<>();

    @Override
    public void onEvent(VehicleArrivalEvent event) {
        RouteTotals totals = totalsByRoute.computeIfAbsent(event.routeId(), ignored -> new RouteTotals());
        totals.passengers.add(event.passengers());
        totals.arrivals.increment();
        summaryEvents.publish(new RidershipSummaryEvent(event.routeId(), totals.passengers.sum(),
                totals.arrivals.sum(), Instant.now()));
    }

    public long passengersFor(String routeId) {
        RouteTotals totals = totalsByRoute.get(routeId);
        return totals == null ? 0 : totals.passengers.sum();
    }

    @Override
    public <T extends TransitEvent> void subscribe(EventType type, Class<T> eventClass, Observer<? super T> observer) {
        summaryEvents.subscribe(type, eventClass, observer);
    }

    @Override
    public <T extends TransitEvent> void unsubscribe(EventType type, Class<T> eventClass, Observer<? super T> observer) {
        summaryEvents.unsubscribe(type, eventClass, observer);
    }

    @Override
    public void publish(TransitEvent event) {
        summaryEvents.publish(event);
    }

    private static final class RouteTotals {
        private final LongAdder passengers = new LongAdder();
        private final LongAdder arrivals = new LongAdder();
    }
}
