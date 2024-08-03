package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.models.Export;
import com.wha.warehousemanagement.models.Status;
import com.wha.warehousemanagement.models.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExportRepository extends JpaRepository<Export, Integer> {
    boolean existsByTransferKey(String transferKey);

    @Query("SELECT e FROM Export e " +
            "LEFT JOIN e.customer c " +
            "LEFT JOIN e.warehouseTo w " +
            "WHERE (e.warehouseFrom.id = :warehouseId) " +
            "AND (:status IS NULL OR e.status = :status) " +
            "AND (" +
            ":search IS NULL OR LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(c.address) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(w.address) LIKE LOWER(CONCAT('%', :search, '%')))" +
            "ORDER BY " +
            "CASE " +
            "WHEN e.status = 'REQUESTING' THEN 1 " +
            "WHEN e.status = 'PENDING' THEN 2 " +
            "WHEN e.status = 'SHIPPING' THEN 3 " +
            "WHEN e.status = 'SUCCEED' THEN 4 " +
            "WHEN e.status = 'CANCEL' THEN 5 " +
            "ELSE 6 END, " +
            "e.exportDate ASC")
    Page<Export> findAllByWarehouseSorted(
            @Param("warehouseId") Integer warehouseId,
            @Param("status") Status status,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT e FROM Export e " +
            "LEFT JOIN e.customer c " +
            "LEFT JOIN e.warehouseTo w " +
            "WHERE e.warehouseFrom.id = :warehouseId " +
            "AND (:status IS NULL OR e.status = :status) " +
            "AND (" +
            ":search IS NULL OR LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(c.address) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(w.address) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Export> findAllByWarehouseWithDefaultSort(
            @Param("warehouseId") Integer warehouseId,
            @Param("status") Status status,
            @Param("search") String search,
            Pageable pageable);

    @Query("SELECT COUNT(e) FROM Export e " +
            "LEFT JOIN e.customer c " +
            "LEFT JOIN e.warehouseTo w " +
            "WHERE e.warehouseFrom.id = :warehouseId " +
            "AND (:status IS NULL OR e.status = :status) " +
            "AND (" +
            ":search IS NULL OR LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(c.address) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(w.address) LIKE LOWER(CONCAT('%', :search, '%')))")
    Integer countByWarehouseIdAndStatus(
            @Param("warehouseId") Integer warehouseId,
            @Param("status") Status status,
            @Param("search") String search
    );

    @Query("SELECT e FROM Export e " +
            "WHERE (:status IS NULL OR e.status = :status) " +
            "ORDER BY " +
            "CASE " +
            "WHEN e.status = 'REQUESTING' THEN 1 " +
            "WHEN e.status = 'PENDING' THEN 2 " +
            "WHEN e.status = 'SHIPPING' THEN 3 " +
            "WHEN e.status = 'SUCCEED' THEN 4 " +
            "WHEN e.status = 'CANCEL' THEN 5 " +
            "ELSE 6 END, " +
            "e.exportDate ASC")
    Page<Export> findAllExportsForAdmin(
            @Param("status") Status status,
            Pageable pageable
    );

    @Query("SELECT e FROM Export e " +
            "LEFT JOIN e.customer c " +
            "LEFT JOIN e.warehouseTo w " +
            "WHERE (e.warehouseFrom.id = :warehouseId) " +
            "ORDER BY " +
            "CASE " +
            "WHEN e.status = 'PENDING' THEN 1 " +
            "WHEN e.status = 'SHIPPING' THEN 2 " +
            "WHEN e.status = 'SUCCEED' THEN 3 " +
            "WHEN e.status = 'CANCEL' THEN 4 " +
            "ELSE 5 END, " +
            "e.exportDate ASC")
    List<Export> findByWarehouseFromId(@Param("warehouseId") Integer warehouseId);

    Export findFirstByWarehouseFromOrderByIdDesc(Warehouse warehouseFrom);

}
