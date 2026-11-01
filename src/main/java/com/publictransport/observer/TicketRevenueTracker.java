package com.publictransport.observer;

import com.publictransport.event.Observer;
import com.publictransport.event.TicketSaleEvent;

import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

public final class TicketRevenueTracker implements Observer<TicketSaleEvent> {
    private final DoubleAdder revenue = new DoubleAdder();
    private final LongAdder tickets = new LongAdder();

    @Override
    public void onEvent(TicketSaleEvent event) {
        revenue.add(event.quantity() * event.unitPrice());
        tickets.add(event.quantity());
    }

    public double revenue() { return revenue.sum(); }
    public long ticketsSold() { return tickets.sum(); }
}
