package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.models.InventorySnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface InventorySnapshotRepository extends JpaRepository<InventorySnapshot, Integer> {
    List<InventorySnapshot> findBySnapshotDateBetween(Date startDate, Date endDate);
    List<InventorySnapshot> findBySnapshotDateBetweenAndZoneIdIn(Date startDate, Date endDate, List<Integer> zoneIds);
}

