package com.wha.warehousemanagement.mappers;

import com.wha.warehousemanagement.dtos.responses.UserResponse;
import com.wha.warehousemanagement.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper extends EntityMapper<UserResponse, User>{
}
