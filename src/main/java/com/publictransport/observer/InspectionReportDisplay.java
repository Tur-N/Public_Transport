package com.publictransport.observer;

import com.publictransport.event.InspectionCompletedEvent;
import com.publictransport.event.Observer;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class InspectionReportDisplay implements Observer<InspectionCompletedEvent> {
    private final AtomicInteger reports = new AtomicInteger();
    private final AtomicReference<InspectionCompletedEvent> lastReport = new AtomicReference<>();

    @Override
    public void onEvent(InspectionCompletedEvent event) {
        lastReport.set(event);
        reports.incrementAndGet();
    }

    public int reports() { return reports.get(); }
    public InspectionCompletedEvent lastReport() { return lastReport.get(); }
}
