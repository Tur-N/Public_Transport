package com.publictransport.model;

import com.publictransport.visitor.Visitor;

public record Tram(String id, int passengers, double energyKwh, boolean accessible) implements TransitNode {
    public Tram {
        if (id == null || passengers < 0 || energyKwh < 0) throw new IllegalArgumentException("Invalid tram");
    }

    @Override
    public <R> R accept(Visitor<R> visitor) { return visitor.visit(this); }
}
