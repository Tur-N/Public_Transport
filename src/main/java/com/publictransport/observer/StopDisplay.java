package com.publictransport.observer;

import com.publictransport.event.Observer;
import com.publictransport.event.VehicleArrivalEvent;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class StopDisplay implements Observer<VehicleArrivalEvent> {
    private final AtomicInteger updates = new AtomicInteger();
    private final AtomicReference<VehicleArrivalEvent> lastArrival = new AtomicReference<>();

    @Override
    public void onEvent(VehicleArrivalEvent event) {
        lastArrival.set(event);
        updates.incrementAndGet();
    }

    public int updates() { return updates.get(); }
    public VehicleArrivalEvent lastArrival() { return lastArrival.get(); }
}
