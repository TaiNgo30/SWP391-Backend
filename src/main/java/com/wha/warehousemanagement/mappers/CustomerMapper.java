package com.wha.warehousemanagement.mappers;

import com.wha.warehousemanagement.dtos.responses.CustomerResponse;
import com.wha.warehousemanagement.models.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerMapper extends EntityMapper<CustomerResponse, Customer> {
}
