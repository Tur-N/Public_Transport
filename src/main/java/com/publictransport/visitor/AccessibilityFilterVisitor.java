package com.publictransport.visitor;

import com.publictransport.model.*;

import java.util.ArrayList;
import java.util.List;

public final class AccessibilityFilterVisitor implements Visitor<List<TransitNode>> {
    @Override public List<TransitNode> visit(TransitNetwork node) { return collect(node); }
    @Override public List<TransitNode> visit(Route node) { return collect(node); }
    @Override public List<TransitNode> visit(Stop node) { return collect(node); }
    @Override public List<TransitNode> visit(Bus bus) { return bus.accessible() ? List.of() : List.of(bus); }
    @Override public List<TransitNode> visit(Tram tram) { return tram.accessible() ? List.of() : List.of(tram); }
    @Override public List<TransitNode> visit(StationFacility station) { return station.accessible() ? List.of() : List.of(station); }
    @Override public List<TransitNode> visit(TicketMachine machine) { return machine.accessible() ? List.of() : List.of(machine); }

    private List<TransitNode> collect(TransitContainer container) {
        List<TransitNode> result = new ArrayList<>();
        container.children().forEach(child -> result.addAll(child.accept(this)));
        return List.copyOf(result);
    }
}
