package com.publictransport.event;

public interface Subject<E extends TransitEvent> {
    <T extends E> void subscribe(EventType type, Class<T> eventClass, Observer<? super T> observer);
    <T extends E> void unsubscribe(EventType type, Class<T> eventClass, Observer<? super T> observer);
    void publish(E event);
}
