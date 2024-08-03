package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.requests.ExportDetailRequest;
import com.wha.warehousemanagement.dtos.requests.ExportDetailUpdateRequest;
import com.wha.warehousemanagement.dtos.requests.SuggestedExportProductsRequest;
import com.wha.warehousemanagement.dtos.requests.checkAvailableProductRequest;
import com.wha.warehousemanagement.dtos.responses.SuggestedExportProductsResponse;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.services.ExportDetailService;
import com.wha.warehousemanagement.services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/export-details")
@RequiredArgsConstructor
public class ExportDetailController {
    private final ExportDetailService exportDetailService;
    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<?> getAllExportDetails() {
        return ResponseEntity.ok(exportDetailService.getAllExportDetails());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getExportDetailById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(exportDetailService.getExportDetailById(id));
    }

    @PostMapping
    public ResponseEntity<?> createExportDetail(@RequestBody List<ExportDetailRequest> requests) {
        return ResponseEntity.ok(exportDetailService.createExportDetail(requests));
    }

    @PostMapping("/check-quantity")
    public ResponseEntity<?> checkQuantity(@RequestBody ExportDetailRequest request) {
        return ResponseEntity.ok(exportDetailService.checkInventoryQuantityForUpdate(request));
    }

    @PutMapping("/list-update")
    public ResponseEntity<?> updateExportDetail(@RequestBody List<ExportDetailUpdateRequest> request) {
        return ResponseEntity.ok(exportDetailService.updateExportDetail(request));
    }

    //localhost:6060/export-details/1
    @DeleteMapping("/list-delete")
    public ResponseEntity<?> deleteExportDetail(@RequestBody List<Integer> ids) {
        return ResponseEntity.ok(exportDetailService.deleteExportDetail(ids));
    }

    @PostMapping("/suggest")
    public ResponseEntity<ResponseObject<List<SuggestedExportProductsResponse>>> suggestExportDetail(@RequestBody List<SuggestedExportProductsRequest> requests) {
        return ResponseEntity.ok(exportDetailService.suggestExportInventory(requests));
    }

    @PostMapping("/check-available-quantity")
    public ResponseEntity<?> checkAvailableQuantity(@RequestBody checkAvailableProductRequest request) {
        return ResponseEntity.ok(inventoryService.checkAvailableQuantity(request));
    }

    @GetMapping("/export/{exportId}")
    public ResponseEntity<?> getExportDetailsByExportId(@PathVariable("exportId") Integer exportId) {
        return ResponseEntity.ok(exportDetailService.getExportDetailsByExportId(exportId));
    }

    @GetMapping("/export-products/{exportId}")
    public ResponseEntity<?> getProductsInExportByExportId(@PathVariable("exportId") Integer exportId) {
        return ResponseEntity.ok(exportDetailService.getProductsInExportByExportId(exportId));
    }

    @PostMapping("/update-and-add")
    public ResponseEntity<?> updateAndAddExportDetails(
            @RequestBody List<ExportDetailRequest> requests,
            @RequestParam Integer exportId
    ) {
        System.out.println("exportId: " + exportId);
        System.out.println("requests: " + requests);
        return ResponseEntity.ok(exportDetailService.updateAndAddExportDetails(requests, exportId));
    }
}
