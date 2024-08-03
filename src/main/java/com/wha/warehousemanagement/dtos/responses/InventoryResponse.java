package com.wha.warehousemanagement.dtos.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@ToString
@Data
public class InventoryResponse {
    private Integer id;
    private ProductResponse product;
    private Integer quantity;
    private Date expiredAt;
    private ZoneResponse zone;
}
