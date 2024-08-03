package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.models.ExportDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExportDetailRepository extends JpaRepository<ExportDetail, Integer> {


    @Query("SELECT COALESCE(SUM(ed.quantity), 0) FROM ExportDetail ed " +
            "JOIN ed.export e " +
            "WHERE e.warehouseFrom.id = :warehouseId " +
            "AND ed.product.id = :productId " +
            "AND e.status = 'PENDING'")
    int findTotalPendingQuantityByWarehouseAndProduct(@Param("warehouseId") Integer warehouseId, @Param("productId") Integer productId);

//    @Query("SELECT COALESCE(SUM(ed.quantity), 0) FROM ExportDetail ed " +
//            "JOIN ed.export e " +
//            "WHERE e.warehouseFrom.id = :warehouseId " +
//            "AND ed.product.id = :productId " +
//            "AND e.status = 'PENDING'")
//    int findTotalPendingQuantityByWarehouseAndProduct(@Param("warehouseId") Integer warehouseId, @Param("productId") Integer productId);

    List<ExportDetail> findByExportId(Integer exportId);
}
