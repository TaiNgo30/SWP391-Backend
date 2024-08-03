package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.TokenDTO;
import com.wha.warehousemanagement.dtos.requests.UserLoginRequest;
import com.wha.warehousemanagement.dtos.requests.UserSignUpRequest;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.mappers.WarehouseMapper;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.models.User;
import com.wha.warehousemanagement.models.Warehouse;
import com.wha.warehousemanagement.repositories.UserRepository;
import com.wha.warehousemanagement.repositories.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final JWTUtils jwtUtils;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final WarehouseMapper warehouseMapper;
    private final WarehouseRepository warehouseRepository;

    public ResponseObject<Object> signUp(UserSignUpRequest request) {
        try {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
            }
            if (request.getPassword().length() < 8) {
                throw new CustomException(ErrorCode.PASSWORD_TOO_SHORT);
            }
            Optional<Warehouse> warehouse = warehouseRepository.findById(request.getWarehouseId());

            if (warehouse.isEmpty()) {
                throw new CustomException(ErrorCode.PASSWORD_TOO_SHORT);
            }
            User user = new User
                    (
                            request.getFullName(),
                            request.getUsername(),
                            passwordEncoder.encode(request.getPassword()),
                            request.getEmail(),
                            request.getPhone(),
                            request.getAddress(),
                            request.getRole(),
                            warehouse.get()
                    );
            User userResult = userRepository.save(user);
            return new ResponseObject<>(HttpStatus.OK.value(), "User signed up successfully", userResult);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "User signed up UN-successfully", null);
        }
    }


    public ResponseObject<TokenDTO> login(UserLoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getUsername(), request.getPassword()
            ));
            User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
            String jwt = jwtUtils.generateToken(user);
            String refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            TokenDTO tokenData = new TokenDTO();
            tokenData.setUsername(request.getUsername());
            tokenData.setUserId(user.getId());
            tokenData.setWarehouseId(user.getWarehouse().getId());
            tokenData.setToken(jwt);
            tokenData.setRefreshToken(refreshToken);
            tokenData.setExpirationTime("24h");
            return new ResponseObject<>(HttpStatus.OK.value(), "Login successfully", tokenData);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Invalid username or password", null);
        }
    }

    public ResponseObject<Object> refreshToken(String refreshTokenRequest) {
        try {
            TokenDTO tokenResponse = new TokenDTO();
            String ourUsername = jwtUtils.extractUsername(refreshTokenRequest);
            User users = userRepository.findByUsername(ourUsername).orElseThrow();
            if (jwtUtils.isTokenValid(refreshTokenRequest, users)) {
                var jwt = jwtUtils.generateToken(users);
                tokenResponse.setToken(jwt);
                tokenResponse.setRefreshToken(refreshTokenRequest);
                tokenResponse.setExpirationTime("24h");
            } else {
                return new ResponseObject<>(400, "Invalid refresh token", null);
            }
            return new ResponseObject<>(200, "Successfully refreshed in", null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Invalid refresh token", null);
        }
    }

}
