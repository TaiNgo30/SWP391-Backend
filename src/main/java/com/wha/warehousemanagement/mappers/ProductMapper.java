package com.wha.warehousemanagement.mappers;

import com.wha.warehousemanagement.dtos.responses.ProductResponse;
import com.wha.warehousemanagement.models.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper extends EntityMapper<ProductResponse, Product> {

    @Mapping(source = "id", target = "id")
    ProductResponse toDto(Product product);

}
