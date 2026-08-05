package com.printxpress.app.database;

import com.printxpress.app.database.entities.Order;
import java.util.List;

public final class OrderProgressor {
    private static final long STAGE_MS = 120_000L;

    private OrderProgressor() {}

    public static synchronized boolean refresh(AppDatabase db, Order order) {
        if (order == null || "CANCELLED".equals(order.status) || "DELIVERED".equals(order.status)) {
            return false;
        }

        Order current = db.orderDao().getById(order.id);
        if (current == null) {
            return false;
        }

        if (!current.status.equals(order.status)) {
            order.status = current.status;
            order.statusUpdatedAt = current.statusUpdatedAt;
            return false;
        }

        long age = Math.max(0, System.currentTimeMillis() - order.orderDate);
        String next = "PROCESSING";

        if (age >= STAGE_MS * 3) {
            next = "DELIVERED";
        } else if (age >= STAGE_MS * 2) {
            next = "READY";
        } else if (age >= STAGE_MS) {
            next = "PRINTING";
        }

        if (next.equals(order.status)) {
            return false;
        }

        db.orderDao().updateStatusWithTime(order.id, next, System.currentTimeMillis());
        order.status = next;
        order.statusUpdatedAt = System.currentTimeMillis();
        OrderEvents.statusChanged(db, order, next);
        return true;
    }

    public static boolean refreshAll(AppDatabase db, List<Order> orders) {
        boolean changed = false;
        for (Order o : orders) {
            changed |= refresh(db, o);
        }
        return changed;
    }
}
