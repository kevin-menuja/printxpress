package com.printxpress.app.database.entities;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "orders", indices = @Index("userId"))
public class Order {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long userId;
    public long productId;
    public String productName;
    public String category;
    public String material;
    public String size;
    public String customText;
    public String deliveryType;
    public String deliveryAddress;
    public String paymentMethod;
    public String status;
    public String orderId;
    public String designUri;
    public int quantity;
    public long requiredDate;
    public long orderDate;
    public long statusUpdatedAt;
    public double totalPrice;

    public Order(long userId, long productId, String productName, String category,
                 int quantity, String material, String size, String customText,
                 String deliveryType, String deliveryAddress, String paymentMethod,
                 long requiredDate, double totalPrice, String status,
                 long orderDate, String orderId, String designUri) {
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.quantity = quantity;
        this.material = material;
        this.size = size;
        this.customText = customText;
        this.deliveryType = deliveryType;
        this.deliveryAddress = deliveryAddress;
        this.paymentMethod = paymentMethod;
        this.requiredDate = requiredDate;
        this.totalPrice = totalPrice;
        this.status = status;
        this.orderDate = orderDate;
        this.statusUpdatedAt = orderDate;
        this.orderId = orderId;
        this.designUri = designUri;
    }
}
