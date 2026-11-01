package com.publictransport.visitor;

import com.publictransport.model.BicycleDock;
import com.publictransport.model.Stop;
import com.publictransport.model.TransitNode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BicycleDockVisitorTest {
    @Test
    void operatingCostVisitorHandlesNewElement() {
        double cost = new BicycleDock("D1", 10, 250.0, true).accept(new OperatingCostVisitor());
        assertEquals(250.0, cost, 0.0001);
    }

    @Test
    void ridershipVisitorCountsDockAsFacility() {
        RidershipReport report = new RidershipVisitor().analyze(
                new Stop("S1", List.of(new BicycleDock("D1", 10, 250.0, false))));
        assertEquals(new RidershipReport(0, 0, 1), report);
    }

    @Test
    void accessibilityVisitorFindsInaccessibleDock() {
        List<TransitNode> result = new BicycleDock("D1", 10, 250.0, false)
                .accept(new AccessibilityFilterVisitor());
        assertEquals(List.of("D1"), result.stream().map(TransitNode::id).toList());
    }

    @Test
    void jsonVisitorExportsBicycleDockFields() {
        String json = new BicycleDock("D1", 10, 250.0, true).accept(new JsonExportVisitor());
        assertTrue(json.contains("\"type\":\"bicycleDock\""));
        assertTrue(json.contains("\"availableBikes\":10"));
    }

    @Test
    void nodeCountVisitorHandlesNewElement() {
        int count = new BicycleDock("D1", 10, 250.0, true).accept(new NodeCountVisitor());
        assertEquals(1, count);
    }
}
