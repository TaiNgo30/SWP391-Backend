package com.wha.warehousemanagement.mappers;

import com.wha.warehousemanagement.dtos.responses.ExportResponse;
import com.wha.warehousemanagement.models.Export;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExportMapper extends EntityMapper<ExportResponse, Export>  {
}
