package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.models.Export;
import com.wha.warehousemanagement.models.Import;
import com.wha.warehousemanagement.models.Status;
import com.wha.warehousemanagement.models.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImportRepository extends JpaRepository<Import, Integer> {
    Optional<Import> getImportById(int id);

    @Query("SELECT i FROM Import i")
    Page<Import> findAllImports(Pageable pageable);

    @Query("SELECT COUNT(i) FROM Import i")
    Long countAllImports();

    @Query("SELECT i FROM Import i " +
            "LEFT JOIN i.customer c " +
            "LEFT JOIN i.warehouseTo w " +
            "WHERE (i.warehouseTo.id = :warehouseId) " +
            "AND (" +
            ":search IS NULL OR LOWER(i.description) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(c.address) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(w.address) LIKE LOWER(CONCAT('%', :search, '%')))" +
            "ORDER BY " +
            "CASE " +
            "WHEN i.status = 'REQUESTING' THEN 1 " +
            "WHEN i.status = 'PENDING' THEN 2 " +
            "WHEN i.status = 'SHIPPING' THEN 3 " +
            "WHEN i.status = 'SUCCEED' THEN 4 " +
            "WHEN i.status = 'CANCEL' THEN 5 " +
            "ELSE 6 END, " +
            "i.receivedDate ASC")
    Page<Import> findAllImportsByWarehouseSorted(
            @Param("warehouseId") Integer warehouseId,
            @Param("search") String search,
            Pageable pageable);

    @Query("SELECT i FROM Import i " +
            "LEFT JOIN i.customer c " +
            "LEFT JOIN i.warehouseTo w " +
            "WHERE i.warehouseTo.id = :warehouseId " +
            "AND (" +
            ":search IS NULL OR LOWER(i.description) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(c.address) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(w.address) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Import> findAllImportsByWarehouseWithDefaultSort(
            @Param("warehouseId") Integer warehouseId,
            @Param("search") String search,
            Pageable pageable);

    @Query("SELECT COUNT(i) FROM Import i " +
            "LEFT JOIN i.customer c " +
            "LEFT JOIN i.warehouseTo w " +
            "WHERE i.warehouseTo.id = :warehouseId " +
            "AND (:status IS NULL OR i.status = :status) " +
            "AND (" +
            ":search IS NULL OR LOWER(i.description) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(c.address) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(w.address) LIKE LOWER(CONCAT('%', :search, '%')))")
    Integer countImportsByWarehouseIdAndStatus(
            @Param("warehouseId") Integer warehouseId,
            @Param("status") Status status,
            @Param("search") String search);

    Import findFirstByWarehouseToOrderByIdDesc(Warehouse warehouseTo);

}
