package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.requests.ImportRequest;
import com.wha.warehousemanagement.dtos.requests.ImportUpdateRequest;
import com.wha.warehousemanagement.dtos.responses.CustomerResponse;
import com.wha.warehousemanagement.dtos.responses.ExportResponse;
import com.wha.warehousemanagement.dtos.responses.ImportDetailResponse;
import com.wha.warehousemanagement.dtos.responses.ImportResponse;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.mappers.CustomerMapper;
import com.wha.warehousemanagement.mappers.ImportDetailMapper;
import com.wha.warehousemanagement.mappers.ImportMapper;
import com.wha.warehousemanagement.models.*;
import com.wha.warehousemanagement.repositories.CustomerRepository;
import com.wha.warehousemanagement.repositories.ImportDetailRepository;
import com.wha.warehousemanagement.repositories.ImportRepository;
import com.wha.warehousemanagement.repositories.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImportService {

    private final ImportRepository importRepository;
    private final ImportMapper importMapper;
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final ImportDetailRepository importDetailRepository;
    private final ImportDetailMapper importDetailMapper;
    private final WarehouseRepository warehouseRepository;

    public ResponseObject<?> addImport(ImportRequest request) {
        try {
            Import anImport = new Import();
            anImport.setDescription(request.getDescription());
            anImport.setStatus(Status.valueOf(request.getStatus()));
            anImport.setImportType(ImportExportType.valueOf(request.getImportType()));
            anImport.setReceivedDate(new Date());

            if (request.getTransferKey() != null && !request.getTransferKey().trim().isEmpty()) {
                anImport.setTransferKey(request.getTransferKey());
            }

            // Handle warehouseFrom
            if (request.getWarehouseIdFrom() != null) {
                anImport.setWarehouseFrom(warehouseRepository.findById(request.getWarehouseIdFrom())
                        .orElseThrow(() -> new CustomException(ErrorCode.WAREHOUSE_NOT_FOUND)));
            }

            // Handle warehouseTo
            if (request.getWarehouseIdTo() != null) {
                anImport.setWarehouseTo(warehouseRepository.findById(request.getWarehouseIdTo())
                        .orElseThrow(() -> new CustomException(ErrorCode.WAREHOUSE_NOT_FOUND)));
            }

            // Handle warehouseTo
            if (request.getCustomerId() != null) {
                anImport.setCustomer(customerRepository.findById(request.getCustomerId())
                        .orElseThrow(() -> new CustomException(ErrorCode.CUSTOMER_NOT_FOUND)));
            }

            importRepository.save(anImport);
            ImportResponse response = importMapper.toDto(anImport);
            return new ResponseObject<>(HttpStatus.OK.value(), "Import added successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        }
        catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to add import", null);
        }
    }

    public ResponseObject<List<ImportResponse>> getAllImports(
            Integer warehouseId, Integer pageNo, Integer limit, String sortBy, String direction, String search
    ) {
        try {
            Pageable pageable;
            Page<Import> imports;
                Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
                pageable = PageRequest.of(pageNo, limit, sortDirection, sortBy);
                imports = importRepository.findAllImportsByWarehouseWithDefaultSort(warehouseId, search, pageable);
            List<ImportResponse> responses = importMapper.toDto(imports.getContent());
            return new ResponseObject<>(HttpStatus.OK.value(), "Get all imports successfully", responses);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get all imports", null);
        }
    }

    public int getTotalImportsByWarehouse(Integer warehouseId, Status status, String search) {
            return importRepository.countImportsByWarehouseIdAndStatus(warehouseId, status, search);
    }

    public ResponseObject<?> getImportById(int id) {
        try {
            ImportResponse response = importRepository.findById(id)
                    .map(anImport -> {
                        ImportResponse importResponse = importMapper.toDto(anImport);

                        List<ImportDetailResponse> importDetailResponses = importDetailRepository
                                .findAllByAnImport_Id(anImport.getId())
                                .stream().map(importDetailMapper::toDto).toList();

                        CustomerResponse customerResponse = customerMapper.toDto(anImport.getCustomer());
                        importResponse.setCustomer(customerResponse);
                        return importResponse;
                    })
                    .orElseThrow(() -> new CustomException(ErrorCode.IMPORT_NOT_FOUND));
            return new ResponseObject<>(HttpStatus.OK.value(), "Get import by id successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get import by id", null);
        }
    }

    public ResponseObject<?> updateImport(Integer id, ImportUpdateRequest request) {
        try {
            Import anImport = importRepository.getImportById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.IMPORT_NOT_FOUND));
            if (request.getDescription() != null && !request.getDescription().trim().isEmpty()) {
                anImport.setDescription(request.getDescription());
            }
            if (request.getImportType() != null && !request.getImportType().trim().isEmpty()) {
                anImport.setImportType(ImportExportType.valueOf(request.getImportType()));
            }
            if (request.getReceivedDate() != null) {
                anImport.setReceivedDate(request.getReceivedDate());
            }
            if (request.getCustomerId() != null) {
                anImport.setCustomer(customerRepository.findById(request.getCustomerId())
                        .orElseThrow(() -> new CustomException(ErrorCode.CUSTOMER_NOT_FOUND)));
            }
            importRepository.save(anImport);
            ImportResponse response = importMapper.toDto(anImport);
            return new ResponseObject<>(HttpStatus.OK.value(), "Updated import successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update import", null);
        }
    }

    public ResponseObject<?> deleteImportById(int id) {
        try {
            Import anImport = importRepository.getImportById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.IMPORT_NOT_FOUND));
            importRepository.delete(anImport);
            return new ResponseObject<>(HttpStatus.OK.value(), "Deleted import successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete import", null);
        }
    }

    public ResponseObject<?> deleteAllImports() {
        try {
            List<Import> list = importRepository.findAll();
            if (!list.isEmpty()) {
                importRepository.deleteAll();
                return new ResponseObject<>(HttpStatus.OK.value(), "Deleted all imports successfully", null);
            } else {
                return new ResponseObject<>(HttpStatus.NO_CONTENT.value(), "No imports to delete", null);
            }
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete imports", null);
        }
    }

//    public ResponseObject<?> getAllImports(int page, int limit) {
//        try {
//            PageRequest pageable = PageRequest.of(page, limit);
//            List<ImportResponse> response = importRepository.findAllImports(pageable)
//                    .stream()
//                    .map(importMapper::toDto)
//                    .collect(Collectors.toList());
//            return new ResponseObject<>(HttpStatus.OK.value(), "Imports retrieved successfully", response);
//        } catch (CustomException e) {
//            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
//        } catch (Exception e) {
//            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get all imports", null);
//        }
//    }
public ResponseObject<?> getAllImports() {
    try {
        List<ImportResponse> response = importRepository.findAll()
                .stream()
                .map(importMapper::toDto)
                .collect(Collectors.toList());
        return new ResponseObject<>(HttpStatus.OK.value(), "Imports retrieved successfully", response);
    } catch (CustomException e) {
        return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
    } catch (Exception e) {
        return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get all imports", null);
    }
}
    public ResponseObject<?> getTotalImports() {
        try {
            Long totalImport = importRepository.countAllImports();
            return new ResponseObject<>(HttpStatus.OK.value(), "Total import retrieved successfully", totalImport);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get total import", null);
        }
    }

    public ResponseObject<?> getLatestImportByWarehouseId(Integer warehouseId) {
        try {
            Warehouse warehouseTo = warehouseRepository.findById(warehouseId)
                    .orElseThrow(() -> new CustomException(ErrorCode.WAREHOUSE_NOT_FOUND));
            Import anImport = importRepository.findFirstByWarehouseToOrderByIdDesc(warehouseTo);
            ImportResponse response = importMapper.toDto(anImport);
            return new ResponseObject<>(HttpStatus.OK.value(), "Get latest import by warehouse id successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get latest import by warehouse id", null);
        }
    }
}
