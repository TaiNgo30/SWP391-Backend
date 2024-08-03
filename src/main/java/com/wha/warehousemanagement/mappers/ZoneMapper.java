package com.wha.warehousemanagement.mappers;

import com.wha.warehousemanagement.dtos.responses.ZoneResponse;
import com.wha.warehousemanagement.models.Zone;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ZoneMapper extends EntityMapper<ZoneResponse, Zone>{
    @Mapping(source = "warehouse.id", target = "warehouseId")
    ZoneResponse toDto(Zone zone);
}
