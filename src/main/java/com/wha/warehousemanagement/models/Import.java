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
@Table(name = "imports")
public class Import {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "import_id")
    private Integer id;

    @Column(name = "import_description")
    private String description;

    @Column(name = "import_status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "import_received_date")
    private Date receivedDate;

    @Column(name = "import_type")
    @Enumerated(EnumType.STRING)
    private ImportExportType importType;

    @Column(name = "transfer_key")
    private String transferKey;

    @ManyToOne
    @JoinColumn(name = "warehouse_id_from", nullable = true)
    private Warehouse warehouseFrom;

    @ManyToOne
    @JoinColumn(name = "warehouse_id_to", nullable = true)
    private Warehouse warehouseTo;

    @ManyToOne()
    @JoinColumn(name = "customer_id", nullable = true)
    private Customer customer;

    @OneToMany(mappedBy = "anImport", fetch = FetchType.LAZY)
    private Set<ImportDetail> importDetails;
}
