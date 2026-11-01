package com.publictransport.model;

import com.publictransport.visitor.Visitor;

public record Bus(String id, int passengers, double fuelLitres, boolean accessible) implements TransitNode {
    public Bus {
        if (id == null || passengers < 0 || fuelLitres < 0) throw new IllegalArgumentException("Invalid bus");
    }

    @Override
    public <R> R accept(Visitor<R> visitor) { return visitor.visit(this); }
}
