package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.requests.ProductRequest;
import com.wha.warehousemanagement.dtos.responses.CategoryResponse;
import com.wha.warehousemanagement.dtos.responses.ProductListForExportResponse;
import com.wha.warehousemanagement.dtos.responses.ProductResponse;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.mappers.CategoryMapper;
import com.wha.warehousemanagement.mappers.ProductMapper;
import com.wha.warehousemanagement.models.Category;
import com.wha.warehousemanagement.models.Product;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.repositories.CategoryRepository;
import com.wha.warehousemanagement.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final InventoryService inventoryService;

    public ResponseObject<?> addProduct(ProductRequest request) {
        try {
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                throw new CustomException(ErrorCode.PRODUCT_NAME_BLANK);
            } else if (productRepository.findByName(request.getName()).isPresent()) {
                throw new CustomException(ErrorCode.PRODUCT_ALREADY_EXISTS);
            }
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
            Product product = new Product();
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setCategory(category);
            productRepository.save(product);
            ProductResponse response = productMapper.toDto(product);
            return new ResponseObject<>(HttpStatus.OK.value(), "Product added successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to add product", null);
        }
    }

    public ResponseObject<?> getAllProducts() {
        try {
            List<ProductResponse> responses = productRepository.findAll()
                    .stream().map(
                            product -> {
                                ProductResponse response = productMapper.toDto(product);
                                CategoryResponse categoryResponse = categoryMapper.toDto(product.getCategory());
                                response.setCategory(categoryResponse);
                                return response;
                            }
                    )
                    .toList();
            return new ResponseObject<>(HttpStatus.OK.value(), "Get all products successfully", responses);
        } catch (CustomException e) {
            return new ResponseObject<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed get products details", null);
        }
    }

    public ResponseObject<ProductResponse> getProductById(int id) {
        try {
            Product product = productRepository.getProductById(id).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
            ProductResponse response = productMapper.toDto(product);
            return new ResponseObject<>(HttpStatus.OK.value(), "Get product by id successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to add product", null);
        }
    }

    public ResponseObject<ProductResponse> updateProduct(int id, ProductRequest request) {
        try {
            Product product = productRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
            if (request.getName() != null && !request.getName().trim().isEmpty()) {
                product.setName(request.getName());
            }
            if (request.getDescription() != null && !request.getDescription().trim().isEmpty()) {
                product.setDescription(request.getDescription());
            }
            if (request.getCategoryId() != null) {
                product.setCategory(categoryRepository.findById(request.getCategoryId())
                        .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND)));
            }
            productRepository.save(product);
            ProductResponse response = productMapper.toDto(product);
            return new ResponseObject<>(HttpStatus.OK.value(), "Updated product successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to add product", null);
        }
    }

    public ResponseObject<Object> deleteProductById(int id) {
        try {
            Product product = productRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
            productRepository.delete(product);
            return new ResponseObject<>(HttpStatus.OK.value(), "Deleted product successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete product", null);
        }
    }

    public ResponseObject<Object> deleteAllProducts() {
        try {

            List<Product> list = new ArrayList<>(productRepository.findAll());
            if (list.isEmpty()) {
                throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
            }
            productRepository.deleteAll();
            return new ResponseObject<>(HttpStatus.OK.value(), "Deleted all products successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete product", null);
        }
    }

    // This function is for Admin choose products to export
    // This func also has available quantity of each product in a warehouse then we can check if Admin choose more than available quantity in front end
    public ResponseObject<List<ProductListForExportResponse>> getAllProductsByWarehouseId(
            Integer warehouseId
    ) {
        // Get all product by warehouseId
        // Get available quanity of each product by (available quantity = total quantity - quantity in export)
        try {
            List<Product> products = productRepository.getAllProductsByWarehouseIdWithFilters(warehouseId);

            List<ProductListForExportResponse> responses = products.stream().map(
                    product -> {
                        int totalAvailableQuantity = inventoryService.getAvailableQuantityOfProduct(warehouseId, product.getId());
                        ProductListForExportResponse response = new ProductListForExportResponse();
                        response.setId(product.getId());
                        response.setName(product.getName());
                        response.setDescription(product.getDescription());
                        response.setCategory(categoryMapper.toDto(product.getCategory()));
                        response.setAvailableQuantity(totalAvailableQuantity);
                        return response;
                    }
            ).toList();
            return new ResponseObject<>(HttpStatus.OK.value(), "Get all products by warehouseId successfully", responses);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get all products by warehouseId", null);
        }
    }
    public Integer getTotalProductsForAutoSelect(Integer warehouseId, Integer categoryId, String search) {
        return productRepository.getTotalProductsForAutoSelect(warehouseId, categoryId, search);
    }
}
