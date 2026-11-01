package com.publictransport.observer;

import com.publictransport.event.*;
import com.publictransport.model.Stop;
import com.publictransport.model.TransitNode;
import com.publictransport.visitor.AccessibilityFilterVisitor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class DelayInspectionService implements Observer<VehicleArrivalEvent>, Subject<TransitEvent> {
    private final Map<String, Stop> stopsById;
    private final int delayThresholdMinutes;
    private final AccessibilityFilterVisitor inspectionVisitor;
    private final EventSubject<TransitEvent> inspectionEvents = new EventSubject<>();

    public DelayInspectionService(Map<String, Stop> stopsById, int delayThresholdMinutes,
                                  AccessibilityFilterVisitor inspectionVisitor) {
        if (delayThresholdMinutes < 0) throw new IllegalArgumentException("Delay threshold cannot be negative");
        this.stopsById = Map.copyOf(stopsById);
        this.delayThresholdMinutes = delayThresholdMinutes;
        this.inspectionVisitor = inspectionVisitor;
    }

    @Override
    public void onEvent(VehicleArrivalEvent event) {
        if (event.delayMinutes() < delayThresholdMinutes) return;
        Stop stop = stopsById.get(event.stopId());
        if (stop == null) return;
        List<String> inaccessibleIds = stop.accept(inspectionVisitor).stream().map(TransitNode::id).toList();
        inspectionEvents.publish(new InspectionCompletedEvent(event.routeId(), stop.id(), event.delayMinutes(),
                inaccessibleIds, Instant.now()));
    }

    @Override
    public <T extends TransitEvent> void subscribe(EventType type, Class<T> eventClass, Observer<? super T> observer) {
        inspectionEvents.subscribe(type, eventClass, observer);
    }

    @Override
    public <T extends TransitEvent> void unsubscribe(EventType type, Class<T> eventClass, Observer<? super T> observer) {
        inspectionEvents.unsubscribe(type, eventClass, observer);
    }

    @Override
    public void publish(TransitEvent event) {
        inspectionEvents.publish(event);
    }
}
