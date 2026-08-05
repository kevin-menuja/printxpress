package com.printxpress.app.database;

import com.printxpress.app.database.entities.Notification;
import com.printxpress.app.database.entities.Order;

public final class OrderEvents {

    private OrderEvents() {}

    public static void notify(AppDatabase db, long userId, String title, String message, String type) {
        db.notificationDao().insert(new Notification(
            userId, 
            title, 
            message, 
            type, 
            false, 
            System.currentTimeMillis()
        ));
    }

    public static void placed(AppDatabase db, Order order) {
        notify(
            db, 
            order.userId, 
            "Order confirmed", 
            "Order #" + order.orderId + " has been placed successfully.", 
            "order_update"
        );
    }

    public static void cancelled(AppDatabase db, Order order) {
        notify(
            db, 
            order.userId, 
            "Order cancelled", 
            "Order #" + order.orderId + " was cancelled.", 
            "order_update"
        );
    }

    public static void rescheduled(AppDatabase db, Order order) {
        notify(
            db, 
            order.userId, 
            "Order rescheduled", 
            "The required date for order #" + order.orderId + " was updated.", 
            "order_update"
        );
    }

    public static void statusChanged(AppDatabase db, Order order, String status) {
        String readable = "READY".equals(status) 
            ? ("DELIVERY".equals(order.deliveryType) ? "out for delivery" : "ready for pickup") 
            : status.toLowerCase();
            
        notify(
            db, 
            order.userId, 
            "Order update", 
            "Order #" + order.orderId + " is " + readable + ".", 
            "order_update"
        );
    }
}
