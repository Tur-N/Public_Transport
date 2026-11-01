package com.publictransport.model;

import com.publictransport.visitor.Visitor;

import java.util.List;

public record TransitNetwork(String id, List<TransitNode> children) implements TransitContainer {
    public TransitNetwork {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Network id is required");
        children = List.copyOf(children);
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}
