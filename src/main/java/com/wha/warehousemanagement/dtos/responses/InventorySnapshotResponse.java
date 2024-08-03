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
public class InventorySnapshotResponse {
    private Integer id;
    private ProductResponse product;
    private ZoneResponse zone;
    private String zoneName;
    private Date snapshotDate;
    private Integer quantity;
    private String warehouseName;
}
