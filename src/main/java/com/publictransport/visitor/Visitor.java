package com.publictransport.visitor;

import com.publictransport.model.*;

public interface Visitor<R> {
    R visit(TransitNetwork network);
    R visit(Route route);
    R visit(Stop stop);
    R visit(Bus bus);
    R visit(Tram tram);
    R visit(StationFacility station);
    R visit(TicketMachine machine);
    R visit(BicycleDock dock);
}
