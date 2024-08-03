package com.wha.warehousemanagement.models;

import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;

@Getter
@Setter
public class ExportNotification {

    // Getters and setters
    private Integer exportId;
    private String message;

    public ExportNotification() {}

    public ExportNotification(Integer exportId, String message) {
        this.exportId = exportId;
        this.message = message;
    }

    public ExportNotification(Export export) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = formatter.format(export.getExportDate());
        this.exportId = export.getId();
        this.message = "New export created by " + export.getWarehouseFrom().getName() + " to your warehouse at " + " at " + formattedDate +
                " description: " + export.getDescription();
    }
}