package com.publictransport.visitor;

import com.publictransport.model.*;

public final class NodeCountVisitor implements Visitor<Integer> {
    @Override public Integer visit(TransitNetwork node) { return countChildren(node); }
    @Override public Integer visit(Route node) { return countChildren(node); }
    @Override public Integer visit(Stop node) { return countChildren(node); }
    @Override public Integer visit(Bus node) { return 1; }
    @Override public Integer visit(Tram node) { return 1; }
    @Override public Integer visit(StationFacility node) { return 1; }
    @Override public Integer visit(TicketMachine node) { return 1; }
    @Override public Integer visit(BicycleDock node) { return 1; }

    private int countChildren(TransitContainer container) {
        return 1 + container.children().stream().mapToInt(child -> child.accept(this)).sum();
    }
}
