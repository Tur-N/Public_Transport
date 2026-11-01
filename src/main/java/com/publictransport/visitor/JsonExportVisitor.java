package com.publictransport.visitor;

import com.publictransport.model.*;

import java.util.stream.Collectors;

public final class JsonExportVisitor implements Visitor<String> {
    @Override public String visit(TransitNetwork node) {
        return "{\"type\":\"network\",\"id\":" + quote(node.id()) + ",\"routes\":" + children(node) + "}";
    }
    @Override public String visit(Route node) {
        return "{\"type\":\"route\",\"id\":" + quote(node.id()) + ",\"stops\":" + children(node) + "}";
    }
    @Override public String visit(Stop node) {
        return "{\"type\":\"stop\",\"id\":" + quote(node.id()) + ",\"elements\":" + children(node) + "}";
    }
    @Override public String visit(Bus bus) {
        return "{\"type\":\"bus\",\"id\":" + quote(bus.id()) + ",\"passengers\":" + bus.passengers()
                + ",\"fuelLitres\":" + bus.fuelLitres() + ",\"accessible\":" + bus.accessible() + "}";
    }
    @Override public String visit(Tram tram) {
        return "{\"type\":\"tram\",\"id\":" + quote(tram.id()) + ",\"passengers\":" + tram.passengers()
                + ",\"energyKwh\":" + tram.energyKwh() + ",\"accessible\":" + tram.accessible() + "}";
    }
    @Override public String visit(StationFacility station) {
        return "{\"type\":\"stationFacility\",\"id\":" + quote(station.id()) + ",\"maintenanceCost\":"
                + station.maintenanceCost() + ",\"accessible\":" + station.accessible() + "}";
    }
    @Override public String visit(TicketMachine machine) {
        return "{\"type\":\"ticketMachine\",\"id\":" + quote(machine.id()) + ",\"ticketsSold\":"
                + machine.ticketsSold() + ",\"maintenanceCost\":" + machine.maintenanceCost()
                + ",\"accessible\":" + machine.accessible() + "}";
    }
    @Override public String visit(BicycleDock dock) {
        return "{\"type\":\"bicycleDock\",\"id\":" + quote(dock.id()) + ",\"availableBikes\":"
                + dock.availableBikes() + ",\"maintenanceCost\":" + dock.maintenanceCost()
                + ",\"accessible\":" + dock.accessible() + "}";
    }

    private String children(TransitContainer container) {
        return container.children().stream().map(child -> child.accept(this)).collect(Collectors.joining(",", "[", "]"));
    }

    private String quote(String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r") + "\"";
    }
}
