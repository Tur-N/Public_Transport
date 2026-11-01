package com.publictransport.concurrency;

import com.publictransport.event.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrencyTest {
    @Test
    void fourProducersPublishExactlyFourThousandEvents() throws Exception {
        EventSubject<TransitEvent> subject = new EventSubject<>();
        AtomicInteger received = new AtomicInteger();
        subject.subscribe(EventType.VEHICLE_ARRIVAL, VehicleArrivalEvent.class, event -> received.incrementAndGet());
        ExecutorService executor = Executors.newFixedThreadPool(4);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<?>> tasks = new ArrayList<>();
        for (int producer = 0; producer < 4; producer++) {
            final int producerId = producer;
            tasks.add(executor.submit(() -> {
                start.await();
                for (int i = 0; i < 1_000; i++) {
                    subject.publish(new VehicleArrivalEvent("R1", "S1", "B" + producerId,
                            1, 0, Instant.now()));
                }
                return null;
            }));
        }
        start.countDown();
        for (Future<?> task : tasks) task.get(10, TimeUnit.SECONDS);
        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        assertEquals(4_000, received.get());
    }

    @Test
    void simulationRequiresAtLeastThreeThreads() {
        assertThrows(IllegalArgumentException.class, () ->
                new TransitSimulation(new com.publictransport.source.VehicleArrivalSource(),
                        new com.publictransport.source.TicketSaleSource(), 2));
    }
}
