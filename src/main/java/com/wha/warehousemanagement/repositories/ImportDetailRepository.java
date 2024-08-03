package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.models.ExportDetail;
import com.wha.warehousemanagement.models.ImportDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportDetailRepository extends JpaRepository<ImportDetail, Integer> {
    List<ImportDetail> findAllByAnImport_Id(Integer importId);
    List<ImportDetail> findByAnImport_Id(Integer importId);
}
