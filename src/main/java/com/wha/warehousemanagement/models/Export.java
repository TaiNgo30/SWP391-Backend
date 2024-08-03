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
@Table(name = "exports")
public class Export {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "export_id")
    private Integer id;

    @Column(name = "export_description")
    private String description;

    @Column(name = "export_status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "export_date")
    private Date exportDate;

    @Column(name = "export_type")
    @Enumerated(EnumType.STRING)
    private ImportExportType exportType;

    @Column(name = "transfer_key")
    private String transferKey;

    @ManyToOne
    @JoinColumn(name = "warehouse_id_from", nullable = true)
    private Warehouse warehouseFrom;

    @ManyToOne
    @JoinColumn(name = "warehouse_id_to", nullable = true)
    private Warehouse warehouseTo;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = true)
    private Customer customer;

    @OneToMany(mappedBy = "export", fetch = FetchType.LAZY)
    private Set<ExportDetail> exportDetails;

}
