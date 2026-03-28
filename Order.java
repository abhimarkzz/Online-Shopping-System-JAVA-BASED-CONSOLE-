package com.shopping.models;

import com.shopping.datastructures.CustomLinkedList;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status { PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED }

    private String                    orderId;
    private int                       customerId;
    private String                    customerName;
    private String                    customerEmail;
    private String                    shippingAddress;
    private CustomLinkedList<CartItem> items;
    private double                    totalAmount;
    private Status                    status;
    private String                    orderDate;

    public Order() {
        items  = new CustomLinkedList<>();
        status = Status.PENDING;
        orderDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public Order(String orderId, int customerId, String customerName,
                 String customerEmail, String shippingAddress,
                 CustomLinkedList<CartItem> items, double totalAmount) {
        this();
        this.orderId          = orderId;
        this.customerId       = customerId;
        this.customerName     = customerName;
        this.customerEmail    = customerEmail;
        this.shippingAddress  = shippingAddress;
        this.items            = items != null ? items : new CustomLinkedList<>();
        this.totalAmount      = totalAmount;
    }

    public String                     getOrderId()        { return orderId; }
    public int                        getCustomerId()     { return customerId; }
    public String                     getCustomerName()   { return customerName; }
    public String                     getCustomerEmail()  { return customerEmail; }
    public String                     getShippingAddress(){ return shippingAddress; }
    public CustomLinkedList<CartItem> getItems()          { return items; }
    public double                     getTotalAmount()    { return totalAmount; }
    public Status                     getStatus()         { return status; }
    public String                     getOrderDate()      { return orderDate; }

    public void setOrderId(String v)                       { orderId = v; }
    public void setCustomerId(int v)                       { customerId = v; }
    public void setCustomerName(String v)                  { customerName = v; }
    public void setCustomerEmail(String v)                 { customerEmail = v; }
    public void setShippingAddress(String v)               { shippingAddress = v; }
    public void setItems(CustomLinkedList<CartItem> v)     { items = v; }
    public void setTotalAmount(double v)                   { totalAmount = v; }
    public void setStatus(Status v)                        { status = v; }
    public void setOrderDate(String v)                     { orderDate = v; }

    public String getItemsSummary() {
        StringBuilder sb = new StringBuilder();
        try {
            for (CartItem i : items) {
                if (sb.length() > 0) sb.append("; ");
                sb.append(i.getProduct().getName()).append(" x").append(i.getQuantity());
            }
        } catch (Exception e) { /* ignore */ }
        return sb.toString();
    }

    public String getItemsCsvString() {
        StringBuilder sb = new StringBuilder();
        try {
            for (CartItem i : items) {
                if (sb.length() > 0) sb.append("|");
                sb.append(i.getProduct().getId()).append(":").append(i.getQuantity());
            }
        } catch (Exception e) { /* ignore */ }
        return sb.toString();
    }

    @Override public boolean equals(Object o) {
        return (o instanceof Order) && orderId != null && orderId.equals(((Order)o).orderId);
    }
    @Override public int hashCode() { return orderId != null ? orderId.hashCode() : 0; }
}