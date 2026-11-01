package com.publictransport.event;

@FunctionalInterface
public interface Observer<E extends TransitEvent> {
    void onEvent(E event);
}
