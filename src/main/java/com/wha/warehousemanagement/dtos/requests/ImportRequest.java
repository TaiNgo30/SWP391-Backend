package com.wha.warehousemanagement.dtos.requests;

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
public class ImportRequest {
    private String description;

    private String status;

    private String importType;

    private String transferKey;

    private Integer warehouseIdFrom;

    private Integer warehouseIdTo;

    private Integer customerId;
}
