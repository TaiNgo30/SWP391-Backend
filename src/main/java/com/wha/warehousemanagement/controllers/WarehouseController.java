package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.requests.WarehouseRequest;
import com.wha.warehousemanagement.services.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllWarehouses() {
        return ResponseEntity.ok(warehouseService.getAllWarehouses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWarehouseById(@PathVariable("id") int id) {
        return ResponseEntity.ok(warehouseService.getWarehouseById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addWarehouse(@RequestBody WarehouseRequest request) {
        return ResponseEntity.ok(warehouseService.addWarehouse(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateWarehouse(@PathVariable("id") int id, @RequestBody WarehouseRequest request) {
        return ResponseEntity.ok(warehouseService.updateWarehouseById(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWarehouse(@PathVariable("id") int id) {
        return ResponseEntity.ok(warehouseService.deleteWarehouseById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getWarehousesByUserId(@PathVariable int userId) {
        return ResponseEntity.ok(warehouseService.getWarehousesByUserId(userId));
    }

}
