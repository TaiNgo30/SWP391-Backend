package com.wha.warehousemanagement.dtos.requests;

import java.util.Date;

public class TransferRequest {
    private int productId;
    private int fromZoneId;
    private int toZoneId;
    private int quantity;
    private Date expiredAt;

    // Getters và Setters
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

    public Date getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Date expiredAt) {
        this.expiredAt = expiredAt;
    }
}
