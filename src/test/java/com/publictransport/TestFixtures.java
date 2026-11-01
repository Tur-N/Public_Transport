package com.publictransport;

import com.publictransport.model.*;

import java.util.List;

public final class TestFixtures {
    private TestFixtures() { }

    public static Stop centralStop() {
        return new Stop("S1", List.of(
                new Bus("B1", 20, 2.0, true),
                new Tram("T1", 30, 3.0, false),
                new StationFacility("STA1", 10.0, false),
                new TicketMachine("M1", 50, 5.0, true)));
    }

    public static TransitNetwork network() {
        Stop first = centralStop();
        Stop second = new Stop("S2", List.of(new Bus("B2", 10, 1.0, false)));
        return new TransitNetwork("N1", List.of(new Route("R1", List.of(first, second))));
    }
}
