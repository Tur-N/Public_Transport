package com.publictransport.visitor;

import com.publictransport.TestFixtures;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NodeCountVisitorTest {
    @Test
    void countsEveryNodeWithoutChangingAnyElementClass() {
        assertEquals(9, TestFixtures.network().accept(new NodeCountVisitor()));
    }
}
