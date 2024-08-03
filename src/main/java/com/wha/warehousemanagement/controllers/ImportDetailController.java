package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.requests.ImportDetailRequest;
import com.wha.warehousemanagement.services.ImportDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/import-details")
@RequiredArgsConstructor
public class ImportDetailController {
    private final ImportDetailService importDetailService;

    @GetMapping
    public ResponseEntity<?> getAllImportDetails() {
        return ResponseEntity.ok(importDetailService.getAllImportDetails());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getImportDetailById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(importDetailService.getImportDetailById(id));
    }

    @PostMapping
    public ResponseEntity<?> createImportDetail(@RequestBody List<ImportDetailRequest> requests) {
        return ResponseEntity.ok(importDetailService.createImportDetail(requests));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateImportDetail(@PathVariable("id") Integer id, @RequestBody ImportDetailRequest request) {
        return ResponseEntity.ok(importDetailService.updateImportDetail(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImportDetail(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(importDetailService.deleteImportDetail(id));
    }

    @GetMapping("/import/{importId}")
    public ResponseEntity<?> getImportDetailsByImportId(@PathVariable("importId") Integer importId) {
        return ResponseEntity.ok(importDetailService.getImportDetailsByImportId(importId));
    }

}
