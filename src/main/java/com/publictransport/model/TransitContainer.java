package com.publictransport.model;

import java.util.List;

public interface TransitContainer extends TransitNode {
    List<TransitNode> children();
}
