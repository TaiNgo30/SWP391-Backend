package com.wha.warehousemanagement.mappers;

import com.wha.warehousemanagement.dtos.responses.CategoryResponse;
import com.wha.warehousemanagement.models.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper extends EntityMapper<CategoryResponse, Category>{

}
