package com.wha.warehousemanagement.dtos.requests;

public class TransferProductRequest {
    private int productId;
    private int fromZoneId;
    private int toZoneId;
    private int quantity;

    // Getters and setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getFromZoneId() {
        return fromZoneId;
    }

    public void setFromZoneId(int fromZoneId) {
        this.fromZoneId = fromZoneId;
    }

    public int getToZoneId() {
        return toZoneId;
    }

    public void setToZoneId(int toZoneId) {
        this.toZoneId = toZoneId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
