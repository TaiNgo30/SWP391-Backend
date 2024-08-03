package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.requests.ProductRequest;
import com.wha.warehousemanagement.dtos.responses.ProductListForExportResponse;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<?> addProduct(@RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.addProduct(request));
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") int id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable("id") int id, @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") int id) {
        return ResponseEntity.ok(productService.deleteProductById(id));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAllProducts() {
        return ResponseEntity.ok(productService.deleteAllProducts());
    }

    //localhost:6060/product/product-list-for-export/1?page=1&sortBy=id&search=product:1
    @GetMapping("/product-list-for-auto-select/{warehouseId}")
    public ResponseEntity<ResponseObject<List<ProductListForExportResponse>>> getProductListForExport(
            @PathVariable("warehouseId") Integer warehouseId
    ) {
        return ResponseEntity.ok(productService.getAllProductsByWarehouseId(warehouseId));
    }

    @GetMapping("/product-list-for-auto-select/total/{warehouseId}")
    public ResponseEntity<?> getTotalProductListForAutoSelect(
            @PathVariable("warehouseId") Integer warehouseId,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "search", required = false) String search
    ) {
        if (categoryId != null && categoryId == 0) {
            categoryId = null;
        }
        if (search != null && search.isBlank()) {
            search = null;
        }
        return ResponseEntity.ok(productService.getTotalProductsForAutoSelect(warehouseId, categoryId, search));
    }
}
