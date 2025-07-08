package com.example.android_lab.models;

public class PaymentHistoryItem implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String orderId;
    private String date;
    private double amount;
    private String status;
    private double total;
    private String contactInfo;
    private String productListString;

    public PaymentHistoryItem() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    public String getProductListString() { return productListString; }
    public void setProductListString(String productListString) { this.productListString = productListString; }
}
