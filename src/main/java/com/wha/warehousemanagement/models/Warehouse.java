package com.wha.warehousemanagement.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "warehouses")
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "warehouse_id")
    private Integer id;

    @Column(name = "warehouse_name")
    private String name;

    @Column(name = "warehouse_description")
    private String description;

    @Column(name = "warehouse_address")
    private String address;

    @Column(name = "warehouse_created_at")
    private Date createdAt;

    @OneToMany(mappedBy = "warehouse", fetch = FetchType.LAZY)
    private Set<Zone> zones;

    @OneToMany(mappedBy = "warehouse", fetch = FetchType.LAZY)
    private Set<User> users;

    @OneToMany(mappedBy = "warehouseFrom", fetch = FetchType.LAZY)
    private Set<Import> importsFrom;

    @OneToMany(mappedBy = "warehouseTo", fetch = FetchType.LAZY)
    private Set<Import> importsTo;

    @OneToMany(mappedBy = "warehouseFrom", fetch = FetchType.LAZY)
    private Set<Export> exportsFrom;

    @OneToMany(mappedBy = "warehouseTo", fetch = FetchType.LAZY)
    private Set<Export> exportsTo;
}
