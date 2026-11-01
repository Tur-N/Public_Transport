package com.publictransport.observer;

import com.publictransport.event.Observer;
import com.publictransport.event.RidershipSummaryEvent;

import java.util.concurrent.atomic.AtomicReference;

public final class RidershipSummaryDisplay implements Observer<RidershipSummaryEvent> {
    private final AtomicReference<RidershipSummaryEvent> lastSummary = new AtomicReference<>();

    @Override
    public void onEvent(RidershipSummaryEvent event) {
        lastSummary.set(event);
    }

    public RidershipSummaryEvent lastSummary() { return lastSummary.get(); }
}
