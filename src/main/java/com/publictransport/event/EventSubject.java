package com.publictransport.event;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class EventSubject<E extends TransitEvent> implements Subject<E> {
    private final ConcurrentHashMap<EventType, CopyOnWriteArrayList<Subscription>> registry =
            new ConcurrentHashMap<>();

    @Override
    public <T extends E> void subscribe(EventType type, Class<T> eventClass, Observer<? super T> observer) {
        if (type == null || eventClass == null || observer == null) {
            throw new IllegalArgumentException("Subscription values cannot be null");
        }
        registry.computeIfAbsent(type, ignored -> new CopyOnWriteArrayList<>())
                .addIfAbsent(new Subscription(eventClass, observer));
    }

    @Override
    public <T extends E> void unsubscribe(EventType type, Class<T> eventClass, Observer<? super T> observer) {
        CopyOnWriteArrayList<Subscription> subscriptions = registry.get(type);
        if (subscriptions != null) {
            subscriptions.removeIf(subscription -> subscription.matches(eventClass, observer));
        }
    }

    @Override
    public void publish(E event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        CopyOnWriteArrayList<Subscription> subscriptions = registry.get(event.type());
        if (subscriptions != null) {
            for (Subscription subscription : subscriptions) {
                subscription.dispatch(event);
            }
        }
    }

    private static final class Subscription {
        private final Class<? extends TransitEvent> eventClass;
        private final Object observer;
        private final Consumer<TransitEvent> callback;

        private <T extends TransitEvent> Subscription(Class<T> eventClass, Observer<? super T> observer) {
            this.eventClass = eventClass;
            this.observer = observer;
            this.callback = event -> observer.onEvent(eventClass.cast(event));
        }

        private void dispatch(TransitEvent event) {
            if (eventClass.isInstance(event)) {
                callback.accept(event);
            }
        }

        private boolean matches(Class<?> candidateClass, Object candidateObserver) {
            return eventClass.equals(candidateClass) && observer.equals(candidateObserver);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof Subscription that)) return false;
            return eventClass.equals(that.eventClass) && observer.equals(that.observer);
        }

        @Override
        public int hashCode() {
            return 31 * eventClass.hashCode() + observer.hashCode();
        }
    }
}
