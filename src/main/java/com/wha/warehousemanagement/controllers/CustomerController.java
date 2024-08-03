package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.requests.CustomerRequest;
import com.wha.warehousemanagement.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<?> addCustomer(@RequestBody CustomerRequest request) {
        return ResponseEntity.ok(customerService.addProvider(request));
    }

    @GetMapping
    public ResponseEntity<?> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable("id") int id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable("id") int id, @RequestBody CustomerRequest request) {
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomerById(@PathVariable("id") int id) {
        return ResponseEntity.ok(customerService.deleteCustomerById(id));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAllCustomers() {
        return ResponseEntity.ok(customerService.deleteAllCustomers());
    }
}
