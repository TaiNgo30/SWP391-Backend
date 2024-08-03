package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.requests.ZoneRequest;
import com.wha.warehousemanagement.services.ZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("zone")
@RequiredArgsConstructor
public class ZoneController {
private final ZoneService zoneService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllWarehouses() {
        return ResponseEntity.ok(zoneService.getAllZones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWarehouseById(@PathVariable("id") int id) {
        return ResponseEntity.ok(zoneService.getZoneById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addWarehouse(@RequestBody ZoneRequest request) {
        return ResponseEntity.ok(zoneService.addZone(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateWarehouse(@PathVariable("id") int id, @RequestBody ZoneRequest request) {
        return ResponseEntity.ok(zoneService.updateZoneById(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWarehouse(@PathVariable("id") int id) {
        return ResponseEntity.ok(zoneService.deleteZoneById(id));
    }


    //localhost:6060/zone/warehouse/1
    @GetMapping("/warehouse/{id}")
    public ResponseEntity<?> getZonesByWarehouseId(@PathVariable("id") int id) {
        return ResponseEntity.ok(zoneService.getZonesByWarehouseId(id));
    }

}
