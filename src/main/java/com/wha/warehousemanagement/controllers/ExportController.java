package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.requests.ExportRequest;
import com.wha.warehousemanagement.dtos.requests.ExportTransferRequest;
import com.wha.warehousemanagement.dtos.requests.ExportUpdateRequest;
import com.wha.warehousemanagement.dtos.responses.ExportByAdminReqResponse;
import com.wha.warehousemanagement.dtos.responses.ExportResponse;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.models.Status;
import com.wha.warehousemanagement.services.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    @PostMapping
    public ResponseEntity<?> addExport(@RequestBody ExportRequest request) {
        return ResponseEntity.ok(exportService.addExport(request));
    }

    @GetMapping
    public ResponseEntity<?> getAllExports() {
        return ResponseEntity.ok(exportService.getAllExports());
    }

    //
    @GetMapping("/by-warehouse/{warehouseId}")
    public ResponseEntity<ResponseObject<List<ExportResponse>>> getAllExports(
            @PathVariable("warehouseId") Integer warehouseId
    ) {
        return ResponseEntity.ok(exportService.getAllExports(
                warehouseId
        ));
    }

    @GetMapping("/by-warehouse/total/{warehouseId}")
    public ResponseEntity<?> getTotalExportsByWarehouse(
            @PathVariable("warehouseId") Integer warehouseId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "search", required = false) String search
    ) {
        Status exportStatus = null;
        search = search.isBlank() ? null : search;
        if (status != null && !status.isEmpty()) {
            try {
                exportStatus = Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid status value");
            }
        }
        return ResponseEntity.ok(exportService.getTotalExportsByWarehouseIdAndFilterByStatus(
                warehouseId,
                exportStatus,
                search
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getExportById(@PathVariable("id") int id) {
        return ResponseEntity.ok(exportService.getExportById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateExport(@PathVariable("id") int id, @RequestBody ExportUpdateRequest request) {
        return ResponseEntity.ok(exportService.updateExport(id, request));
    }

    @PutMapping("/update-export/{id}")
    public ResponseEntity<?> updateExportWithRequest(@PathVariable("id") int id, @RequestBody ExportRequest request) {
        return ResponseEntity.ok(exportService.updateExport(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExport(@PathVariable("id") int id) {
        return ResponseEntity.ok(exportService.deleteExportById(id));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAllExports() {
        return ResponseEntity.ok(exportService.deleteAllExports());
    }

    // For Admin create export request in warehouse A and import request for warehouse B (only product and quantity)
    @PostMapping("/admin/req-transfer")
    public ResponseEntity<ResponseObject<ExportByAdminReqResponse>> createTransferBetweenWarehouses(@RequestBody ExportTransferRequest request) {
        return ResponseEntity.ok(exportService.createTransferBetweenWarehouses(request));
    }

    @GetMapping("/admin")
    public ResponseEntity<?> getAllExportsForAdmin(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "direction", required = false) String direction,
            @RequestParam(value = "status", required = false) Status status
    ) {
        int limit = 5;
        pageNo = pageNo - 1;
        return ResponseEntity.ok(exportService.getAllExportsForAdmin(pageNo, limit, sortBy, direction, status));
    }

    @PutMapping("/approve/{exportId}")
    public ResponseEntity<?> approveExportRequest(
            @PathVariable Integer exportId
    ) {
        return ResponseEntity.ok(exportService.approveExportPending(exportId));
    }

    @PutMapping("/shipped/{exportId}")
    public ResponseEntity<?> shippedExportRequest(
            @PathVariable Integer exportId
    ) {
        return ResponseEntity.ok(exportService.confirmShippedSuccessfully(exportId));
    }

    @GetMapping("/by-warehouse/get-latest/{warehouseId}")
    public ResponseEntity<?> getLatestExportByWarehouseId(
            @PathVariable("warehouseId") Integer warehouseId
    ) {
        return ResponseEntity.ok(exportService.getLatestExportByWarehouseId(warehouseId));
    }
}
