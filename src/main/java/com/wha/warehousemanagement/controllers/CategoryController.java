package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.requests.CategoryRequest;
import com.wha.warehousemanagement.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<?> addCategory(@RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.addCategory(request));
    }

    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable("id") int id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable("id") int id, @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable("id") int id) {
        return ResponseEntity.ok(categoryService.deleteCategoryById(id));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAllCategories() {
        return ResponseEntity.ok(categoryService.deleteAllCategories());
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllSortedCategoryWithSearch (
            @RequestParam(value = "search", required = false) String search
    ) {
        return ResponseEntity.ok(categoryService.getAllSortedCategoryWithSearch(search));
    }
}
