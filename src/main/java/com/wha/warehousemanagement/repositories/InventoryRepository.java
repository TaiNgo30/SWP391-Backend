package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.models.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    @Query("SELECT i FROM Inventory i " +
            "JOIN i.product p " +
            "JOIN p.category c " +
            "JOIN i.zone z " +
            "JOIN z.warehouse w " +
            "WHERE w.id = :warehouseId " +
            "AND (:categoryId IS NULL OR c.id = :categoryId) " +
            "AND (:zoneName IS NULL OR z.name = :zoneName)")
    Page<Inventory> findByWarehouseIdAndCategoryId(Integer warehouseId, Integer categoryId, String zoneName, Pageable pageable);

    @Query("SELECT COUNT(i) FROM Inventory i " +
            "JOIN i.product p " +
            "JOIN p.category c " +
            "JOIN i.zone z " +
            "JOIN z.warehouse w " +
            "WHERE w.id = :warehouseId " +
            "AND (:categoryId IS NULL OR c.id = :categoryId) " +
            "AND (:zoneName IS NULL OR z.name = :zoneName)")
    Long countInventoriesByWarehouseIdAndCategoryId(Integer warehouseId, Integer categoryId, String zoneName);

    @Query("SELECT COUNT(i) FROM Inventory i JOIN i.zone z JOIN z.warehouse w WHERE w.id = :warehouseId")
    Long countInventoriesByWarehouseId(Integer warehouseId);


    // find inventories by zone id
    List<Inventory> findByZoneId(Integer zoneId);

    // zones transfer
    Inventory findByProductIdAndZoneId(int productId, int zoneId);

    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.zone.warehouse.id = :warehouseId AND i.expiredAt >= CURRENT_DATE ORDER BY i.expiredAt ASC")
    List<Inventory> findByProductIdAndWarehouseIdOrderByExpiredAtAsc(@Param("productId") Integer productId, @Param("warehouseId") Integer warehouseId);

    @Query("SELECT SUM(i.quantity) FROM Inventory i WHERE i.product.id = :productId AND i.zone.warehouse.id = :warehouseId")
    Integer countTotalQuantityByProductIdAndWarehouseId(@Param("productId") Integer productId, @Param("warehouseId") Integer warehouseId);

    @Query("SELECT i FROM Inventory i " +
            "JOIN i.product p " +
            "JOIN p.category c " +
            "JOIN i.zone z " +
            "JOIN z.warehouse w " +
            "WHERE w.id = :warehouseId " +
            "AND (:categoryId IS NULL OR c.id = :categoryId) " +
            "AND (:zoneName IS NULL OR z.name = :zoneName) " +
            "AND (:productName IS NULL OR p.name LIKE %:productName%) " +
            "AND (:quantityLow IS NULL OR i.quantity >= :quantityLow) " +
            "AND (:quantityHigh IS NULL OR i.quantity <= :quantityHigh)")
    Page<Inventory> searchInventoriesWithFilters(
            Integer warehouseId,
            Integer categoryId,
            String zoneName,
            String productName,
            Integer quantityLow,
            Integer quantityHigh,
            Pageable pageable);

    @Query("SELECT COUNT(i) FROM Inventory i " +
            "JOIN i.product p " +
            "JOIN p.category c " +
            "JOIN i.zone z " +
            "JOIN z.warehouse w " +
            "WHERE w.id = :warehouseId " +
            "AND (:categoryId IS NULL OR c.id = :categoryId) " +
            "AND (:zoneName IS NULL OR z.name = :zoneName) " +
            "AND (:productName IS NULL OR p.name LIKE %:productName%) " +
            "AND (:quantityLow IS NULL OR i.quantity >= :quantityLow) " +
            "AND (:quantityHigh IS NULL OR i.quantity <= :quantityHigh)")
    Long countInventoriesWithFilters(
            Integer warehouseId,
            Integer categoryId,
            String zoneName,
            String productName,
            Integer quantityLow,
            Integer quantityHigh);

    @Query("SELECT i FROM Inventory i WHERE i.zone.warehouse.id = :warehouseId " +
            "AND ((:includeExpired = true AND i.expiredAt < :currentDate) OR " +
            "(:includeNearExpired = true AND i.expiredAt BETWEEN :currentDate AND :nearExpiredDate) OR " +
            "(:includeValid = true AND i.expiredAt > :nearExpiredDate))")
    Page<Inventory> findInventoriesByWarehouseIdWithFilters(
            @Param("warehouseId") Integer warehouseId,
            @Param("currentDate") Date currentDate,
            @Param("nearExpiredDate") Date nearExpiredDate,
            @Param("includeExpired") boolean includeExpired,
            @Param("includeNearExpired") boolean includeNearExpired,
            @Param("includeValid") boolean includeValid,
            Pageable pageable);

    @Query("SELECT i FROM Inventory i " +
            "JOIN i.product p " +
            "JOIN p.category c " +
            "JOIN i.zone z " +
            "JOIN z.warehouse w " +
            "WHERE w.id = :warehouseId " +
            "AND (:categoryId IS NULL OR c.id = :categoryId) " +
            "AND (:zoneId IS NULL OR z.id = :zoneId) " +
            "AND (:search IS NULL OR :search = '' OR LOWER(p.name) LIKE %:search% OR LOWER(p.description) LIKE %:search%)")
    Page<Inventory> searchInventoriesForAdmin(Pageable pageable,
                                              @Param("warehouseId") Integer warehouseId,
                                              @Param("categoryId") Integer categoryId,
                                              @Param("zoneId") Integer zoneId,
                                              @Param("search") String search);


    @Query("SELECT COALESCE(SUM(i.quantity), 0) FROM Inventory i WHERE i.zone.warehouse.id = :warehouseId AND i.product.id = :productId")
    int findTotalQuantityByWarehouseAndProductId(int warehouseId, int productId);

//    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.zone.id = :zoneId AND i.expiredAt = :expiredAt")
//    Optional<Inventory> findByProductIdAndZoneIdAndExpiredAt(
//            @Param("productId") Integer productId,
//            @Param("zoneId") Integer zoneId,
//            @Param("expiredAt") Date expiredAt);

    List<Inventory> findByZoneWarehouseId(Integer warehouseId);

    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.zone.id = :zoneId AND (:expiredAt is null or i.expiredAt = :expiredAt)")
    Optional<Inventory> findByProductIdAndZoneIdAndExpiredAt(@Param("productId") Integer productId, @Param("zoneId") Integer zoneId, @Param("expiredAt") Date expiredAt);

    @Query("SELECT i FROM Inventory i WHERE i.product.name = :productName AND (:expiredAt is not null or i.expiredAt = :expiredAt) AND i.zone.id = :zoneId")
    Inventory findByExportDetail(@Param("productName") String productName,
            @Param("expiredAt") Date expiredAt,
            @Param("zoneId") Integer zoneId);

    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.zone.warehouse.id = :warehouseId")
    List<Inventory> findByProductIdandWarehouseId(Integer productId, Integer warehouseId);

    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.zone.id = :zoneId AND i.expiredAt IS NULL")
    Optional<Inventory> findByProductIdAndZoneIdAndExpiredAtIsNull(@Param("productId") Integer productId, @Param("zoneId") Integer zoneId);

}
