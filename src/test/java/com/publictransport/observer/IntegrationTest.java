package com.publictransport.observer;

import com.publictransport.TestFixtures;
import com.publictransport.event.*;
import com.publictransport.model.Stop;
import com.publictransport.source.TicketSaleSource;
import com.publictransport.source.VehicleArrivalSource;
import com.publictransport.visitor.AccessibilityFilterVisitor;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationTest {
    @Test
    void delayedArrivalTriggersInspectionOfRelatedStop() {
        Stop stop = TestFixtures.centralStop();
        DelayInspectionService service = new DelayInspectionService(Map.of(stop.id(), stop), 5,
                new AccessibilityFilterVisitor());
        InspectionReportDisplay display = new InspectionReportDisplay();
        service.subscribe(EventType.INSPECTION_COMPLETED, InspectionCompletedEvent.class, display);
        service.onEvent(new VehicleArrivalEvent("R1", "S1", "B1", 10, 8, Instant.now()));
        assertEquals(1, display.reports());
        assertEquals("S1", display.lastReport().stopId());
        assertEquals(java.util.List.of("T1", "STA1"), display.lastReport().inaccessibleItems());
    }

    @Test
    void smallDelayDoesNotTriggerInspection() {
        Stop stop = TestFixtures.centralStop();
        DelayInspectionService service = new DelayInspectionService(Map.of(stop.id(), stop), 5,
                new AccessibilityFilterVisitor());
        InspectionReportDisplay display = new InspectionReportDisplay();
        service.subscribe(EventType.INSPECTION_COMPLETED, InspectionCompletedEvent.class, display);
        service.onEvent(new VehicleArrivalEvent("R1", "S1", "B1", 10, 2, Instant.now()));
        assertEquals(0, display.reports());
    }

    @Test
    void unknownStopDoesNotTriggerInspection() {
        DelayInspectionService service = new DelayInspectionService(Map.of(), 5, new AccessibilityFilterVisitor());
        InspectionReportDisplay display = new InspectionReportDisplay();
        service.subscribe(EventType.INSPECTION_COMPLETED, InspectionCompletedEvent.class, display);
        service.onEvent(new VehicleArrivalEvent("R1", "missing", "B1", 10, 9, Instant.now()));
        assertEquals(0, display.reports());
    }

    @Test
    void aggregatorRepublishesAggregatedData() {
        RidershipAggregator aggregator = new RidershipAggregator();
        AtomicReference<RidershipSummaryEvent> received = new AtomicReference<>();
        aggregator.subscribe(EventType.RIDERSHIP_SUMMARY, RidershipSummaryEvent.class, received::set);
        aggregator.onEvent(new VehicleArrivalEvent("R1", "S1", "B1", 17, 0, Instant.now()));
        aggregator.onEvent(new VehicleArrivalEvent("R1", "S1", "B2", 13, 0, Instant.now()));
        assertEquals(30, aggregator.passengersFor("R1"));
        assertEquals(30, received.get().totalPassengers());
        assertEquals(2, received.get().arrivalCount());
    }

    @Test
    void aggregatorIsAlsoASubject() {
        RidershipAggregator aggregator = new RidershipAggregator();
        RidershipSummaryDisplay display = new RidershipSummaryDisplay();
        aggregator.subscribe(EventType.RIDERSHIP_SUMMARY, RidershipSummaryEvent.class, display);
        aggregator.onEvent(new VehicleArrivalEvent("R2", "S2", "T1", 9, 0, Instant.now()));
        assertNotNull(display.lastSummary());
    }

    @Test
    void ticketRevenueTrackerTotalsSales() {
        TicketSaleSource source = new TicketSaleSource();
        TicketRevenueTracker tracker = new TicketRevenueTracker();
        source.subscribe(EventType.TICKET_SALE, TicketSaleEvent.class, tracker);
        source.sell("R1", 2, 120.0);
        source.sell("R1", 3, 100.0);
        assertEquals(5, tracker.ticketsSold());
        assertEquals(540.0, tracker.revenue(), 0.0001);
    }

    @Test
    void arrivalSourceNotifiesBothDisplayAndApp() {
        VehicleArrivalSource source = new VehicleArrivalSource();
        StopDisplay display = new StopDisplay();
        PassengerApp app = new PassengerApp();
        source.subscribe(EventType.VEHICLE_ARRIVAL, VehicleArrivalEvent.class, display);
        source.subscribe(EventType.VEHICLE_ARRIVAL, VehicleArrivalEvent.class, app);
        source.reportArrival("R1", "S1", "B1", 5, 0);
        assertEquals(1, display.updates());
        assertEquals(1, app.notifications());
    }
}
