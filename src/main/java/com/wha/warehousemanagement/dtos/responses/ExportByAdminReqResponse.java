package com.wha.warehousemanagement.dtos.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExportByAdminReqResponse {
    private Integer id;

    private String description;

    private String status;

    private Date exportDate;

    private String exportType;

    private String transferKey;

    private WarehouseResponse warehouseFrom;

    private WarehouseResponse warehouseTo;

    List<ExportDetailWithExportIdResponse> exportDetails;

    private CustomerResponse customer;
}
