package com.wha.warehousemanagement.mappers;

import com.wha.warehousemanagement.dtos.responses.InventoryResponse;
import com.wha.warehousemanagement.models.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface InventoryMapper extends EntityMapper<InventoryResponse, Inventory>{
}
