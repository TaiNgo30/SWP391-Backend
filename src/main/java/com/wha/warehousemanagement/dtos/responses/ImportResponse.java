package com.wha.warehousemanagement.dtos.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImportResponse {
    private Integer id;
    private String description;
    private String status;
    private Date receivedDate;
    private String importType;
    private String transferKey;
    private WarehouseResponse warehouseFrom;
    private WarehouseResponse warehouseTo;
    private CustomerResponse customer;
}
