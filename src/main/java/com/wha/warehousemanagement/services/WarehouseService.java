package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.requests.WarehouseRequest;
import com.wha.warehousemanagement.dtos.responses.WarehouseResponse;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.mappers.WarehouseMapper;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.models.Warehouse;
import com.wha.warehousemanagement.repositories.UserRepository;
import com.wha.warehousemanagement.repositories.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final UserRepository userRepository;

    public ResponseObject<?> getAllWarehouses() {
        try {
            List<WarehouseResponse> responses = warehouseRepository.findAll()
                    .stream().map(warehouseMapper::toDto)
                    .collect(Collectors.toList());
            return new ResponseObject<>(HttpStatus.OK.value(), "Warehouses fetched successfully", responses);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to fetch warehouses", null);
        }
    }

    public ResponseObject<?> getWarehouseById(int id) {
        try {
            WarehouseResponse response = warehouseRepository.findById(id)
                    .map(warehouseMapper::toDto)
                    .orElseThrow(() -> new CustomException(ErrorCode.WAREHOUSE_NOT_FOUND));
            return new ResponseObject<>(HttpStatus.OK.value(), "Warehouse fetched successfully", response);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to fetch warehouse", null);
        }
    }

    public ResponseObject<?> addWarehouse(WarehouseRequest request) {
        try {
            if (warehouseRepository.existsByName(request.getName())) {
                throw new CustomException(ErrorCode.WAREHOUSE_NAME_EXISTS);
            }
            if (warehouseRepository.existsByAddress(request.getAddress())) {
                throw new CustomException(ErrorCode.WAREHOUSE_ADDRESS_EXISTS);
            }
            Warehouse warehouse = new Warehouse();
            warehouse.setName(request.getName());
            warehouse.setDescription(request.getDescription());
            warehouse.setAddress(request.getAddress());
            warehouse.setCreatedAt(new Date());
            warehouseRepository.save(warehouse);
            WarehouseResponse response = warehouseMapper.toDto(warehouseRepository.findAll().get(warehouseRepository.findAll().size() - 1));
            return new ResponseObject<>(HttpStatus.OK.value(), "Warehouse added successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to add warehouse", null);
        }
    }

    public ResponseObject<?> updateWarehouseById(int id, WarehouseRequest request) {
        try {
            if (!warehouseRepository.existsById(id)) {
                throw new CustomException(ErrorCode.WAREHOUSE_NOT_FOUND);
            }
//            if (warehouseRepository.existsByName(request.getName())) {
//                throw new CustomException(ErrorCode.WAREHOUSE_NAME_EXISTS);
//            }
//            if (warehouseRepository.existsByAddress(request.getAddress())) {
//                throw new CustomException(ErrorCode.WAREHOUSE_ADDRESS_EXISTS);
//            }
            Warehouse warehouse = warehouseRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.WAREHOUSE_NOT_FOUND));
            if (request.getName() != null && !request.getName().trim().isEmpty()) {
                warehouse.setName(request.getName());
            }
            if (request.getDescription() != null && !request.getDescription().trim().isEmpty()) {
                warehouse.setDescription(request.getDescription());
            }
            if (request.getAddress() != null && !request.getAddress().trim().isEmpty()) {
                warehouse.setAddress(request.getAddress());
            }
            warehouseRepository.save(warehouse);
            WarehouseResponse response = warehouseMapper.toDto(warehouse);
            return new ResponseObject<>(HttpStatus.OK.value(), "Warehouse updated successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update warehouse", null);
        }
    }

    public ResponseObject<Object> deleteWarehouseById(int id) {
        try {
            if (!warehouseRepository.existsById(id)) {
                throw new CustomException(ErrorCode.WAREHOUSE_NOT_FOUND);
            }

            Warehouse warehouse = warehouseRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.WAREHOUSE_NOT_FOUND));

            boolean hasInventories = warehouse.getZones().stream()
                    .anyMatch(zone -> !zone.getInventories().isEmpty());

            if (hasInventories) {
                throw new CustomException(ErrorCode.WAREHOUSE_NOT_EMPTY);
            }

            warehouseRepository.deleteById(id);
            return new ResponseObject<>(HttpStatus.OK.value(), "Warehouse deleted successfully", null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete warehouse", null);
        }
    }

    public ResponseObject<?> getWarehousesByUserId(int id) {
        try {
            if (!userRepository.existsById(id)) {
                throw new CustomException(ErrorCode.USER_NOT_FOUND);
            }
            WarehouseResponse response = warehouseMapper.toDto(warehouseRepository.findByUserId(id));
            return new ResponseObject<>(HttpStatus.OK.value(), "Warehouse fetched successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update warehouse", null);
        }
    }

}
