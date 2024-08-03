package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.requests.ImportRequest;
import com.wha.warehousemanagement.dtos.requests.ImportUpdateRequest;
import com.wha.warehousemanagement.dtos.responses.ImportResponse;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.models.Status;
import com.wha.warehousemanagement.services.ImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/import")
@RequiredArgsConstructor
public class ImportController {

    private final ImportService importService;

    @PostMapping
    public ResponseEntity<?> addImport(@RequestBody ImportRequest request) {
        return ResponseEntity.ok(importService.addImport(request));
    }

    @GetMapping("/by-warehouse/{warehouseId}")
    public ResponseEntity<ResponseObject<List<ImportResponse>>> getAllImportsByWarehouseId(
            @PathVariable("warehouseId") Integer warehouseId,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "direction", defaultValue = "desc", required = false) String direction,
            @RequestParam(value = "status", required = false) Status status,
            @RequestParam(value = "search", required = false) String search
    ) {
        int limit = 10;
        pageNo = pageNo - 1;
        search = search.isBlank() ? null : search;
        return ResponseEntity.ok(importService.getAllImports(
                warehouseId, pageNo, limit, sortBy, direction, search
        ));
    }

    @GetMapping("/by-warehouse/total/{warehouseId}")
    public ResponseEntity<?> getTotalImportsByWarehouse(
            @PathVariable("warehouseId") Integer warehouseId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "search", required = false) String search
    ) {
        Status importStatus = null;
        search = search.isBlank() ? null : search;
        if (status != null && !status.isEmpty()) {
            try {
                importStatus = Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid status value");
            }
        }
        return ResponseEntity.ok(importService.getTotalImportsByWarehouse(warehouseId, importStatus, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getImportById(@PathVariable("id") int id) {
        return ResponseEntity.ok(importService.getImportById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateImport(@PathVariable("id") int id, @RequestBody ImportUpdateRequest request) {
        return ResponseEntity.ok(importService.updateImport(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImportById(@PathVariable("id") int id) {
        return ResponseEntity.ok(importService.deleteImportById(id));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAllImports() {
        return ResponseEntity.ok(importService.deleteAllImports());
    }

    //localhost:8080/import/import?page=1
//    @GetMapping("/import")
//    public ResponseEntity<?> getAllImport(
//            @RequestParam(value = "page", defaultValue = "1") int page
//    ) {
//        int limit = 20;
//        page = page - 1;
//        return ResponseEntity.ok(importService.getAllImports());
//    }
    @GetMapping("/import")
    public ResponseEntity<?> getAllImport(
//            @RequestParam(value = "page", defaultValue = "1") int page
    ) {
//        int limit = 20;
//        page = page - 1;
        return ResponseEntity.ok(importService.getAllImports());
    }

    //localhost:8080/import/total-import
    @GetMapping("/total-import")
    public ResponseEntity<?> getTotalImport() {
        return ResponseEntity.ok(importService.getTotalImports());
    }

    @GetMapping("/by-warehouse/get-latest/{warehouseId}")
    public ResponseEntity<?> getLatestImportByWarehouseId(@PathVariable("warehouseId") Integer warehouseId) {
        return ResponseEntity.ok(importService.getLatestImportByWarehouseId(warehouseId));
    }
}
