package com.publictransport.model;

import com.publictransport.visitor.Visitor;

public record TicketMachine(String id, int ticketsSold, double maintenanceCost, boolean accessible)
        implements TransitNode {
    public TicketMachine {
        if (id == null || ticketsSold < 0 || maintenanceCost < 0) {
            throw new IllegalArgumentException("Invalid ticket machine");
        }
    }

    @Override
    public <R> R accept(Visitor<R> visitor) { return visitor.visit(this); }
}
