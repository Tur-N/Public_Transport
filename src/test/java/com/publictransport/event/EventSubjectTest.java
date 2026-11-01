package com.publictransport.event;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class EventSubjectTest {
    private VehicleArrivalEvent arrival() {
        return new VehicleArrivalEvent("R1", "S1", "B1", 12, 3, Instant.now());
    }

    @Test
    void publishesToMatchingEventType() {
        EventSubject<TransitEvent> subject = new EventSubject<>();
        AtomicInteger calls = new AtomicInteger();
        subject.subscribe(EventType.VEHICLE_ARRIVAL, VehicleArrivalEvent.class, event -> calls.incrementAndGet());
        subject.publish(arrival());
        assertEquals(1, calls.get());
    }

    @Test
    void doesNotPublishToAnotherEventType() {
        EventSubject<TransitEvent> subject = new EventSubject<>();
        AtomicInteger calls = new AtomicInteger();
        subject.subscribe(EventType.TICKET_SALE, TicketSaleEvent.class, event -> calls.incrementAndGet());
        subject.publish(arrival());
        assertEquals(0, calls.get());
    }

    @Test
    void unsubscribeStopsFutureNotifications() {
        EventSubject<TransitEvent> subject = new EventSubject<>();
        AtomicInteger calls = new AtomicInteger();
        Observer<VehicleArrivalEvent> listener = event -> calls.incrementAndGet();
        subject.subscribe(EventType.VEHICLE_ARRIVAL, VehicleArrivalEvent.class, listener);
        subject.unsubscribe(EventType.VEHICLE_ARRIVAL, VehicleArrivalEvent.class, listener);
        subject.publish(arrival());
        assertEquals(0, calls.get());
    }

    @Test
    void listenerCanSubscribeFromInsideHandler() {
        EventSubject<TransitEvent> subject = new EventSubject<>();
        AtomicInteger newListenerCalls = new AtomicInteger();
        Observer<VehicleArrivalEvent> lateListener = event -> newListenerCalls.incrementAndGet();
        AtomicInteger firstCalls = new AtomicInteger();
        subject.subscribe(EventType.VEHICLE_ARRIVAL, VehicleArrivalEvent.class, event -> {
            if (firstCalls.incrementAndGet() == 1) {
                subject.subscribe(EventType.VEHICLE_ARRIVAL, VehicleArrivalEvent.class, lateListener);
            }
        });
        subject.publish(arrival());
        assertEquals(0, newListenerCalls.get());
        subject.publish(arrival());
        assertEquals(1, newListenerCalls.get());
    }

    @Test
    void listenerCanUnsubscribeFromInsideHandler() {
        EventSubject<TransitEvent> subject = new EventSubject<>();
        AtomicInteger calls = new AtomicInteger();
        @SuppressWarnings("unchecked") Observer<VehicleArrivalEvent>[] self = new Observer[1];
        self[0] = event -> {
            calls.incrementAndGet();
            subject.unsubscribe(EventType.VEHICLE_ARRIVAL, VehicleArrivalEvent.class, self[0]);
        };
        subject.subscribe(EventType.VEHICLE_ARRIVAL, VehicleArrivalEvent.class, self[0]);
        assertDoesNotThrow(() -> subject.publish(arrival()));
        subject.publish(arrival());
        assertEquals(1, calls.get());
    }

    @Test
    void eventRecordsCopyMutableCollections() {
        java.util.ArrayList<String> items = new java.util.ArrayList<>(List.of("lift"));
        InspectionCompletedEvent event = new InspectionCompletedEvent("R1", "S1", 6, items, Instant.now());
        items.add("ramp");
        assertEquals(List.of("lift"), event.inaccessibleItems());
        assertThrows(UnsupportedOperationException.class, () -> event.inaccessibleItems().add("stairs"));
    }

    @Test
    void rejectsNullEvent() {
        EventSubject<TransitEvent> subject = new EventSubject<>();
        assertThrows(IllegalArgumentException.class, () -> subject.publish(null));
    }
}
