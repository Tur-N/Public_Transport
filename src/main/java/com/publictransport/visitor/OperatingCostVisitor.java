package com.publictransport.visitor;

import com.publictransport.model.*;

public final class OperatingCostVisitor implements Visitor<Double> {
    private static final double DIESEL_PRICE = 290.0;
    private static final double ELECTRICITY_PRICE = 28.0;

    @Override public Double visit(TransitNetwork node) { return sum(node); }
    @Override public Double visit(Route node) { return sum(node); }
    @Override public Double visit(Stop node) { return sum(node); }
    @Override public Double visit(Bus bus) { return bus.fuelLitres() * DIESEL_PRICE; }
    @Override public Double visit(Tram tram) { return tram.energyKwh() * ELECTRICITY_PRICE; }
    @Override public Double visit(StationFacility station) { return station.maintenanceCost(); }
    @Override public Double visit(TicketMachine machine) { return machine.maintenanceCost(); }
    @Override public Double visit(BicycleDock dock) { return dock.maintenanceCost(); }

    private double sum(TransitContainer container) {
        return container.children().stream().mapToDouble(child -> child.accept(this)).sum();
    }
}
