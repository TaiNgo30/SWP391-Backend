package com.wha.warehousemanagement.dtos;

import lombok.Data;

@Data
public class TokenDTO {
    private String username;
    private int userId;
    private int warehouseId;
    private String token;
    private String refreshToken;
    private String expirationTime;
}
