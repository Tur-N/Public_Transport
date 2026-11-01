package com.publictransport.model;

import com.publictransport.visitor.Visitor;

public record StationFacility(String id, double maintenanceCost, boolean accessible) implements TransitNode {
    public StationFacility {
        if (id == null || maintenanceCost < 0) throw new IllegalArgumentException("Invalid station facility");
    }

    @Override
    public <R> R accept(Visitor<R> visitor) { return visitor.visit(this); }
}
