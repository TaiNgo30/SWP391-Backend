package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.models.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Integer> {
    Optional<Warehouse> findById(int id);

    boolean existsByName(String name);

    boolean existsByAddress(String address);

    void deleteAll();

    @Query("SELECT w FROM Warehouse w JOIN w.users u WHERE u.id = :id")
    Warehouse findByUserId(@Param("id") int id);
}
