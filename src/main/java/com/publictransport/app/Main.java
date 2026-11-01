package com.publictransport.app;

import com.publictransport.concurrency.TransitSimulation;
import com.publictransport.event.*;
import com.publictransport.model.*;
import com.publictransport.observer.*;
import com.publictransport.source.TicketSaleSource;
import com.publictransport.source.VehicleArrivalSource;
import com.publictransport.visitor.*;

import java.util.List;
import java.util.Map;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        int threadCount = args.length > 0 ? Integer.parseInt(args[0]) : 4;

        Stop centralStop = new Stop("STOP-CENTRAL", List.of(
                new Bus("BUS-21", 35, 18.0, true),
                new Tram("TRAM-3", 42, 24.0, true),
                new StationFacility("LIFT-1", 1500.0, false),
                new TicketMachine("TM-2", 120, 800.0, true)));
        Stop riverStop = new Stop("STOP-RIVER", List.of(
                new Bus("BUS-11", 20, 12.0, false),
                new StationFacility("RAMP-2", 600.0, true),
                new BicycleDock("BIKE-DOCK-1", 12, 250.0, false)));
        Route route = new Route("R1", List.of(centralStop, riverStop));
        TransitNetwork network = new TransitNetwork("ASTANA-NETWORK", List.of(route));

        VehicleArrivalSource arrivals = new VehicleArrivalSource();
        TicketSaleSource ticketSales = new TicketSaleSource();
        StopDisplay stopDisplay = new StopDisplay();
        PassengerApp passengerApp = new PassengerApp();
        TicketRevenueTracker revenueTracker = new TicketRevenueTracker();
        RidershipAggregator aggregator = new RidershipAggregator();
        RidershipSummaryDisplay summaryDisplay = new RidershipSummaryDisplay();
        DelayInspectionService inspectionService = new DelayInspectionService(
                Map.of(centralStop.id(), centralStop, riverStop.id(), riverStop), 5,
                new AccessibilityFilterVisitor());
        InspectionReportDisplay inspectionDisplay = new InspectionReportDisplay();

        arrivals.subscribe(EventType.VEHICLE_ARRIVAL, VehicleArrivalEvent.class, stopDisplay);
        arrivals.subscribe(EventType.VEHICLE_ARRIVAL, VehicleArrivalEvent.class, passengerApp);
        arrivals.subscribe(EventType.VEHICLE_ARRIVAL, VehicleArrivalEvent.class, aggregator);
        arrivals.subscribe(EventType.VEHICLE_ARRIVAL, VehicleArrivalEvent.class, inspectionService);
        ticketSales.subscribe(EventType.TICKET_SALE, TicketSaleEvent.class, revenueTracker);
        aggregator.subscribe(EventType.RIDERSHIP_SUMMARY, RidershipSummaryEvent.class, summaryDisplay);
        inspectionService.subscribe(EventType.INSPECTION_COMPLETED, InspectionCompletedEvent.class, inspectionDisplay);

        arrivals.reportArrival("R1", centralStop.id(), "BUS-21", 35, 8);
        ticketSales.sell("R1", 2, 120.0);
        new TransitSimulation(arrivals, ticketSales, threadCount).run(10);

        System.out.println("Public Transport Demo");
        System.out.println("Arrival updates: " + stopDisplay.updates());
        System.out.println("Passenger app notifications: " + passengerApp.notifications());
        System.out.println("Tickets sold: " + revenueTracker.ticketsSold());
        System.out.println("Ticket revenue: " + revenueTracker.revenue());
        System.out.println("Aggregated passengers: " + aggregator.passengersFor("R1"));
        System.out.println("Inspection reports: " + inspectionDisplay.reports());
        System.out.println("Operating cost: " + network.accept(new OperatingCostVisitor()));
        System.out.println("JSON: " + network.accept(new JsonExportVisitor()));
    }
}
