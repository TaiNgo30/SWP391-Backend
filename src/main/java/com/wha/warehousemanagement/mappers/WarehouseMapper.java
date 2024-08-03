package com.wha.warehousemanagement.mappers;

import com.wha.warehousemanagement.dtos.responses.WarehouseResponse;
import com.wha.warehousemanagement.models.Warehouse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WarehouseMapper extends EntityMapper<WarehouseResponse, Warehouse>{
}
