package com.publictransport.observer;

import com.publictransport.event.Observer;
import com.publictransport.event.VehicleArrivalEvent;

import java.util.concurrent.atomic.AtomicInteger;

public final class PassengerApp implements Observer<VehicleArrivalEvent> {
    private final AtomicInteger notifications = new AtomicInteger();

    @Override
    public void onEvent(VehicleArrivalEvent event) {
        notifications.incrementAndGet();
    }

    public int notifications() { return notifications.get(); }
}
