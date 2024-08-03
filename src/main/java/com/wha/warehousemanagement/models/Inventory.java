package com.wha.warehousemanagement.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inventories")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Integer id;

    @ManyToOne()
    @JoinColumn(name = "product_id", nullable = true)
    private Product product;

    @ManyToOne()
    @JoinColumn(name = "zone_id", nullable = true)
    private Zone zone;

    @Column(name = "inventory_quantity")
    private Integer quantity;

    @Column(name = "inventory_expired_at")
    private Date expiredAt;
}
