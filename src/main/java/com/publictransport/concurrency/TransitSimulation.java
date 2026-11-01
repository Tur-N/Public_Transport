package com.publictransport.concurrency;

import com.publictransport.source.TicketSaleSource;
import com.publictransport.source.VehicleArrivalSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public final class TransitSimulation {
    private final VehicleArrivalSource arrivals;
    private final TicketSaleSource ticketSales;
    private final int threadCount;

    public TransitSimulation(VehicleArrivalSource arrivals, TicketSaleSource ticketSales, int threadCount) {
        if (threadCount < 3) throw new IllegalArgumentException("Use at least 3 producer threads");
        this.arrivals = arrivals;
        this.ticketSales = ticketSales;
        this.threadCount = threadCount;
    }

    public void run(int eventsPerProducer) {
        if (eventsPerProducer < 0) throw new IllegalArgumentException("Event count cannot be negative");
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<?>> tasks = new ArrayList<>();
        try {
            for (int producer = 0; producer < 3; producer++) {
                final int producerId = producer;
                tasks.add(executor.submit(() -> {
                    for (int i = 0; i < eventsPerProducer; i++) {
                        arrivals.reportArrival("R1", "S1", "BUS-" + producerId,
                                10 + producerId, i % 7);
                        if (i % 2 == 0) ticketSales.sell("R1", 1, 120.0);
                    }
                }));
            }
            for (Future<?> task : tasks) task.get();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Simulation interrupted", exception);
        } catch (ExecutionException exception) {
            throw new IllegalStateException("Producer failed", exception.getCause());
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                    if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                        throw new IllegalStateException("Producer threads did not stop");
                    }
                }
            } catch (InterruptedException exception) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Shutdown interrupted", exception);
            }
        }
    }
}
