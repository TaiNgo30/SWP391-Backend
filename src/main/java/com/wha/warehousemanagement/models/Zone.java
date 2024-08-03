package com.wha.warehousemanagement.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "zones")
public class Zone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "zone_id")
    private Integer id;

    @Column(name = "zone_name")
    private String name;

    @Column(name = "zone_description")
    private String description;

    @Column(name = "zone_status")
    @Enumerated(EnumType.STRING)
    private ZoneStatus zoneStatus;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @OneToMany(mappedBy = "zone", fetch = FetchType.LAZY)
    private Set<Inventory> inventories;
}
