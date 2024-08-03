package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.requests.*;
import com.wha.warehousemanagement.dtos.responses.InventoriesByAdminViewResponse;
import com.wha.warehousemanagement.dtos.responses.InventoryResponse;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.mappers.InventoryMapper;
import com.wha.warehousemanagement.mappers.ProductMapper;
import com.wha.warehousemanagement.models.*;
import com.wha.warehousemanagement.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;
    private final ProductRepository productRepository;
    private final ZoneRepository zoneRepository;
    private final SearchRepository searchRepository;
    private final ExportDetailRepository exportDetailRepository;

    public ResponseObject<?> getAllInventories() {
        try {
            List<InventoryResponse> response =
                    inventoryRepository.findAll()
                            .stream()
                            .map(inventoryMapper::toDto
                            )
                            .collect(Collectors.toList());
            return new ResponseObject<>(HttpStatus.OK.value(), "Inventories retrieved successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get all inventories", null);
        }
    }

//    public ResponseObject<?> getInventoryById(Integer id) {
//        try {
//            InventoryResponse response = inventoryRepository.findById(id)
//                .map(imp -> {
//                    InventoryResponse inventoryResponse = inventoryMapper.toDto(imp);
//                    InventoryResponse.builder()
//                            .product(productMapper.toDto(imp.getProduct()))
//                            .zoneName(imp.getZone().getName()).quantity().build();
//                    return inventoryResponse;
//                })
//                .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
//            return new ResponseObject<>(HttpStatus.OK.value(), "Inventory retrieved successfully", response);
//        } catch (CustomException e) {
//            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
//        } catch (Exception e) {
//            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get inventory", null);
//        }
//    }

    public ResponseObject<?> getInventoryById(Integer id) {
        try {
            Inventory inventory = inventoryRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
            InventoryResponse response = inventoryMapper.toDto(inventory);
            return new ResponseObject<>(HttpStatus.OK.value(), "Inventory retrieved successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get inventory", null);
        }
    }

    public ResponseObject<?> deleteInventoryById(Integer id) {
        try {
            inventoryRepository.deleteById(id);
            return new ResponseObject<>(HttpStatus.OK.value(), "Inventory deleted successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete inventory", null);
        }
    }

    public ResponseObject<?> updateInventory(int id, InventoryRequest request) {
        try {
            Inventory inventory = inventoryRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
            if (request.getProductId() != null) {
                inventory.setProduct(productRepository.findById(request.getProductId()).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)));
            }
            if (request.getQuantity() != null) {
                inventory.setQuantity(request.getQuantity());
            }
            if (request.getExpiredAt() != null) {
                inventory.setExpiredAt(request.getExpiredAt());
            }
            if (request.getZoneId() != null) {
                inventory.setZone(zoneRepository.findById(request.getZoneId()).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)));
            }
            inventoryRepository.save(inventory);
            InventoryResponse response = inventoryMapper.toDto(inventory);
            return new ResponseObject<>(HttpStatus.OK.value(), "Inventory updated successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update inventory", null);
        }
    }

    public ResponseObject<?> addInventory(InventoryRequest request) {
        try {
            Inventory inventory = new Inventory();
            if (request.getProductId() != null) {
                inventory.setProduct(productRepository.findById(request.getProductId()).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)));
            }
            if (request.getQuantity() != null) {
                inventory.setQuantity(request.getQuantity());
            }
            if (request.getExpiredAt() != null) {
                inventory.setExpiredAt(request.getExpiredAt());
            }
            if (request.getZoneId() != null) {
                inventory.setZone(zoneRepository.findById(request.getZoneId()).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)));
            }
            inventoryRepository.save(inventory);
            InventoryResponse response = inventoryMapper.toDto(inventory);
            return new ResponseObject<>(HttpStatus.OK.value(), "Inventory added successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to add inventory", null);
        }
    }

    public ResponseObject<?> getInventoryByWarehouseId(
            int pageNo, int limit, String sortBy, int warehouseId, String... search
    ) {
        try {
            return new ResponseObject<>(HttpStatus.OK.value(), "Inventories retrieved successfully", searchRepository.searchInventories(pageNo, limit, sortBy, warehouseId, search));
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get all inventories", null);
        }
    }

    public ResponseObject<?> getTotalProductByWarehouseIdFilter(int warehouseId, Integer categoryId, String zoneName) {
        try {
            Long totalProduct = inventoryRepository.countInventoriesByWarehouseIdAndCategoryId(warehouseId, categoryId, zoneName);
            return new ResponseObject<>(HttpStatus.OK.value(), "Total product retrieved successfully", totalProduct);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get total product", null);
        }
    }


    public ResponseObject<?> getTotalProductByWarehouseId(int warehouseId) {
        try {
            Long totalProduct = inventoryRepository.countInventoriesByWarehouseId(warehouseId);
            return new ResponseObject<>(HttpStatus.OK.value(), "Total product retrieved successfully", totalProduct);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get total product", null);
        }
    }


    //zones transfer
//    @Transactional
//    public void transferProductBetweenZones(int productId, int fromZoneId, int toZoneId, int quantity) {
//        // Giảm số lượng sản phẩm trong zone xuất phát
//        Inventory fromInventory = inventoryRepository.findByProductIdAndZoneId(productId, fromZoneId);
//        if (fromInventory == null || fromInventory.getQuantity() < quantity) {
//            throw new RuntimeException("Không đủ số lượng sản phẩm để chuyển");
//        }
//        fromInventory.setQuantity(fromInventory.getQuantity() - quantity);
//        inventoryRepository.save(fromInventory);
//
//        // Tăng số lượng sản phẩm trong zone đích
//        Inventory toInventory = inventoryRepository.findByProductIdAndZoneId(productId, toZoneId);
//        if (toInventory == null) {
//            Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
//            Zone zone = zoneRepository.findById(toZoneId).orElseThrow(() -> new RuntimeException("Zone not found"));
//
//            toInventory = new Inventory();
//            toInventory.setProduct(product);
//            toInventory.setZone(zone);
//            toInventory.setQuantity(0);
//        }
//        toInventory.setQuantity(toInventory.getQuantity() + quantity);
//        inventoryRepository.save(toInventory);
//    }

    @Transactional
    public void transferProducts(List<TransferRequest> transferRequests) {
        for (TransferRequest request : transferRequests) {
            transferProductBetweenZones(request.getProductId(), request.getFromZoneId(), request.getToZoneId(), request.getQuantity(), request.getExpiredAt());
        }
    }

    @Transactional
    public void transferProductBetweenZones(int productId, int fromZoneId, int toZoneId, int quantity, Date expiredAt) {
        // Giảm số lượng sản phẩm trong zone xuất phát
        Optional<Inventory> fromInventoryOpt = inventoryRepository.findByProductIdAndZoneIdAndExpiredAt(productId, fromZoneId, expiredAt);
        Inventory fromInventory = fromInventoryOpt.orElseThrow(() -> new RuntimeException("Quantity not available enough!"));

        if (fromInventory.getQuantity() < quantity) {
            throw new RuntimeException("Quantity not available enough!");
        }
        fromInventory.setQuantity(fromInventory.getQuantity() - quantity);
        inventoryRepository.save(fromInventory);

        // Kiểm tra tồn kho tại zone đích với cùng expiredAt
        Optional<Inventory> toInventoryOpt = inventoryRepository.findByProductIdAndZoneIdAndExpiredAt(productId, toZoneId, expiredAt);
        Inventory toInventory;
        if (toInventoryOpt.isPresent()) {
            // Nếu tìm thấy tồn kho với cùng expiredAt, cộng thêm số lượng
            toInventory = toInventoryOpt.get();
            toInventory.setQuantity(toInventory.getQuantity() + quantity);
        } else {
            // Nếu không tìm thấy tồn kho với cùng expiredAt, tạo mới một inventory
            Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
            Zone zone = zoneRepository.findById(toZoneId).orElseThrow(() -> new RuntimeException("Zone not found"));

            toInventory = new Inventory();
            toInventory.setProduct(product);
            toInventory.setZone(zone);
            toInventory.setQuantity(quantity);
            toInventory.setExpiredAt(expiredAt);
        }
        inventoryRepository.save(toInventory);
    }

    // find inventories by zone id
    public List<Inventory> getInventoryByZoneId(Integer zoneId) {
        return inventoryRepository.findByZoneId(zoneId);
    }
    //
    public ResponseObject<?> getInventoryByWarehouseIdWithFilters(
            int pageNo, int limit, int warehouseId,
            String productName, Integer categoryId, String zoneName,
            Integer quantityLow, Integer quantityHigh) {
        try {
            Pageable pageable = PageRequest.of(pageNo, limit);
            Page<Inventory> inventories = inventoryRepository.searchInventoriesWithFilters(
                    warehouseId, categoryId, zoneName, productName, quantityLow, quantityHigh, pageable);
            return new ResponseObject<>(HttpStatus.OK.value(), "Inventories retrieved successfully", inventories);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get all inventories", null);
        }
    }

    public ResponseObject<?> getTotalProductByWarehouseIdWithFilters(
            int warehouseId, Integer categoryId, String zoneName,
            String productName, Integer quantityLow, Integer quantityHigh) {
        try {
            Long totalProduct = inventoryRepository.countInventoriesWithFilters(
                    warehouseId, categoryId, zoneName, productName, quantityLow, quantityHigh);
            return new ResponseObject<>(HttpStatus.OK.value(), "Total product retrieved successfully", totalProduct);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get total product", null);
        }
    }

    public ResponseObject<Page<InventoryResponse>> getInventoriesByWarehouseIdWithFilters(
            Integer warehouseId, int pageNo, int pageSize, boolean includeExpired,
            boolean includeNearExpired, boolean includeValid) {
        try {
            Date currentDate = new Date();
            Date nearExpiredDate = new Date(currentDate.getTime() + (7 * 24 * 60 * 60 * 1000)); // 7 days from now
            Pageable pageable = PageRequest.of(pageNo - 1, pageSize); // Subtract 1 from pageNo

            Page<Inventory> inventories = inventoryRepository.findInventoriesByWarehouseIdWithFilters(
                    warehouseId, currentDate, nearExpiredDate, includeExpired, includeNearExpired, includeValid, pageable);
            Page<InventoryResponse> response = inventories.map(inventoryMapper::toDto);
            return new ResponseObject<>(HttpStatus.OK.value(), "Inventories retrieved successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get inventories", null);
        }
    }

    public ResponseObject<Page<InventoriesByAdminViewResponse>> getInventoryByWarehouseIdWithFiltersForAdmin(
            int pageNo, int limit, Integer warehouseId,
            String sortBy, String direction, Integer categoryId, Integer zoneId,
            String search
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNo, limit, sortDirection, sortBy);

        Page<Inventory> inventories = inventoryRepository.searchInventoriesForAdmin(pageable, warehouseId, categoryId, zoneId, search);

        Page<InventoriesByAdminViewResponse> response = inventories.map(
                inventory -> {
                    Product product = inventory.getProduct();
                    Category category = product.getCategory();
                    Zone zone = inventory.getZone();
                    int heldQuantity = exportDetailRepository.findTotalPendingQuantityByWarehouseAndProduct(warehouseId, product.getId());
                    return InventoriesByAdminViewResponse.builder()
                            .productName(product.getName())
                            .productDescription(product.getDescription())
                            .productCategory(category.getName())
                            .productQuantity(inventory.getQuantity())
                            .productExpiryDate(inventory.getExpiredAt())
                            .productZone(zone.getName())
                            .build();
                }
        );

        return new ResponseObject<>(HttpStatus.OK.value(), "Inventories retrieved successfully", response);
    }

    // This function should be called to check when Admin input a product with quantiy for export
    public Integer getAvailableQuantityOfProduct(Integer warehouseId, Integer productId) {
        return inventoryRepository.findTotalQuantityByWarehouseAndProductId(warehouseId, productId);
    }

    public ResponseObject<?> checkAvailableQuantity(checkAvailableProductRequest request) {
        try {
            int availableQuantity = getAvailableQuantityOfProduct(request.getWarehouseId(), request.getProductId());
            if (availableQuantity < request.getQuantity()) {
                return new ResponseObject<>(HttpStatus.OK.value(), "Not enough quantity in this warehouse", null);
            }
            return new ResponseObject<>(HttpStatus.OK.value(), "Enough quantity", null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to check available quantity", null);
        }
    }

    public InventoryResponse searchInventoryByProductIdZoneIdAndExpiredAt(Integer productId, Integer zoneId, Date expiredAt) {
        Inventory inventory = inventoryRepository.findByProductIdAndZoneIdAndExpiredAt(productId, zoneId, expiredAt)
                .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
        return inventoryMapper.toDto(inventory);
    }

    public List<Inventory> getInventoryByWarehouseId(Integer warehouseId) {
        return inventoryRepository.findByZoneWarehouseId(warehouseId);
    }

    public ResponseObject<?> getInventoryByProductId(Integer productId, Integer warehouseId) {
        try {
            List<Inventory> inventories = inventoryRepository.findByProductIdandWarehouseId(productId, warehouseId);
            List<InventoryResponse> response = inventories.stream().map(inventoryMapper::toDto).toList();
            return new ResponseObject<>(HttpStatus.OK.value(), "Inventories retrieved successfully", response);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get inventories", null);
        }
    }

    public ResponseObject<?> getInventoryByProductIdAndZoneIdAndExpiredAt(InventoryByProductZoneExpiredDateReq request) {
        try {
            Inventory inventory;
            if (request.getExpiredAt() == null) {
                inventory = inventoryRepository.findByProductIdAndZoneIdAndExpiredAtIsNull(
                                request.getProductId(), request.getZoneId())
                        .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
            } else {
                inventory = inventoryRepository.findByProductIdAndZoneIdAndExpiredAt(
                                request.getProductId(), request.getZoneId(), request.getExpiredAt())
                        .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
            }
            InventoryResponse response = inventoryMapper.toDto(inventory);
            return new ResponseObject<>(HttpStatus.OK.value(), "Inventory retrieved successfully", response);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get inventory", null);
        }
    }
}
