package com.wha.warehousemanagement.mappers;

import com.wha.warehousemanagement.dtos.responses.ExportDetailResponse;
import com.wha.warehousemanagement.models.ExportDetail;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExportDetailMapper extends EntityMapper<ExportDetailResponse, ExportDetail>{
}
