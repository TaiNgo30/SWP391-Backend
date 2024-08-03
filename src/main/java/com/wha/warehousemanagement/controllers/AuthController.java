package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.TokenDTO;
import com.wha.warehousemanagement.dtos.requests.UserLoginRequest;
import com.wha.warehousemanagement.dtos.requests.UserSignUpRequest;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseObject<Object>> signUp(@RequestBody UserSignUpRequest request) {
        return ResponseEntity.ok(authService.signUp(request));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseObject<TokenDTO>> login(@RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseObject<Object>> refreshToken(@RequestBody String refreshTokenRequest) {
        return ResponseEntity.ok(authService.refreshToken(refreshTokenRequest));
    }
}
