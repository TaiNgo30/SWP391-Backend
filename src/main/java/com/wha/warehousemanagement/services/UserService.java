package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.requests.UserRequest;
import com.wha.warehousemanagement.dtos.responses.UserResponse;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.mappers.UserMapper;
import com.wha.warehousemanagement.models.*;
import com.wha.warehousemanagement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.wha.warehousemanagement.repositories.WarehouseRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final WarehouseRepository warehouseRepository;

    public ResponseObject<?> getAllUsers() {
        try {
            List<UserResponse> responses = userRepository.findAll()
                    .stream().map(userMapper::toDto)
                    .collect(Collectors.toList());
            return new ResponseObject<>(HttpStatus.OK.value(), "Get all users successfully", responses);
        } catch (CustomException e) {
            return new ResponseObject<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed get users details", null);
        }
    }

    public ResponseObject<?> getUserById(int id) {
        try {
            UserResponse response = userRepository.findById(id)
                    .map(userMapper::toDto)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            return new ResponseObject<>(HttpStatus.OK.value(), "Get user by id successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed get user by id", null);
        }
    }

    public ResponseObject<?> updateUser(int id, UserRequest request) {
        try {
            User user = userRepository.findById(id).orElseThrow(
                    () -> new CustomException(ErrorCode.USER_NOT_FOUND)
            );

            if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
                user.setFullName(request.getFullName());
            }
            if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
                user.setUsername(request.getUsername());
            }
            if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
                user.setPassword(request.getPassword());
            }
            if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
                user.setEmail(request.getEmail());
            }
            if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
                user.setPhone(request.getPhone());
            }
            if (request.getAddress() != null && !request.getAddress().trim().isEmpty()) {
                user.setAddress(request.getAddress());
            }
            if (request.getRole() != null && !request.getRole().trim().isEmpty()) {
                user.setRole(Role.valueOf(request.getRole()));
            }
            user.setWarehouse(warehouseRepository.findById(request.getWarehouseId()).orElseThrow(() -> new CustomException(ErrorCode.WAREHOUSE_NOT_FOUND) ));
            userRepository.save(user);
            UserResponse response = userMapper.toDto(user);
            return new ResponseObject<>(HttpStatus.OK.value(), "Update user successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed update user", null);
        }
    }


    public ResponseObject<?> deleteUserById(int id) {
        try {
            User user = userRepository.findById(id).orElseThrow(
                    () -> new CustomException(ErrorCode.USER_NOT_FOUND)
            );
            userRepository.delete(user);
            return new ResponseObject<>(HttpStatus.OK.value(), "Delete user successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed delete user", null);
        }
    }
}
