package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.requests.ImportDetailRequest;
import com.wha.warehousemanagement.dtos.responses.ExportDetailResponse;
import com.wha.warehousemanagement.dtos.responses.ImportDetailResponse;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.mappers.ExportMapper;
import com.wha.warehousemanagement.mappers.ImportDetailMapper;
import com.wha.warehousemanagement.mappers.ImportMapper;
import com.wha.warehousemanagement.mappers.ProductMapper;
import com.wha.warehousemanagement.models.*;
import com.wha.warehousemanagement.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImportDetailService {
    private final ImportDetailRepository importDetailRepository;
    private final ImportDetailMapper importDetailMapper;
    private final ImportMapper importMapper;
    private final ImportRepository importRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ZoneRepository zoneRepository;
    private final InventoryRepository inventoryRepository;

    public ResponseObject<?> getAllImportDetails() {
        try {
            List<ImportDetailResponse> responses = importDetailRepository.findAll()
                    .stream()
                    .map(imp -> {
                        ImportDetailResponse response = importDetailMapper.toDto(imp);
                        response.setProduct(productMapper.toDto(imp.getProduct()));
                        return response;
                    })
                    .collect(Collectors.toList());
            return new ResponseObject<>(HttpStatus.OK.value(), "Import details retrieved successfully", responses);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get all import details", null);
        }
    }

    public ResponseObject<?> getImportDetailById(Integer id) {
        try {
            ImportDetailResponse response = importDetailRepository.findById(id)
                    .map(imp -> {
                        ImportDetailResponse importDetailResponse = importDetailMapper.toDto(imp);
                        importDetailResponse.setProduct(productMapper.toDto(imp.getProduct()));
                        return importDetailResponse;
                    })
                    .orElseThrow(() -> new CustomException(ErrorCode.IMPORT_DETAIL_NOT_FOUND));
            return new ResponseObject<>(HttpStatus.OK.value(), "Import details retrieved successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get import details by id", null);
        }
    }

    public ResponseObject<?> createImportDetail(List<ImportDetailRequest> requests) {
        try {
            List<ImportDetail> importDetails = new ArrayList<>();
            for (ImportDetailRequest request : requests) {
                ImportDetail importDetail = new ImportDetail();
                importDetail = update(importDetail, request);
                importDetails.add(importDetail);

                // Update or create inventory
                Product product = productRepository.findById(request.getProductId())
                        .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
                Zone zone = zoneRepository.findById(request.getZoneId())
                        .orElseThrow(() -> new CustomException(ErrorCode.ZONE_NOT_FOUND));

                Inventory inventory = inventoryRepository.findByProductIdAndZoneIdAndExpiredAt(
                                request.getProductId(), request.getZoneId(), request.getExpiredAt())
                        .orElse(new Inventory(null, product, zone, 0, request.getExpiredAt()));

                // Update quantity
                inventory.setQuantity(inventory.getQuantity() + request.getQuantity());

                inventoryRepository.save(inventory);
            }
            importDetailRepository.saveAll(importDetails);
            return new ResponseObject<>(HttpStatus.OK.value(), "Import details created successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to create import details", null);
        }
    }

    private ImportDetail update(ImportDetail importDetail, ImportDetailRequest request) {
        Import anImport = importRepository.findById(request.getImportId())
                .orElseThrow(() -> new CustomException(ErrorCode.IMPORT_NOT_FOUND));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        importDetail.setAnImport(anImport);
        importDetail.setProduct(product);
        importDetail.setQuantity(request.getQuantity());
        importDetail.setExpiredAt(request.getExpiredAt());
        return importDetail;
    }

    public ResponseObject<?> updateImportDetail(int id, ImportDetailRequest request) {
        try {
            ImportDetail importDetail = importDetailRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.IMPORT_DETAIL_NOT_FOUND));
            importDetailRepository.save(update(importDetail, request));
            ImportDetailResponse response = importDetailMapper.toDto(importDetail);
            return new ResponseObject<>(HttpStatus.OK.value(), "Import details updated successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update import details", null);
        }
    }

    public ResponseObject<?> deleteImportDetail(int id) {
        try {
            ImportDetail importDetail = importDetailRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.IMPORT_DETAIL_NOT_FOUND));
            importDetailRepository.delete(importDetail);
            return new ResponseObject<>(HttpStatus.OK.value(), "Import details deleted successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete import details", null);
        }
    }

    public ResponseObject<?> getImportDetailsByImportId(Integer importId) {
        try {
            List<ImportDetailResponse> responses = importDetailRepository.findByAnImport_Id(importId)
                    .stream()
                    .map(imp -> {
                        ImportDetailResponse response = importDetailMapper.toDto(imp);
                        response.setImportResponse(importMapper.toDto(imp.getAnImport()));
                        response.setProduct(productMapper.toDto(imp.getProduct()));
                        return response;
                    })
                    .toList();
            return new ResponseObject<>(HttpStatus.OK.value(), "Import details retrieved successfully", responses);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get import details", null);
        }
    }
}
