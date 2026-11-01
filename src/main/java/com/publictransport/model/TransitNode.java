package com.publictransport.model;

import com.publictransport.visitor.Visitor;

public interface TransitNode {
    String id();
    <R> R accept(Visitor<R> visitor);
}
