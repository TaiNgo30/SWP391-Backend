package com.wha.warehousemanagement.mappers;

import com.wha.warehousemanagement.dtos.responses.InventorySnapshotResponse;
import com.wha.warehousemanagement.models.InventorySnapshot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventorySnapshotMapper {
    @Mapping(source = "zone.name", target = "zoneName")
    @Mapping(source = "zone.warehouse.name", target = "warehouseName")
    InventorySnapshotResponse toDto(InventorySnapshot snapshot);
}
