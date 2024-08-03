package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.models.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Integer> {
    boolean existsByName(String name);
    List<Zone> findAllByWarehouse_Id(int warehouseId);
}
