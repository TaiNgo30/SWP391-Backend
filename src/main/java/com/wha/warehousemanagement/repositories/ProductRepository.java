package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findByName(String name);

    Optional<Product> getProductById(int id);

    @Query("SELECT p FROM Product p " +
            "JOIN p.category c " +
            "JOIN p.inventories i " +
            "JOIN i.zone z " +
            "JOIN z.warehouse w " +
            "WHERE w.id = :warehouseId ")
    List<Product> getAllProductsByWarehouseIdWithFilters(@Param("warehouseId") Integer warehouseId);

    @Query("SELECT COUNT(p) FROM Product p " +
            "JOIN p.category c " +
            "JOIN p.inventories i " +
            "JOIN i.zone z " +
            "JOIN z.warehouse w " +
            "WHERE w.id = :warehouseId " +
            "AND (:categoryId IS NULL OR c.id = :categoryId) " +
            "AND (:search IS NULL OR p.name LIKE %:search%)")
    Integer getTotalProductsForAutoSelect(@Param("warehouseId") Integer warehouseId,
                                          @Param("categoryId") Integer categoryId,
                                          @Param("search") String search);
}
