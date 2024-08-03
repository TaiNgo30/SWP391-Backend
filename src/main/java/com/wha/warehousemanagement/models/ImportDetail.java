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
@Table(name = "import_details")
public class ImportDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "import_detail_id")
    private Integer id;

    @ManyToOne()
    @JoinColumn(name = "import_id", nullable = true)
    private Import anImport;

    @ManyToOne()
    @JoinColumn(name = "product_id", nullable = true)
    private Product product;

    @Column(name = "import_detail_quantity")
    private Integer quantity;

    @Column(name = "import_detail_expired_at")
    private Date expiredAt;
}
