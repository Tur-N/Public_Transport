package com.publictransport.visitor;

import com.publictransport.TestFixtures;
import com.publictransport.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VisitorTest {
    @Test
    void calculatesOperatingCostWithHandCalculatedValues() {
        double cost = TestFixtures.centralStop().accept(new OperatingCostVisitor());
        assertEquals(679.0, cost, 0.0001);
    }

    @Test
    void ridershipVisitorAccumulatesPassengersAcrossSubtree() {
        RidershipReport report = TestFixtures.network().accept(new RidershipVisitor());
        assertEquals(new RidershipReport(60, 3, 2), report);
    }

    @Test
    void ridershipVisitorCanBeReusedAndResetsState() {
        RidershipVisitor visitor = new RidershipVisitor();
        visitor.analyze(TestFixtures.network());
        assertEquals(new RidershipReport(50, 2, 2), visitor.analyze(TestFixtures.centralStop()));
    }

    @Test
    void filterReturnsOnlyInaccessibleElements() {
        List<TransitNode> result = TestFixtures.centralStop().accept(new AccessibilityFilterVisitor());
        assertEquals(List.of("T1", "STA1"), result.stream().map(TransitNode::id).toList());
    }

    @Test
    void jsonExportKeepsCorrectNesting() {
        String json = TestFixtures.network().accept(new JsonExportVisitor());
        assertTrue(json.startsWith("{\"type\":\"network\""));
        assertTrue(json.contains("\"routes\":[{\"type\":\"route\""));
        assertTrue(json.contains("\"stops\":[{\"type\":\"stop\""));
        assertTrue(json.contains("\"elements\":[{\"type\":\"bus\""));
    }

    @Test
    void jsonExportEscapesSpecialCharacters() {
        TransitNetwork network = new TransitNetwork("Network\"A", List.of());
        assertTrue(network.accept(new JsonExportVisitor()).contains("Network\\\"A"));
    }

    @Test
    void stopVisitorDoesNotInspectOtherStops() {
        Stop selected = new Stop("ONLY", List.of(new Bus("BAD", 1, 1.0, false)));
        List<TransitNode> issues = selected.accept(new AccessibilityFilterVisitor());
        assertEquals(List.of("BAD"), issues.stream().map(TransitNode::id).toList());
    }

    @Test
    void containerChildrenAreImmutable() {
        java.util.ArrayList<TransitNode> children = new java.util.ArrayList<>();
        children.add(new Bus("B", 1, 1.0, true));
        Stop stop = new Stop("S", children);
        children.clear();
        assertEquals(1, stop.children().size());
        assertThrows(UnsupportedOperationException.class, () -> stop.children().clear());
    }
}
