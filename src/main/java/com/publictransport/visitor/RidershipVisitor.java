package com.publictransport.visitor;

import com.publictransport.model.*;

public final class RidershipVisitor implements Visitor<RidershipReport> {
    private int passengers;
    private int vehicles;
    private int facilities;

    public RidershipReport analyze(TransitNode root) {
        passengers = 0;
        vehicles = 0;
        facilities = 0;
        root.accept(this);
        return snapshot();
    }

    @Override public RidershipReport visit(TransitNetwork node) { return traverse(node); }
    @Override public RidershipReport visit(Route node) { return traverse(node); }
    @Override public RidershipReport visit(Stop node) { return traverse(node); }
    @Override public RidershipReport visit(Bus bus) { passengers += bus.passengers(); vehicles++; return snapshot(); }
    @Override public RidershipReport visit(Tram tram) { passengers += tram.passengers(); vehicles++; return snapshot(); }
    @Override public RidershipReport visit(StationFacility station) { facilities++; return snapshot(); }
    @Override public RidershipReport visit(TicketMachine machine) { facilities++; return snapshot(); }
    @Override public RidershipReport visit(BicycleDock dock) { facilities++; return snapshot(); }

    private RidershipReport traverse(TransitContainer container) {
        container.children().forEach(child -> child.accept(this));
        return snapshot();
    }

    private RidershipReport snapshot() {
        return new RidershipReport(passengers, vehicles, facilities);
    }
}
