package com.publictransport.model;

import com.publictransport.visitor.Visitor;

public record BicycleDock(String id, int availableBikes, double maintenanceCost, boolean accessible)
        implements TransitNode {
    public BicycleDock {
        if (id == null || availableBikes < 0 || maintenanceCost < 0) {
            throw new IllegalArgumentException("Invalid bicycle dock");
        }
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}
