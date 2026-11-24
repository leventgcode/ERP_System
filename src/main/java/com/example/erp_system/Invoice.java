package com.example.erp_system;

public class Invoice {
    private int id;
    private String invoiceNumber;
    private String customerName;
    private double totalAmount;
    private String createdAt;

    public Invoice(int id, String invoiceNumber, String customerName, double totalAmount, String createdAt) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public String getCustomerName() { return customerName; }
    public double getTotalAmount() { return totalAmount; }
    public String getCreatedAt() { return createdAt; }
}