package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.requests.ZoneRequest;
import com.wha.warehousemanagement.dtos.responses.ZoneResponse;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.mappers.ZoneMapper;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.models.Warehouse;
import com.wha.warehousemanagement.models.Zone;
import com.wha.warehousemanagement.repositories.WarehouseRepository;
import com.wha.warehousemanagement.repositories.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ZoneService {
    private final ZoneRepository zoneRepository;
    private final ZoneMapper zoneMapper;
    private final WarehouseRepository warehouseRepository;
    public ResponseObject<?> getAllZones() {
        try{
            List<ZoneResponse> responses = zoneRepository.findAll()
                    .stream().map(zoneMapper::toDto).toList();
            if (responses.isEmpty())
                throw new CustomException(ErrorCode.ZONE_NOT_FOUND);
            return new ResponseObject<>(HttpStatus.OK.value(), "All zones fetched successfully", responses);
        }catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get all zones", null);
        }
    }

    public ResponseObject<?> getZoneById(int id) {
        try{
            ZoneResponse response = zoneRepository.findById(id)
                    .map(zoneMapper::toDto)
                    .orElseThrow(() -> new CustomException(ErrorCode.ZONE_NOT_FOUND));
            return new ResponseObject<>(HttpStatus.OK.value(), "All zones fetched successfully", response);
        }catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get all zones", null);
        }
    }

    public ResponseObject<?> addZone(ZoneRequest request){
        try {
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                throw new CustomException(ErrorCode.ZONE_NAME_BLANK);
            }
            //them exist trong warehouseId
            if (zoneRepository.existsByName(request.getName())) {
                throw new CustomException(ErrorCode.ZONE_ALREADY_EXISTS);
            }
            Zone newZone = new Zone();
            newZone.setName(request.getName());
            newZone.setDescription(request.getDescription());
            newZone.setZoneStatus(request.getZoneStatus());
            Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                    .orElseThrow(() -> new CustomException(ErrorCode.WAREHOUSE_NOT_FOUND));
            newZone.setWarehouse(warehouse);
            zoneRepository.save(newZone);
            ZoneResponse response = zoneMapper.toDto(newZone);
            return new ResponseObject<>(HttpStatus.OK.value(), "Zone added successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to add zone", null);
        }
    }

    public ResponseObject<?> updateZoneById(int id, ZoneRequest request) {
        try {
            Zone zone = zoneRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.ZONE_NOT_FOUND));
//            if (request.getName() == null || request.getName().trim().isEmpty()) {
//                throw new CustomException(ErrorCode.ZONE_NAME_BLANK);
//            }
//            if (zoneRepository.existsByName(request.getName())) {
//                throw new CustomException(ErrorCode.ZONE_ALREADY_EXISTS);
//            }
            zone.setName(request.getName());
            zone.setDescription(request.getDescription());
            zone.setZoneStatus(request.getZoneStatus());
            zoneRepository.save(zone);
            ZoneResponse response = zoneMapper.toDto(zone);
            return new ResponseObject<>(HttpStatus.OK.value(), "Zone updated successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update zone", null);
        }
    }

    public ResponseObject<?> deleteZoneById(int id) {
        try {
            Zone zone = zoneRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.ZONE_NOT_FOUND));
            zoneRepository.delete(zone);
            return new ResponseObject<>(HttpStatus.OK.value(), "Zone deleted successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete zone", null);
        }
    }

    public ResponseObject<?> getZonesByWarehouseId (int warehouseId) {
        try {
            List<ZoneResponse> responses = zoneRepository.findAllByWarehouse_Id(warehouseId)
                    .stream().map(zoneMapper::toDto).toList();
            if (responses.isEmpty())
                throw new CustomException(ErrorCode.ZONE_NOT_FOUND);
            return new ResponseObject<>(HttpStatus.OK.value(), "All zones fetched successfully", responses);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get all zones", null);
        }
    }

}
