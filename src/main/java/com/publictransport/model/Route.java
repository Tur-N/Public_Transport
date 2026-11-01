package com.publictransport.model;

import com.publictransport.visitor.Visitor;

import java.util.List;

public record Route(String id, List<TransitNode> children) implements TransitContainer {
    public Route {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Route id is required");
        children = List.copyOf(children);
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}
