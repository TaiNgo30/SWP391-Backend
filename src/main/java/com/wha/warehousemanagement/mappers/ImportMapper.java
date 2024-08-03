package com.wha.warehousemanagement.mappers;

import com.wha.warehousemanagement.dtos.responses.ImportResponse;
import com.wha.warehousemanagement.models.Import;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ImportMapper extends EntityMapper<ImportResponse, Import>  {
}
