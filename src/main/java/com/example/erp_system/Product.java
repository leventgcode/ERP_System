package com.example.erp_system;

public class Product {
    private int id;
    private String barcode;
    private String name;
    private double price;
    private int stockQuantity;

    public Product(int id, String barcode, String name, double price, int stockQuantity) {
        this.id = id;
        this.barcode = barcode;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public int getId() { return id; }
    public String getBarcode() { return barcode; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }
}