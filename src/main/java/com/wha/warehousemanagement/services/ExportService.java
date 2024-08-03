package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.requests.ExportRequest;
import com.wha.warehousemanagement.dtos.requests.ExportTransferRequest;
import com.wha.warehousemanagement.dtos.requests.ExportUpdateRequest;
import com.wha.warehousemanagement.dtos.responses.ExportByAdminReqResponse;
import com.wha.warehousemanagement.dtos.responses.ExportResponse;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.mappers.ExportMapper;
import com.wha.warehousemanagement.mappers.WarehouseMapper;
import com.wha.warehousemanagement.models.*;
import com.wha.warehousemanagement.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final ExportRepository exportRepository;
    private final ExportMapper exportMapper;
    private final ExportDetailService exportDetailService;
    private final WarehouseRepository warehouseRepository;
    private final CustomerRepository customerRepository;
    private final ImportRepository importRepository;
    private final WarehouseMapper warehouseMapper;
    private final InventoryRepository inventoryRepository;
    private final ExportDetailRepository exportDetailRepository;
    private final ImportDetailRepository importDetailRepository;
    private final ProductRepository productRepository;
    private final WebSocketService webSocketService;

    public ResponseObject<?> addExport(ExportRequest request) {
        try {
            Export export = new Export();
            export.setDescription(request.getDescription());
            export.setStatus(Status.valueOf(request.getStatus()));
            export.setExportDate(new Date());
            export.setExportType(ImportExportType.valueOf(request.getExportType()));

            if (request.getTransferKey() != null && !request.getTransferKey().trim().isEmpty()) {
                export.setTransferKey(request.getTransferKey());
            }

            // Handle warehouseFrom
            if (request.getWarehouseIdFrom() != null) {
                export.setWarehouseFrom(warehouseRepository.findById(request.getWarehouseIdFrom())
                        .orElseThrow(() -> new CustomException(ErrorCode.WAREHOUSE_NOT_FOUND)));
            }

            // Handle warehouseTo
            if (request.getWarehouseIdTo() != null) {
                export.setWarehouseTo(warehouseRepository.findById(request.getWarehouseIdTo())
                        .orElseThrow(() -> new CustomException(ErrorCode.WAREHOUSE_NOT_FOUND)));
            }

            // Handle customer
            if (request.getCustomerId() != null) {
                export.setCustomer(customerRepository.findById(request.getCustomerId())
                        .orElseThrow(() -> new CustomException(ErrorCode.PROVIDER_NOT_FOUND)));
            }

            exportRepository.save(export);

            if (export.getExportType() == ImportExportType.WAREHOUSE) {
                ExportNotification notification = new ExportNotification(export);
                webSocketService.sendNotification(notification, export.getWarehouseTo().getId());
            }

            ExportResponse response = exportMapper.toDto(export);
            return new ResponseObject<>(HttpStatus.OK.value(), "Export added successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        }
    }

    public ResponseObject<List<ExportResponse>> getAllExports(
            Integer warehouseId
    ) {
        try {
            List<Export> exports = exportRepository.findByWarehouseFromId(warehouseId);
            List<ExportResponse> responses = exportMapper.toDto(exports);
            return new ResponseObject<>(HttpStatus.OK.value(), "Get all exports successfully", responses);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get all exports", null);
        }
    }

    public ResponseObject<?> getAllExports() {
        try {
            List<ExportResponse> response =
                    exportRepository.findAll()
                            .stream()
                            .filter(export -> export.getWarehouseFrom() != null)
                            .map(exportMapper::toDto
                            )
                            .collect(Collectors.toList());
            return new ResponseObject<>(HttpStatus.OK.value(), "Inventories retrieved successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get all inventories", null);
        }
    }

    public ResponseObject<?> getExportById(int id) {
        try {
            Export export = exportRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.EXPORT_NOT_FOUND));
            ExportResponse response = exportMapper.toDto(export);
            return new ResponseObject<>(HttpStatus.OK.value(), "Get export by id successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get export by id", null);
        }
    }

    public ResponseObject<?> updateExport(int id, ExportUpdateRequest request) {
        try {
            Export export = exportRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.EXPORT_NOT_FOUND));

            if (request.getDescription() != null && !request.getDescription().trim().isEmpty())
                export.setDescription(request.getDescription());
            if (request.getType() != null && !request.getType().trim().isEmpty())
                export.setExportType(ImportExportType.valueOf(request.getType()));
            if (request.getExportDate() != null)
                export.setExportDate(request.getExportDate());

            if (export.getExportType() == ImportExportType.CUSTOMER || export.getExportType() == ImportExportType.WASTE) {
                if (request.getCustomerId() != null) {
                    Customer customer = customerRepository.findById(request.getCustomerId())
                            .orElseThrow(() -> new CustomException(ErrorCode.CUSTOMER_NOT_FOUND));
                    export.setCustomer(customer);
                    export.setWarehouseTo(null);
                } else {
                    throw new CustomException(ErrorCode.CUSTOMER_NOT_FOUND);
                }
            } else if (export.getExportType() == ImportExportType.WAREHOUSE) {
                if (request.getWarehouseIdTo() != null) {
                    Warehouse warehouseTo = warehouseRepository.findById(request.getWarehouseIdTo())
                            .orElseThrow(() -> new CustomException(ErrorCode.WAREHOUSE_NOT_FOUND));
                    export.setWarehouseTo(warehouseTo);
                    export.setCustomer(null);
                } else {
                    throw new CustomException(ErrorCode.WAREHOUSE_NOT_FOUND);
                }
            }
            exportRepository.save(export);
            return new ResponseObject<>(HttpStatus.OK.value(), "Updated export successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update export", null);
        }
    }

    public ResponseObject<?> updateExport(int id, ExportRequest request) {
        System.out.println("Export request: " + request.toString());
        try {
            Export export = exportRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.EXPORT_NOT_FOUND));

            if (request.getDescription() != null && !request.getDescription().trim().isEmpty())
                export.setDescription(request.getDescription());
            if (request.getStatus() != null && !request.getStatus().trim().isEmpty())
                export.setStatus(Status.valueOf(request.getStatus()));
            if (request.getExportDate() != null)
                export.setExportDate(java.sql.Date.valueOf(request.getExportDate()));
            if (request.getTransferKey() != null && !request.getTransferKey().trim().isEmpty())
                export.setTransferKey(request.getTransferKey());

            if (request.getExportType() != null && !request.getExportType().trim().isEmpty()) {
                export.setExportType(ImportExportType.valueOf(request.getExportType()));

            }
            if (request.getWarehouseIdTo() != null) {
                Warehouse warehouseTo = warehouseRepository.findById(request.getWarehouseIdTo())
                        .orElseThrow(() -> new CustomException(ErrorCode.WAREHOUSE_NOT_FOUND));

                export.setWarehouseTo(warehouseTo);
            }
            if (request.getWarehouseIdFrom() != null) {
                Warehouse warehouseFrom = warehouseRepository.findById(request.getWarehouseIdFrom())
                        .orElseThrow(() -> new CustomException(ErrorCode.WAREHOUSE_NOT_FOUND));

                export.setWarehouseFrom(warehouseFrom);
            }


            exportRepository.save(export);
            return new ResponseObject<>(HttpStatus.OK.value(), "Updated export successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update export", null);
        }
    }


    public ResponseObject<?> deleteExportById(int id) {
        try {
            Export export = exportRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
            exportRepository.delete(export);
            return new ResponseObject<>(HttpStatus.OK.value(), "Deleted export successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete export", null);
        }
    }

    public ResponseObject<?> deleteAllExports() {
        try {
            List<Export> list = exportRepository.findAll();
            if (!list.isEmpty()) {
                exportRepository.deleteAll();
                return new ResponseObject<>(HttpStatus.OK.value(), "Deleted all exports successfully", null);
            } else {
                return new ResponseObject<>(HttpStatus.NO_CONTENT.value(), "No exports to delete", null);
            }
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete exports", null);
        }
    }

//    public ResponseObject<List<ExportResponse>> searchExportDetails(
//            int page, int limit, String sortBy,String direction, int warehouseId, String exportDate, String customerName,
//            String customerAddress, String status) {
//        try {
//            if (direction == "asc") {
//                direction = "ASC";
//            } else {
//                direction = "DESC";
//            }
//            Sort.Direction sortDirection = Sort.Direction.fromString(direction.equals("asc") ? "ASC" : "DESC");
//            Pageable pageable = PageRequest.of(page, limit, Sort.by(sortDirection, sortBy));
//            List<ExportResponse> exports = exportMapper.toDto(exportRepository.searchExportDetails(
//                    warehouseId, exportDate, customerName, customerAddress, status, pageable).getContent());
//            return new ResponseObject<>(HttpStatus.OK.value(), "Search export details successfully", exports);
//        } catch (Exception e) {
//            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to search export details", null);
//        }
//    }

    // Dont need to check inventories by warehouseId because when the inventories on the monitor of a particular STAFF, it only shows the inventories of that warehouse
    @Transactional
    public ResponseObject<ExportByAdminReqResponse> createTransferBetweenWarehouses(ExportTransferRequest request) {
        Warehouse warehouseFrom = warehouseRepository.findById(request.getWarehouseFromId())
                .orElseThrow(() -> new CustomException(ErrorCode.WAREHOUSE_NOT_FOUND));
        Warehouse warehouseTo = warehouseRepository.findById(request.getWarehouseToId())
                .orElseThrow(() -> new CustomException(ErrorCode.WAREHOUSE_NOT_FOUND));

        // Tạo transferKey
        String transferKey = generateUniqueTransferKey();

        // Tạo Export
        Export export = new Export();

        try {
            export.setDescription(request.getDescription());
            export.setStatus(Status.PENDING);
            export.setExportDate(request.getExportDate());
            export.setExportType(ImportExportType.WAREHOUSE);
            export.setTransferKey(transferKey);
            export.setWarehouseFrom(warehouseFrom);
            export.setWarehouseTo(warehouseTo);

            exportRepository.save(export);
            for (Map.Entry<Integer, Integer> entry : request.getInventoriesSelected().entrySet()) {

                Inventory inventory = inventoryRepository.findById(entry.getKey())
                        .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));

                Product product = productRepository.findById(inventory.getProduct().getId())
                        .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

                if (inventory.getQuantity() < entry.getValue()) {
                    throw new CustomException(ErrorCode.OUT_OF_STOCK);
                }

                // Cập nhật số lượng trong kho
                inventory.setQuantity(inventory.getQuantity() - entry.getValue());
                inventoryRepository.save(inventory);

                ExportDetail exportDetail = new ExportDetail();
                exportDetail.setProduct(product);
                exportDetail.setQuantity(entry.getValue());
                exportDetail.setExport(export);
                exportDetail.setExpiredAt(inventory.getExpiredAt());
                exportDetailRepository.save(exportDetail);
            }
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to create EXPORT transfer between warehouses", null);
        }

        // Tạo Import tương ứng
        Import anImport = new Import();
        try {
            anImport.setDescription(request.getDescription());
            anImport.setStatus(Status.PENDING);
            anImport.setReceivedDate(request.getExportDate());
            anImport.setImportType(ImportExportType.WAREHOUSE);
            anImport.setTransferKey(transferKey);
            anImport.setWarehouseFrom(warehouseFrom);
            anImport.setWarehouseTo(warehouseTo);

            importRepository.save(anImport);
            for (Map.Entry<Integer, Integer> entry : request.getInventoriesSelected().entrySet()) {

                Inventory inventory = inventoryRepository.findById(entry.getKey())
                        .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));

                Product product = productRepository.findById(inventory.getProduct().getId())
                        .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

                ImportDetail importDetail = new ImportDetail();
                importDetail.setProduct(product);
                importDetail.setQuantity(entry.getValue());
                importDetail.setAnImport(anImport);
                importDetail.setExpiredAt(inventory.getExpiredAt());
                importDetailRepository.save(importDetail);
            }
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to create IMPORT transfer between warehouses", null);
        }

        // Tạo response
        ExportByAdminReqResponse response = new ExportByAdminReqResponse();
        response.setId(export.getId());
        response.setDescription(export.getDescription());
        response.setStatus(export.getStatus().toString());
        response.setExportDate(export.getExportDate());
        response.setExportType(export.getExportType().toString());
        response.setTransferKey(transferKey);
        response.setWarehouseFrom(warehouseMapper.toDto(warehouseFrom));
        response.setWarehouseTo(warehouseMapper.toDto(warehouseTo));
        response.setExportDetails(exportDetailService.getExportDetailWithExportIdByExportId(export.getId()));

        return new ResponseObject<>(HttpStatus.OK.value(), "Transfer from Warehouse " + warehouseFrom.getName() + " to Warehouse " + warehouseTo.getName() + " is created successfully", response);
    }


    private String generateUniqueTransferKey() {
        String transferKey;
        do {
            transferKey = UUID.randomUUID().toString();
        } while (exportRepository.existsByTransferKey(transferKey));
        return transferKey;
    }


//    private void reserveProducts(Integer warehouseId, Map<Integer, Integer> productsRequested) {
//        for (Map.Entry<Integer, Integer> entry : productsRequested.entrySet()) {
//            Integer productId = entry.getKey();
//            Integer quantityRequested = entry.getValue();
//            List<Inventory> inventories = inventoryRepository.findByProductIdAndWarehouseIdOrderByExpiredAtAsc(warehouseId, productId);
//
//            // Tổng số lượng hàng trong kho
//            int totalAvailableQuantity = inventories.stream().mapToInt(Inventory::getQuantity).sum();
//
//            // Nếu tổng số lượng hàng trong kho nhỏ hơn số lượng hàng cần xuất
//            if (totalAvailableQuantity < quantityRequested) {
//                throw new CustomException(ErrorCode.OUT_OF_QUANTITY);
//            }
//
//            int remainingQuantityToReserve = quantityRequested;
//
//            // Duyệt qua từng hàng trong kho để giữ số lượng hàng cần xuất
//            for (Inventory inventory : inventories) {
//                int availableQuantity = inventory.getQuantity();
//                if (availableQuantity >= remainingQuantityToReserve) {
//                    inventory.setHeldQuantity(inventory.getHeldQuantity() + remainingQuantityToReserve);
//                    inventoryRepository.save(inventory);
//                    break;
//                } else {
//                    inventory.setHeldQuantity(inventory.getHeldQuantity() + availableQuantity);
//                    remainingQuantityToReserve -= availableQuantity;
//                    inventoryRepository.save(inventory);
//                }
//            }
//        }
//    }

    // Do not need to check by warehouseId because when the inventories on the monitor of a particular STAFF, it only shows the inventories of that warehouse
    // So, the staff can only choose the products to export from the warehouse that he/she is managing
    // So do the PENDING status because staff can only process the PENDING exports
//    @Transactional
//    public ResponseObject<?> processExportRequestToTransfer(processExportByStaffRequest request) {
//        Export export = exportRepository.findById(request.getExportId())
//                .orElseThrow(() -> new CustomException(ErrorCode.EXPORT_NOT_FOUND));
//
//        if (export.getStatus() != Status.PENDING) {
//            throw new CustomException(ErrorCode.INVALID_STATUS_TO_EXPORT);
//        }
//
//        // Duyệt qua các sản phẩm và số lượng cần xuất
//        for (Map.Entry<Integer, Integer> entry : request.getSelectedInventories().entrySet()) {
//            Integer inventoryId = entry.getKey();
//            Integer quantityToExport = entry.getValue();
//
//            Inventory inventory = inventoryRepository.findById(inventoryId)
//                    .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
//
//            if (inventory.getQuantity() < quantityToExport) {
//                throw new CustomException(ErrorCode.OUT_OF_STOCK);
//            }
//
//            // Trừ số lượng trong inventory
//            inventory.setQuantity(inventory.getQuantity() - quantityToExport);
//            try {
//                inventoryRepository.save(inventory);
//            } catch (Exception e) {
//                return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update inventories", null);
//            }
//        }
//
//        // Cập nhật trạng thái của Export thành SHIPPING
//        export.setStatus(Status.SHIPPING);
//        try {
//            exportRepository.save(export);
//        } catch (Exception e) {
//            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update export", null);
//        }
//
//        try {
//            Import relatedImport = importRepository.findByTransferKey(export.getTransferKey());
//            relatedImport.setStatus(Status.SHIPPING);
//            importRepository.save(relatedImport);
//        } catch (Exception e) {
//            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update import", null);
//        }
//
//        return new ResponseObject<>(HttpStatus.OK.value(), "Export processed successfully", null);
//    }

    public int getTotalExportsByWarehouseIdAndFilterByStatus(Integer warehouseId, Status status, String search) {
        return exportRepository.countByWarehouseIdAndStatus(warehouseId, status, search);
    }

    public Page<Export> getAllExportsForAdmin(int pageNo, int limit, String sortBy, String direction, Status status) {
        Pageable pageable = PageRequest.of(pageNo, limit, Sort.by(Sort.Direction.fromString(direction), sortBy));
        return exportRepository.findAllExportsForAdmin(status, pageable);
    }

    public ResponseObject<?> approveExportPending(Integer exportId) {
        try {
            Export export = exportRepository.findById(exportId)
                    .orElseThrow(() -> new CustomException(ErrorCode.EXPORT_NOT_FOUND));

            if (export.getStatus() != Status.PENDING) {
                throw new CustomException(ErrorCode.INVALID_STATUS_TO_EXPORT);
            }

            List<ExportDetail> exportDetails = exportDetailRepository.findByExportId(export.getId());

            for (ExportDetail exportDetail : exportDetails) {
                Inventory inventory = inventoryRepository.findByExportDetail(
                        exportDetail.getProduct().getName(),
                        exportDetail.getExpiredAt(),
                        exportDetail.getZone().getId()
                );

                inventory.setQuantity(inventory.getQuantity() - exportDetail.getQuantity());
                inventoryRepository.save(inventory);
            }

            export.setStatus(Status.SHIPPING);
            exportRepository.save(export);

            return new ResponseObject<>(HttpStatus.OK.value(), "Approved export pending successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed at approveExportPending", null);
        }
    }

    public ResponseObject<?> confirmShippedSuccessfully(Integer exportid) {
        try {
            Export export = exportRepository.findById(exportid)
                    .orElseThrow(() -> new CustomException(ErrorCode.EXPORT_NOT_FOUND));
            export.setStatus(Status.SUCCEED);
            exportRepository.save(export);
            return new ResponseObject<>(HttpStatus.OK.value(), "Export shipped successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to ship export", null);
        }
    }

    public ResponseObject<?> getLatestExportByWarehouseId(Integer warehouseId) {
        try {
            Warehouse warehouseFrom = warehouseRepository.findById(warehouseId)
                    .orElseThrow(() -> new CustomException(ErrorCode.WAREHOUSE_NOT_FOUND));
            Export export = exportRepository.findFirstByWarehouseFromOrderByIdDesc(warehouseFrom);
            ExportResponse response = exportMapper.toDto(export);
            return new ResponseObject<>(HttpStatus.OK.value(), "Get latest export by warehouse id successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get latest export by warehouse id", null);
        }
    }

}
